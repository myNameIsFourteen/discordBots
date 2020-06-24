package genericDraft;

import trains1846.Private;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by micha on 6/11/2020.
 */
public class DraftState<Pickable extends Object> {
    private int offset;
    private int playerCount;
    private int picksOutstanding;
    private List<List<Pickable>> packs = new ArrayList<>();
    private List<List<Pickable>> picks = new ArrayList<>();

    public DraftState(int playerCount, List<Pickable> startingDeck) {
        //deal the cards into random packs
        for (int i = 0; i < playerCount; i++) {
            packs.add(new ArrayList<Pickable>());
            picks.add(new ArrayList<Pickable>());
        }
        Collections.shuffle(startingDeck);
        for (int deal = 0;!startingDeck.isEmpty();deal++) {
            deal %= packs.size();
            packs.get(deal).add(startingDeck.remove(0));
        }
        this.playerCount =  playerCount;
        //pack creation complete
    }

    public synchronized boolean makeAPick(int player, int selection) {
        try {
            Pickable pick = currentPack(player).remove(selection);
            picks.get(player).add(pick);
            picksOutstanding--;
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    private List<Pickable> currentPack(int player) {
        int packNum = (player + offset) % packs.size();
        return packs.get(packNum);
    }

    public void passPacks() {
        offset++;
        picksOutstanding = playerCount;
    }

    public String stateOfPlayer(int i) {
        if (picks.get(i).isEmpty()) {
            return "Nothing";
        } else {
            return picks.get(i).toString();
        }
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public boolean newRoundReady() {
        return picksOutstanding == 0;
    }

    public boolean packsEmpty() {
        return picksOutstanding == 0 && packs.get(0).isEmpty();
    }

    public String promptPlayer(int player) {
        StringBuilder bldr = new StringBuilder();
        bldr.append("You currently hold").append(stateOfPlayer(player)).append("\nPlease make a selection:\n");
        int i = 0;
        for (Object pvt : currentPack(player)) {
            bldr.append(i).append(") ").append(pvt).append("\n");
            i++;
        }
        return bldr.toString();
    }
}
