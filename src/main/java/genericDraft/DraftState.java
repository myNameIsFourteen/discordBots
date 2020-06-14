package genericDraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by micha on 6/11/2020.
 */
public class DraftState<Pickable extends Object> {
    private int offset;
    private int playerCount;
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
        //pack creation complete
    }

    public boolean makeAPick(int player, int selection) {
        Pickable pick = currentPack(player).remove(selection);
        picks.get(player).add(pick);
        return true;
    }

    private List<Pickable> currentPack(int player) {
        int packNum = (player + offset) % packs.size();
        return packs.get(player);
    }

    private void passPacks() {
        offset++;
    }
}
