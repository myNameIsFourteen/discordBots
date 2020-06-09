package qe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by micha on 5/14/2020.
 */
public class Tiles {
    public static List<Tile> newDeck(int playerCount) {
        List<Tile> always = new ArrayList<>(Arrays.asList(
                new Tile(2, Country.CN, Industry.AGRICULTURE),
                new Tile(4, Country.US, Industry.AGRICULTURE),

                new Tile(3, Country.EU, Industry.FINANCE),
                new Tile(2, Country.US, Industry.FINANCE),

                new Tile(3, Country.CN, Industry.HOUSING),
                new Tile(4, Country.JP, Industry.HOUSING),
                new Tile(2, Country.EU, Industry.HOUSING),

                new Tile(2, Country.JP, Industry.MANUFACTURING),
                new Tile(4, Country.EU, Industry.MANUFACTURING),
                new Tile(3, Country.US, Industry.MANUFACTURING)
        ));
        if (playerCount == 5) {
            always.addAll(Arrays.asList(
                    new Tile(3, Country.UK, Industry.AGRICULTURE),
                    new Tile(4, Country.UK, Industry.FINANCE),
                    new Tile(2, Country.UK, Industry.GOVERNMENT),

                    new Tile(4, Country.CN, Industry.GOVERNMENT),
                    new Tile(3, Country.JP, Industry.GOVERNMENT)
            ));
        } else {
            always.addAll(Arrays.asList(
                    new Tile(3, Country.JP, Industry.AGRICULTURE),
                    new Tile(1, Country.EU, Industry.AGRICULTURE),

                    new Tile(4, Country.CN, Industry.FINANCE),
                    new Tile(1, Country.JP, Industry.FINANCE),

                    new Tile(1, Country.CN, Industry.MANUFACTURING),

                    new Tile(1, Country.US, Industry.HOUSING)
            ));
        }
        return always;
    }
}
