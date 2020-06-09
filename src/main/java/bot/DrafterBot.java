package bot;

import container.ContainerPlayer;
import container.ContainerStart;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import qe.QeManager;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by micha on 4/21/2020.
 */
public class DrafterBot extends ListenerAdapter {

    private static SelfUser selfUser;
    private static JDA main;
    Publisher46 currentGame = null;

    public static void main(String[] args) throws LoginException {
        String token = args[0];
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setToken(token);
        builder.addEventListeners(new DrafterBot());
        main = builder.build();
        selfUser = main.getSelfUser();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.startsWith("!1846draft")) {
            if (currentGame == null) {
                currentGame = new Publisher46(event, () -> {currentGame = null;});
            } else {
                event.getChannel().sendMessage("Sorry, there is a draft in progress. Try again later or run !1846abort").queue();
            }
        } else if (content.startsWith("!1846abort")) {
            currentGame.publishToAll("This draft was aborted by someone using the !1846abort command");
            currentGame = null;
        } else {
            if (currentGame != null) {
                currentGame.handleMessage(event);
            }
            if (event.getGuild() != null) {
                if (event.getMessage().getMentionedMembers() != null) {
                    for (Member member : event.getMessage().getMentionedMembers()) {
                        if (member.getUser().getId().equals(selfUser.getId())) {
                            event.getChannel().sendMessage("Did somebody say my name?\nIf you would like to start a game of 1846, send a message that starts with \"!1846draft\".\n Make sure to @mention each of the players in the game in that message.").queue();
                            return;
                        }
                    }
                }
            }
        }
    }
}
