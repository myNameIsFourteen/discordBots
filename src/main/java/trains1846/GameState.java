package trains1846;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static trains1846.WaitType.NONE;

/**
 * Created by micha on 6/7/2020.
 */
public class GameState {
    private List<Private> currentHand = new ArrayList<>();
    private List<Private> deck = new ArrayList<>();
    private int playerCount;
    private int discount = 0;
    private int activePlayer = -1;
    private WaitType currentWaiting = NONE;
    private int[] cash = {400, 400, 400, 400, 400};
    private List<List<Private>> picks = new ArrayList<>();

    GameState(int playerCount) {
        this.playerCount = playerCount;
        for (int i = 0; i < playerCount; i++) {
            picks.add(new ArrayList<>());
        }
        addToDeck(Private.alwaysIn());
    }

    void addToDeck(List<Private> newCards) {
        Collections.shuffle(newCards);
        deck.addAll(newCards);
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public List<Private> dealHand() {
        if (!currentHand.isEmpty()) {
            throw new UnsupportedOperationException("Cannot deal a hand while another hand is being considered");
        }
        for(int i = 0; i < playerCount + 2 && !deck.isEmpty(); i++) {
            currentHand.add(deck.remove(0));
        }
        activePlayer++;
        activePlayer = activePlayer % playerCount;
        return currentHand;
    }

    public boolean buyIndex(int selection) {
        Private selected;
        try {
            selected = currentHand.remove(selection);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        picks.get(activePlayer).add(selected);
        cash[activePlayer] -= selected.getCost();
        cash[activePlayer] += discount;
        //gotta add selected to the current player's holdings
        deck.addAll(currentHand);
        currentHand.clear();
        return true;
    }

    public boolean allPasses() {
        return deck.stream().allMatch(Private::isPass);
    }

    public boolean oneLeft() {
        return deck.size() == 1;
    }

    public void reducePrice() {
        deck.addAll(currentHand);
        currentHand.clear();
        discount += 10;
    }

    public int getDiscount() {
        return discount;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public void setWait(WaitType wait) {
        this.currentWaiting = wait;
    }

    public WaitType getWait() {
        return currentWaiting;
    }

    public String getEndState() {
        StringBuilder builder = new StringBuilder();
        for (int i = playerCount - 1; i >= 0; i--) {
            builder.append(stateOfPlayer(i) + "\n");
        }
        return builder.toString();
    }

    @NotNull
    String stateOfPlayer(int i) {
        if (picks.get(i).isEmpty()) {
            return ": $" + cash[i];
        }
        return ": " + picks.get(i) + " and $" + cash[i];
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public String getAmountSpentBy(int i) {
        return String.valueOf(400 - cash[i]);
    }
}
