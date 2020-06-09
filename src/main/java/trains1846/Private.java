package trains1846;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by micha on 6/7/2020.
 */
public enum Private {
    BIG4(100, 60),
    MS(160, 100),
    CWI(60),
    MAIL(80),

    LSL(40),
    MC(40),
    OI(40),

    MEAT(60),
    STEAMBOAT(40),
    BLASTING(60),

    BLANK1(0),
    BLANK2(0),
    BLANK3(0),
    BLANK4(0),
    BLANK5(0),

    CandO(0),
    ERIE(0),
    PRR(0);

    private int minPrice;

    Private(int cost) {
        this.cost = cost;
        this.minPrice = cost;
    }

    Private(int cost, int minPrice) {
        this.cost = cost;
        this.minPrice = minPrice;
    }

    private int cost;

    public static List<Private> alwaysIn() {
        return new ArrayList<>(Arrays.asList(BIG4, MS, CWI, MAIL));
    }

    public static List<Private> groupA() {
        return new ArrayList<>(Arrays.asList(LSL, MC, OI));
    }

    public static List<Private> groupB() {
        return new ArrayList<>(Arrays.asList(MEAT, STEAMBOAT, BLASTING));
    }

    public static List<Private> playerCards(int playerCount) {
        return new ArrayList<>(Arrays.asList(BLANK1, BLANK2, BLANK3, BLANK4, BLANK5).subList(0, playerCount));
    }

    public static List<Private> removableCorps() {
        return new ArrayList<>(Arrays.asList(CandO, ERIE, PRR));
    }

    public static boolean isPass(Private aPrivate) {
        return playerCards(5).contains(aPrivate);
    }

    public int getCost() {
        return cost;
    }

    public int getMinPrice() {
        return minPrice;
    }
}
