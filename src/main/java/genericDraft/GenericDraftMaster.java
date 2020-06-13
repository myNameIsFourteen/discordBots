package genericDraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static trains1846.WaitType.DISCOUNT;
import static trains1846.WaitType.NORMAL;

/**
 * Created by micha on 6/11/2020.
 */
public class GenericDraftMaster {
    private final MessagePublisher output;
    private DraftState state;

    public GenericDraftMaster(MessagePublisher publisher46, int size, List<Object> toDraft) {
        output = publisher46;
        primeGame(size, toDraft);
    }

    void primeGame(int size, List<Object> toDraft) {
        //determine player order / priority deal
        int playerCount = size;
        state = new DraftState(playerCount);
        state.addToDeck(toDraft);

        state.shuffleDeck();

        for (int i = 0; i < playerCount; i++) {
            output.publishToPlayer("---Welcome to the draft. You hold:" + state.stateOfPlayer(i) + "---", i, false);
        }

        dealAndRequestNormalCard();
    }

    void dealAndRequestNormalCard() {
        //Deal N+2 to active player WAIT
        List<Object> privates = state.dealHand();
        requestSelection(privates);
    }

    private void requestSelection(List<Object> privates) {
        StringBuilder bldr = new StringBuilder();
        stateAndPrompt(privates, bldr);
        output.publishToPlayer(bldr.toString(), state.getActivePlayer());
        output.publishToAll(output.mentionPlayer(state.getActivePlayer()) + "is next to pick (check your DMs)");
    }

    private void stateAndPrompt(List<Object> privates, StringBuilder bldr) {
        bldr.append("You currently hold" + state.stateOfPlayer(state.getActivePlayer()) + "Please make a selection:\n");
        int i = 0;
        for (Object pvt : privates) {
            bldr.append(i + ") " + pvt + "\n");
            i++;
        }
    }

    void selectionMade(int selection) {
        //SELECTION recieve and proccess seleciton, shuffle unselected and add to bottom of deck
        if (state.pick(selection)) {
            output.publishToPlayer("You now hold" + state.stateOfPlayer(state.getActivePlayer()), state.getActivePlayer());
            output.publishToAll(output.mentionPlayer(state.getActivePlayer()) + " made a selection.");

            //advance active player
            if (state.isComplete()) {
                draftComplete();
                return;
            }
            dealAndRequestNormalCard();
            return;
        } else {
            output.publishToPlayer("Sorry, that number was not in-bounds.", state.getActivePlayer());
        }
    }

    void draftComplete() {
        StringBuilder bldr = new StringBuilder("The Draft Is Complete\n");
        for (int i = state.getPlayerCount()-1; i >= 0; i--) {
            bldr.append(output.mentionPlayer(i) + " holds" + state.stateOfPlayer(i));
        }
        output.publishToAll(bldr.toString());
        output.abortDraft();
    }

    public synchronized void gotMessage(int selection) {
        selectionMade(selection);
    }
}
