package trains1846;

import genericDraft.MessagePublisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static trains1846.WaitType.DISCOUNT;
import static trains1846.WaitType.NORMAL;

/**
 * Created by micha on 6/7/2020.
 */
public class DraftMaster {
    private final MessagePublisher output;
    GameState state;

    public DraftMaster(MessagePublisher publisher46, int size, boolean excludeNewPrivates) {
        output = publisher46;
        primeGame(size, excludeNewPrivates);
    }

    private void primeGame(int size, boolean excludeNewPrivates) {
        //determine player order / priority deal
        state = new GameState(size);
        state.addToDeck(Private.playerCards(size));
        List<Private> removed = new ArrayList<>();
        List<Private> groupA = Private.groupA();
        List<Private> groupB = Private.groupB();
        if (excludeNewPrivates) {
            groupA.remove(Private.LITTLEM);
            groupB.remove(Private.BOOM);
            removed.add(Private.LITTLEM);
            removed.add(Private.BOOM);
        }
        removed.addAll(removeSome(size, groupA, 2 - size + groupA.size()));
        state.addToDeck(groupA);

        removed.addAll(removeSome(size, groupB, 2 - size + groupB.size()));
        state.addToDeck(groupB);

        output.publishToAll("Removed Privates: " + removed);

        List<Private> corps = Private.removableCorps();
        output.publishToAll("Removed Corporations: " + removeSome(size, corps, 5 - size));

        state.shuffleDeck();

        for (int i = 0; i < size; i++) {
            output.publishToPlayer("---Welcome to 1846 draft.---\n"+
                    "You are in seat #" + (size - i) + " of " + size + ".", i, false, true);
        }

        dealAndRequestNormalCard();
    }

    private List<Private> removeSome(int playerCount, List<Private> groupA, int numberToRemove) {
        Collections.shuffle(groupA);
        List<Private> ret = new ArrayList<>();
        for (int i = 0; i < numberToRemove; i++) {
            Private removed = groupA.remove(0);
            ret.add(removed);
        }
        return ret;
    }

    private void dealAndRequestNormalCard() {
        //Deal N+2 to active player WAIT
        List<Private> privates = state.dealHand();
        requestSelection(privates);
    }

    private void requestSelection(List<Private> privates) {
        StringBuilder bldr = new StringBuilder();
        stateAndPrompt(privates, bldr);
        output.publishToPlayer(bldr.toString(), state.getActivePlayer(), true, false);
        output.publishToAll(output.namePlayer(state.getActivePlayer()) + " is next to pick (check your DMs)");
        state.setWait(NORMAL);
    }

    private void stateAndPrompt(List<Private> privates, StringBuilder bldr) {
        bldr.append("You currently hold ").append(state.stateOfPlayer(state.getActivePlayer())).append("\nPlease make a selection:\n");
        int i = 0;
        for (Private pvt : privates) {
            bldr.append(i).append(") ").append(pvt.getPrettyName()).append(" for $").append(pvt.getCost() - state.getDiscount()).append("\n");
            i++;
        }
    }

    private boolean selectionMade(int selection) {
        //SELECTION recieve and proccess seleciton, shuffle unselected and add to bottom of deck
        if (state.buyIndex(selection)) {
            output.publishToPlayer("You now hold" + state.stateOfPlayer(state.getActivePlayer()) + "\n", state.getActivePlayer());
            output.publishToAll(output.namePlayer(state.getActivePlayer()) + " made a selection.");

            //advance active player
            if (state.allPasses()) {
                output.publishToAll("Only blanks remain.");
                draftComplete();
                return true;
            } else if (state.oneLeft()) {
                dealAndRequestLastCard();
                return true;
            }
            dealAndRequestNormalCard();
            return true;
        } else {
            output.publishToPlayer("Sorry, that response was not in-bounds. Please make another selection.", state.getActivePlayer());
        }
        return false;
    }

    private void dealAndRequestLastCard() {
        //handle discount
        List<Private> singleTon = state.dealHand();
        if (state.getWait() == NORMAL) {
            state.setWait(DISCOUNT);
            output.publishToAll("The final private is: " + singleTon);
        }
        int discount = state.getDiscount();
        if (singleTon.get(0).getCost() - discount == singleTon.get(0).getMinPrice()) {
            output.publishToPlayer("You are forced to accept:" + singleTon.get(0) + " for $" + singleTon.get(0).getMinPrice(), state.getActivePlayer());
            finalSelecitonMade(0);
        } else {
            StringBuilder bldr = new StringBuilder();
            stateAndPrompt(singleTon, bldr);
            bldr.append("1) decline and reduce price by $10");
            output.publishToPlayer(bldr.toString(), state.getActivePlayer(), true, false);
            output.publishToAll(output.namePlayer(state.getActivePlayer()) + " is next to pick (check your DMs)");
        }
    }

    private boolean finalSelecitonMade(int selection) {
        if (selection == 0) {
            state.buyIndex(0);
            output.publishToPlayer("You now hold" + state.stateOfPlayer(state.getActivePlayer()) + "\n", state.getActivePlayer());
            output.publishToAll(output.namePlayer(state.getActivePlayer()) + " accepted the last card.");
            draftComplete();
            return true;
        } else if (selection == 1) {
            state.reducePrice();
            output.publishToPlayer("Last card declined. You now hold" + state.stateOfPlayer(state.getActivePlayer()), state.getActivePlayer());
            output.publishToAll(output.namePlayer(state.getActivePlayer()) + " declined the last card.");
            dealAndRequestLastCard();
            return true;
        } else {
            output.publishToPlayer("I'm sorry, please enter 0 or 1", state.getActivePlayer());
            return false;
        }
    }

    private void draftComplete() {
        StringBuilder bldr = new StringBuilder("The Draft Is Complete\n");
        for (int i = state.getPlayerCount()-1; i >= 0; i--) {
            if (i == state.getPlayerCount() -1) {
                bldr.append("*PD* ");
            }
            bldr.append(output.mentionPlayer(i)).append(" spent $").append(state.getAmountSpentBy(i)).append(" and now holds").append(state.stateOfPlayer(i)).append("\n");
        }
        output.publishToAll(bldr.toString());
        output.abortDraft(true);
    }

    public synchronized boolean gotMessage(int selection) {
        if (state.getWait() == NORMAL) {
            return selectionMade(selection);
        } else if (state.getWait() == DISCOUNT) {
            return finalSelecitonMade(selection);
        } else {
            output.publishToAll("I was expecting a selection and got something else. Please make another selection.");
            return false;
        }
    }
}
