package qe;

/**
 * Created by micha on 5/14/2020.
 */
public class Tile {
    private int value;
    private Country country;
    private Industry industry;

    public Tile(int value, Country country, Industry industry) {
        this.value = value;
        this.country = country;
        this.industry = industry;
    }

    @Override
    public String toString() {
        return "Tile: [" + country + "|" + industry + "|" + value + "pts]";
    }
}
