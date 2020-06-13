package genericDraft;

import java.util.List;

/**
 * Created by micha on 6/11/2020.
 */
public class DraftState {
    private int activePlayer;
    private int playerCount;

    public DraftState(int playerCount) {
        
    }

    public void addToDeck(List<Object> toDraft) {
        
    }

    public void shuffleDeck() {
        
    }

    public String stateOfPlayer(int i) {
        return null;
    }

    public List<Object> dealHand() {
        return null;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public boolean pick(int selection) {
        return false;
    }

    public boolean isComplete() {
        return false;
    }

    public int getPlayerCount() {
        return playerCount;
    }
}
