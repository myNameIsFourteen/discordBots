package qe;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Iterator;

/**
 * Created by micha on 5/14/2020.
 */
public class QeManager {
    static QeState gameState = null;

    public static void handleMessage(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if (content.startsWith("!qe draw")) {
            if (gameState != null) {
                event.getChannel().sendMessage(gameState.drawTile()).queue();
            }
        } else if (content.startsWith("!qe start")) {
            gameState = new QeStart().startGame(event.getMessage().getMentionedMembers());
            Iterator it = gameState.getCountriesForPlayers().iterator();
            Iterator id = gameState.getIndustriesForPlayers().iterator();
            for (Member player : event.getMessage().getMentionedMembers()) {
                Object country = it.next();
                event.getChannel().sendMessage(player.getEffectiveName() + " is playing: " + country).queue();
                player.getUser().openPrivateChannel().queue((channel) -> {
                    channel.sendMessage("===============================\nYou just joined a game of QE. Your nation is: " +
                            country + "\nAnd your bonus iundustry is " +
                            id.next() + ".\n" +
                            "==============================="
                    ).queue();
                });
            }
        }
    }
}
