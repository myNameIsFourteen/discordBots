package trains1846;

import bot.Publisher46;

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
        List<Private> groupA = Private.groupA();
        removeSome(playerCount, groupA);
        state.addToDeck(groupA);

        List<Private> groupB = Private.groupB();
        removeSome(playerCount, groupB);
        state.addToDeck(groupB);

        List<Private> corps = Private.removableCorps();
        removeSome(playerCount, corps);

        state.shuffleDeck();
        dealAndRequestNormalCard();
    }

    private void removeSome(int playerCount, List<Private> groupA) {
        Collections.shuffle(groupA);
        for (int i = 5; i > playerCount; i--) {
            Private removed = groupA.remove(0);
            output.publishToAll("Removed " + removed);
        }
    }

    void dealAndRequestNormalCard() {
        //Deal N+2 to active player WAIT
        List<Private> privates = state.dealHand();
        requestSelection(privates);
    }

    private void requestSelection(List<Private> privates) {
        StringBuilder bldr = new StringBuilder();
        stateAndPrompt(privates, bldr);
        output.publishToPlayer(bldr.toString(), state.getActivePlayer());
        state.setWait(NORMAL);
    }

    private void stateAndPrompt(List<Private> privates, StringBuilder bldr) {
        bldr.append("You currently hold" + state.stateOfPlayer(state.getActivePlayer()) + "Please make a selection:\n");
        int i = 0;
        for (Private pvt : privates) {
            bldr.append(i + ") " + pvt + " for $" + (pvt.getCost() - state.getDiscount()) + "\n");
            i++;
        }
    }

    void selectionMade(int selection) {
        //SELECTION recieve and proccess seleciton, shuffle unselected and add to bottom of deck
        state.buyIndex(selection);
        output.publishToPlayer("You now hold" + state.stateOfPlayer(state.getActivePlayer()), state.getActivePlayer());

        //advance active player
        if (state.allPasses()) {
            output.publishToAll("Only blanks remain.");
            draftComplete();
            return;
        } else if (state.oneLeft()) {
            dealAndRequestLastCard();
        }
        dealAndRequestNormalCard();
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
            finalSelecitonMade(0);
        } else {
            StringBuilder bldr = new StringBuilder();
            stateAndPrompt(singleTon, bldr);
            bldr.append("1) decline and redude price by $10");
            output.publishToPlayer(bldr.toString(), state.getActivePlayer());
        }
    }

    void finalSelecitonMade(int selection) {
        if (selection == 0) {
            state.buyIndex(0);
            output.publishToPlayer("You now hold" + state.stateOfPlayer(state.getActivePlayer()), state.getActivePlayer());
            draftComplete();
        } else {
            state.reducePrice();
            dealAndRequestLastCard();
        }
    }

    void draftComplete() {
        output.publishToAll("The Draft Is Complete\n" + state.getEndState());
        output.abortDraft();
    }

    public void gotMessage(int selection) {
        if (state.getWait() == NORMAL) {
            selectionMade(selection);
        } else if (state.getWait() == DISCOUNT) {
            finalSelecitonMade(selection);
        } else {
            output.publishToAll("I was expecting a selection and got something else");
        }
    }
}
