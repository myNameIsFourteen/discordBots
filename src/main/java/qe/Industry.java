package qe;

import java.util.Arrays;
import java.util.List;

/**
 * Created by micha on 5/14/2020.
 */
public enum Industry {
    AGRICULTURE,
    HOUSING,
    FINANCE,
    MANUFACTURING,
    GOVERNMENT;

    public static List<Industry> shortValues() {
        return Arrays.asList(AGRICULTURE, HOUSING, FINANCE, MANUFACTURING);
    }
}
