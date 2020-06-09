package qe;

import java.util.Arrays;
import java.util.List;

/**
 * Created by micha on 5/14/2020.
 */
public enum Country {
    US,
    CN,
    JP,
    EU,
    UK,;

    public static List<Country> shortValues() {
        return Arrays.asList(US, CN, JP, EU);
    }
}
