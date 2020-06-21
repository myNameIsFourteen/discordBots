package trains1846;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by micha on 6/7/2020.
 */
public enum Private {
    BIG4(100, 60, "Big 4"),
    MS(140, 80, "Michigan Southern"),
    CWI(60, "Chicago & Western Indiana"),
    MAIL(80, "Mail Contract"),

    LSL(40, "Lake Shore Line"),
    MC(40, "Michigan Central"),
    OI(40, "Ohio & Indiana"),

    MEAT(60, "Meat Packing"),
    STEAMBOAT(40, "Steamboat"),
    BLASTING(60, "Tunnel Blasting"),

    BLANK1(0, "Pass(1)"),
    BLANK2(0, "Pass(2)"),
    BLANK3(0, "Pass(3)"),
    BLANK4(0, "Pass(4)"),
    BLANK5(0, "Pass(5)"),

    CandO(0, "C&O"),
    ERIE(0, "Erie"),
    PRR(0, "PRR");

    private int minPrice;
    private String prettyName;

    Private(int cost, String prettyName) {
        this (cost, 0, prettyName);
    }

    Private(int cost, int minPrice, String prettyName) {
        this.cost = cost;
        this.minPrice = minPrice;
        this.prettyName = prettyName;
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

    public String getPrettyName() {
        return prettyName;
    }
}
