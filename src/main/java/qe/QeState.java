package qe;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by micha on 5/14/2020.
 */
public class QeState {
    LinkedList<Tile> deck;
    LinkedList<Country> countriesForPlayers;
    LinkedList<Industry> industriesForPlayers;

    public QeState(List<Tile> deck, List<Country> countriesForPlayers, List<Industry> industriesForPlayers) {
        this.deck = new LinkedList<>(deck);
        this.countriesForPlayers = new LinkedList<>(countriesForPlayers);
        this.industriesForPlayers = new LinkedList<>(industriesForPlayers);
    }

    public String drawTile() {
        if (deck.isEmpty()) {
            return "The deck is empty and the game is over.";
        }
        Tile draw = deck.remove();
        return draw.toString() + ", " + deck.size() + " tiles remain.";
    }

    public Iterable<Country> getCountriesForPlayers() {
        return countriesForPlayers;
    }

    public Iterable<Industry> getIndustriesForPlayers() {
        return industriesForPlayers;
    }
}
