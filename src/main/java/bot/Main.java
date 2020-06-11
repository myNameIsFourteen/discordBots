package bot;

import container.ContainerPlayer;
import container.ContainerStart;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import qe.QeManager;
import qe.QeStart;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.List;

/**
 * Created by micha on 4/21/2020.
 */
public class Main extends ListenerAdapter {

    private static SelfUser selfUser;

    public static void main(String[] args) throws LoginException {
        String token = args[0];
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setToken(token);
        builder.addEventListeners(new Main());
        selfUser = builder.build().getSelfUser();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.startsWith("!container")) {
            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
            if (mentionedMembers.isEmpty() || mentionedMembers.size() > 5) {
                event.getChannel().sendMessage("Container requires 3-5 players to play, but for test purposes I will generate games from 1-5 players.\n" +
                        "@mention 1-5 players in your !container command for me to start your game").complete();
            } else {
                gameStartWARg(event);
            }
        } else if (content.startsWith("!qe")) {
            QeManager.handleMessage(event);
        } else {
            for (Member member : event.getMessage().getMentionedMembers()) {
                if (member.getUser().getId().equals(selfUser.getId())) {
                    event.getChannel().sendMessage("Did somebody say my name?\nHi, I'm MomirBot! If you would like to start a game of container, enter the !container command.").complete();
                    return;
                }
            }
        }
    }

    private void gameStartWARg(@Nonnull MessageReceivedEvent event) {
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        List<ContainerPlayer> containerPlayers = new ContainerStart().startGame(mentionedMembers);
        int porder = 1;
        StringBuilder bldr = new StringBuilder();
        for (ContainerPlayer player : containerPlayers) {
            bldr.append(porder++ + ") " + player.publicString());
            bldr.append(System.lineSeparator());
        }
        event.getChannel().sendMessage(bldr.toString()).complete();
        for (ContainerPlayer player : containerPlayers) {
            player.sendDM();
        }
        System.out.print(bldr.toString());
    }
}
