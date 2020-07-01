package genericDraft;

import trains1846.Private;

import java.util.*;

/**
 * Created by micha on 6/11/2020.
 */
public class DraftState<Pickable extends GenericPickable> {
    private int offset;
    private int playerCount;
    private int picksOutstanding;
    private List<Map<Integer, Pickable>> packs = new ArrayList<>();
    private List<List<Pickable>> picks = new ArrayList<>();

    public DraftState(int playerCount, List<Pickable> startingDeck) {
        //deal the cards into random packs
        for (int i = 0; i < playerCount; i++) {
            packs.add(new HashMap<Integer, Pickable>());
            picks.add(new ArrayList<Pickable>());
        }
        Collections.shuffle(startingDeck);
        for (int deal = 0;!startingDeck.isEmpty();deal++) {
            deal %= packs.size();
            Pickable remove = startingDeck.remove(0);
            packs.get(deal).put(remove.pickIndex(), remove);
        }
        this.playerCount =  playerCount;
        //pack creation complete
    }

    public synchronized boolean makeAPick(int player, int selection) {
        if (currentPack(player).containsKey(selection)) {
            Pickable pick = currentPack(player).remove(selection);
            picks.get(player).add(pick);
            picksOutstanding--;
            return true;
        } else {
            return false;
        }
    }

    private Map<Integer, Pickable> currentPack(int player) {
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
        bldr.append("You currently hold ").append(stateOfPlayer(player)).append("\nPlease make a selection:\n");
        int i = 0;
        for (GenericPickable pvt : currentPack(player).values()) {
            bldr.append(pvt.pickIndex()).append(") ").append(pvt.longDescription()).append("\n");
            i++;
        }
        return bldr.toString();
    }

    public boolean hasDecision(int player) {
        return currentPack(player).size() > 1;
    }

    public String lastPickFor(int player) {
        StringBuilder bldr = new StringBuilder();
        bldr.append("You currently hold").append(stateOfPlayer(player)).append("\nAnd you have been passed:\n");
        for (Pickable pvt : currentPack(player).values()) {
            bldr.append(pvt.longDescription()).append("\n");
        }
        return bldr.toString();
    }

    public int lastPickIndex(int player) {
        for (Pickable pvt : currentPack(player).values()) {
            return pvt.pickIndex();
        }
        return 0;
    }
}
