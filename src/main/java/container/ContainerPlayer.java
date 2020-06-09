package container;

import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by micha on 4/21/2020.
 */
public class ContainerPlayer {
    Member player;
    String playerName;
    int favoriteGood;
    int producesGood;

    public ContainerPlayer(Member player, Integer produce, Integer favorite) {
        this.player = player;
        this.playerName = player.getEffectiveName();
        this.favoriteGood = favorite;
        this.producesGood = produce;
    }

    public String publicString() {
        return playerName + " starts with an " + ContainerConstants.GOODNAMES.get(producesGood) + " factory.";
    }

    public void sendDM() {
        StringBuilder bldr = new StringBuilder();
        List<String> scores = Arrays.asList("  10", "10/5", "   6", "   4", "   2");
        for (int i = 0; i < 5; i++) {
            bldr.append(scores.get(i) + " = " + ContainerConstants.GOODNAMES.get((favoriteGood + i) % 5) + "\n");
        }
        String scorecard = bldr.toString();
        player.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage("===============================\nYou just joined a game of container. Your favorite container type is: " +
                    ContainerConstants.GOODNAMES.get(favoriteGood) + "\nAnd you start with an " +
                    ContainerConstants.GOODNAMES.get(producesGood) + " factory.\n" +
                    "Your scoring card is:\n" + scorecard + "==============================="
            ).queue();
        });
    }
}
