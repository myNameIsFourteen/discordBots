package trains18EU;

import genericDraft.GenericPickable;

/**
 * Created by micha on 6/29/2020.
 */
public enum Minor18EU implements GenericPickable {
    ZERO("Minor 0 (First Choice)"),
    ONE("Minor 1 (Mail Contract)"),
    TWO("Minor 2 (Port)"),
    THREE("Minor 3 (Blasting)"),
    FOUR("Minor 4 (Extra Yellow/set)"),
    FIVE("Minor 5 (Token Bypass)"),
    SIX("Minor 6 (Track Upgrade/set)"),
    SEVEN("Minor 7 (Capital Infusion)"),
    EIGHT("Minor 8 (Town Upgrade/set)"),
    NINE("Minor 9 (Train Discount)"),
    TEN("Minor 10 (Longer Train)"),
    ELEVEN("Minor 11 (+10/+20 one city)"),
    TWELVE("Minor 12 (+1 Train Limit)"),
    THIRTEEN("Minor 13 (City Upgrade/set)"),
    FOURTEEN("Minor 14 (Free Pullman)"),
    FIFTEEN("Minor 15 (Permanant 2T)"),
    ONEB("Minor 1 (First Choice)");

    private String longDesc;

    Minor18EU(String longDesc) {
        this.longDesc = longDesc;
    }

    @Override
    public int pickIndex() {
        if (this == ONEB) {
            return 1;
        } else {
            return this.ordinal();
        }
    }

    @Override
    public String shortDescription() {
        return "M" + pickIndex();
    }

    @Override
    public String longDescription() {
        return longDesc;
    }

    @Override
    public String toString() {
        return shortDescription();
    }
}
