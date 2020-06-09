package qe;

import container.ContainerPlayer;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by micha on 5/14/2020.
 */
public class QeStart {
    public QeState startGame(List<Member> playerNames) {
        List<Tile> deck = Tiles.newDeck(playerNames.size());
        Collections.shuffle(deck);
        List<Country> countries;
        if (playerNames.size() == 5) {
            countries = Arrays.asList(Country.values());
        } else {
            countries = Country.shortValues();
        }
        Collections.shuffle(countries);
        List<Industry> industries;
        if (playerNames.size() == 5) {
            industries = Arrays.asList(Industry.values());
        } else {
            industries = Industry.shortValues();
        }
        Collections.shuffle(industries);
        return new QeState(deck, countries, industries);
    }
}
