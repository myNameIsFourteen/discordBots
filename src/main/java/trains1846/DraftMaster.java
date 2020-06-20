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

    public DraftMaster(MessagePublisher publisher46, int size) {
        output = publisher46;
        primeGame(size);
    }

    void primeGame(int size) {
        //determine player order / priority deal
        int playerCount = size;
        state = new GameState(playerCount);
        state.addToDeck(Private.playerCards(playerCount));
        List<Private> removed = new ArrayList<>();
        List<Private> groupA = Private.groupA();
        removed.addAll(removeSome(playerCount, groupA));
        state.addToDeck(groupA);

        List<Private> groupB = Private.groupB();
        removed.addAll(removeSome(playerCount, groupB));
        state.addToDeck(groupB);

        output.publishToAll("Removed Privates: " + removed);

        List<Private> corps = Private.removableCorps();
        output.publishToAll("Removed Corporations: " + removeSome(playerCount, corps));

        state.shuffleDeck();

        for (int i = 0; i < playerCount; i++) {
            output.publishToPlayer("---Welcome to 1846 draft.---", i, false, true);
        }

        dealAndRequestNormalCard();
    }

    private List<Private> removeSome(int playerCount, List<Private> groupA) {
        Collections.shuffle(groupA);
        List<Private> ret = new ArrayList<>();
        for (int i = 5; i > playerCount; i--) {
            Private removed = groupA.remove(0);
            ret.add(removed);
        }
        return ret;
    }

    void dealAndRequestNormalCard() {
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
        bldr.append("You currently hold" + state.stateOfPlayer(state.getActivePlayer()) + "\nPlease make a selection:\n");
        int i = 0;
        for (Private pvt : privates) {
            bldr.append(i + ") " + pvt + " for $" + (pvt.getCost() - state.getDiscount()) + "\n");
            i++;
        }
    }

    void selectionMade(int selection) {
        //SELECTION recieve and proccess seleciton, shuffle unselected and add to bottom of deck
        if (state.buyIndex(selection)) {
            output.publishToPlayer("You now hold" + state.stateOfPlayer(state.getActivePlayer()) + "\n", state.getActivePlayer());
            output.publishToAll(output.namePlayer(state.getActivePlayer()) + " made a selection.");

            //advance active player
            if (state.allPasses()) {
                output.publishToAll("Only blanks remain.");
                draftComplete();
                return;
            } else if (state.oneLeft()) {
                dealAndRequestLastCard();
                return;
            }
            dealAndRequestNormalCard();
            return;
        } else {
            output.publishToPlayer("Sorry, that number was not in-bounds.", state.getActivePlayer());
        }
    }

    void dealAndRequestLastCard() {
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
            bldr.append("1) decline and redude price by $10");
            output.publishToPlayer(bldr.toString(), state.getActivePlayer(), true, false);
            output.publishToAll(output.namePlayer(state.getActivePlayer()) + " is next to pick (check your DMs)");
        }
    }

    void finalSelecitonMade(int selection) {
        if (selection == 0) {
            state.buyIndex(0);
            output.publishToPlayer("You now hold" + state.stateOfPlayer(state.getActivePlayer()) + "\n", state.getActivePlayer());
            output.publishToAll(output.namePlayer(state.getActivePlayer()) + " accepted the last card.");
            draftComplete();
        } else if (selection == 1) {
            state.reducePrice();
            output.publishToPlayer("Last card declined. You now hold" + state.stateOfPlayer(state.getActivePlayer()), state.getActivePlayer());
            output.publishToAll(output.namePlayer(state.getActivePlayer()) + " declined the last card.");
            dealAndRequestLastCard();
        } else {
            output.publishToPlayer("I'm sorry, please enter 0 or 1", state.getActivePlayer());
        }
    }

    void draftComplete() {
        StringBuilder bldr = new StringBuilder("The Draft Is Complete\n");
        for (int i = state.getPlayerCount()-1; i >= 0; i--) {
            if (i == state.getPlayerCount() -1) {
                bldr.append("*PD* ");
            }
            bldr.append(output.mentionPlayer(i) + " holds" + state.stateOfPlayer(i) + "\n");
        }
        output.publishToAll(bldr.toString());
        output.abortDraft();
    }

    public synchronized void gotMessage(int selection) {
        if (state.getWait() == NORMAL) {
            selectionMade(selection);
        } else if (state.getWait() == DISCOUNT) {
            finalSelecitonMade(selection);
        } else {
            output.publishToAll("I was expecting a selection and got something else");
        }
    }
}
