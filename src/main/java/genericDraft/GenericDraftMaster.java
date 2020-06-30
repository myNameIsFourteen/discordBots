package genericDraft;

import comms.Muxer;

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

    public GenericDraftMaster(MessagePublisher publisher, int size, List<Object> toDraft) {
        output = publisher;
        primeGame(size, toDraft);
    }

    void primeGame(int size, List<Object> toDraft) {
        //determine player order / priority deal
        int playerCount = size;
        state = new DraftState(playerCount, toDraft);

        for (int i = 0; i < playerCount; i++) {
            output.publishToPlayer("---Welcome to the draft. You hold: " + state.stateOfPlayer(i) + "---", i);
        }

        state.passPacks();
        promptEachPick(playerCount);
    }

    private void promptEachPick(int playerCount) {
        for (int i = 0; i < playerCount; i++) {
            if (state.hasDecision(i)) {
                output.publishToPlayer(state.promptPlayer(i), i, true, false);
            } else {
                output.publishToPlayer(state.lastPickFor(i), i, true, true);
                gotMessage(i, 0);
            }
        }
    }

    //recieved a pick from the player
    public boolean gotMessage(int player, int i) {
        boolean picked = state.makeAPick(player, i);
        if (picked) {
            output.publishToPlayer("You now hold " + state.stateOfPlayer(player) + "\n", player);
            output.publishToAll(output.namePlayer(player) + " made a selection.");
        } else {
            output.publishToPlayer("Sorry, that response was not in-bounds. Please make another selection.", i);
        }
        if (state.packsEmpty()) {
            draftComplete();
        } else if (state.newRoundReady()) {
            state.passPacks();
            promptEachPick(state.getPlayerCount());
        }
        return picked;
    }

    private void draftComplete() {
        StringBuilder bldr = new StringBuilder("The Draft Is Complete\n");
        for (int i = state.getPlayerCount()-1; i >= 0; i--) {
            bldr.append(output.mentionPlayer(i)).append("Holds ").append(state.stateOfPlayer(i)).append("\n");
        }
        output.publishToAll(bldr.toString());
        output.abortDraft();
    }
}
