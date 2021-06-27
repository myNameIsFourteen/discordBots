package bot;

import comms.IDraftMaster;
import comms.Muxer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by micha on 4/21/2020.
 */
public class SpaceTrainBot extends ListenerAdapter {

    private static SelfUser selfUser;
    private static JDA main;
    Publisher46 currentGame = null;
    Map<MessageChannel, IDraftMaster> games = new HashMap<>();

    public static void main(String[] args) throws LoginException {
        String token = args[0];
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setToken(token);
        builder.addEventListeners(new SpaceTrainBot());
        main = builder.build();
        selfUser = main.getSelfUser();
        main.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, "18xx | try !help"));
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        Message message = event.getMessage();
        String content = message.getContentRaw();

        boolean handled = false;
        for (TopLevelCommand command : TopLevelCommand.values()) {
            if (command.activeSupport && content.toLowerCase(Locale.US).startsWith(command.invocation)) {
                handled = handleCommand(command, event);
            }
        }

        if (!handled) {
            if (content.startsWith("!draftStatus")) {
                if (event.getChannelType() == ChannelType.PRIVATE) {
                    event.getChannel().sendMessage(gamesStatusMessage()).complete();
                } else {
                    event.getChannel().sendMessage("This command only works via DM").complete();
                }
            } else if (games.containsKey(event.getChannel())) {
                games.get(event.getChannel()).processMessage(event);
            } else {
                Muxer.getTheMuxer().messageIn(event);
            }
        }
    }

    /**
     *
     * @param command
     * @param event
     * @return true if the command was handled
     */
    private boolean handleCommand(TopLevelCommand command, MessageReceivedEvent event) {
        switch (command) {
            case STARTPLAYER:
                if (event.getChannelType() == ChannelType.TEXT) {
                    List<String> users = new ArrayList<>(event.getMessage().getMentionedMembers()).stream().map(Member::getEffectiveName).collect(Collectors.toList());
                    String text = startPlayer(users);
                    if (text.length() > 0) {
                        event.getChannel().sendMessage(text).queue();
                    }
                }
                return true;
            case DRAFTOLD1846:
            case DRAFT1846:
                IDraftMaster iDraftMaster = games.get(event.getChannel());
                if (iDraftMaster == null) {
                    games.put(event.getChannel(), new Publisher46(event, () -> {
                        games.remove(event.getChannel());
                    }, command == TopLevelCommand.DRAFTOLD1846));
                } else {
                    event.getChannel().sendMessage("Sorry, there is a game in progress. Try again later or run !1846abort").complete();
                }
                return true;
            case DRAFT18EU:
                iDraftMaster = games.get(event.getChannel());
                if (iDraftMaster == null) {
                    games.put(event.getChannel(), new Publisher18EU(event, () -> {
                        games.remove(event.getChannel());
                    }));
                } else {
                    event.getChannel().sendMessage("Sorry, there is a game in progress. Try again later or run !1846abort").complete();
                }
                return true;
            case PLAY2038:
                iDraftMaster = games.get(event.getChannel());
                if (iDraftMaster == null) {
                    games.put(event.getChannel(), new Publisher2038(event, () -> {
                        games.remove(event.getChannel());
                    }));
                }
                return true;
            case HELP:
                sendHelpMessage(event);
                return true;
            case ABORT:
                iDraftMaster = games.get(event.getChannel());
                iDraftMaster.processCommand(TopLevelCommand.ABORT);
                return true;
        }
        return false;
    }

    private void sendHelpMessage(MessageReceivedEvent event) {
        if (games.containsKey(event.getChannel())) {
            games.get(event.getChannel()).sendHelpMessage();
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("I accept the following commands:\n");
            for (TopLevelCommand command : TopLevelCommand.values()) {
                if (command.activeSupport) {
                    builder.append(command.invocation).append("\n");
                }
            }
            event.getChannel().sendMessage(builder.toString()).complete();
        }
    }

    private String gamesStatusMessage() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("There are " + games.keySet().size() + " drafts ongoing.\n");
        for (MessageChannel channel : games.keySet()) {
            if (channel.getType() == ChannelType.TEXT) {
                TextChannel textChannel = (TextChannel) channel;
                bldr.append(textChannel.getGuild().getName()).append("::").append(textChannel.getAsMention()).append("\n");
            } else {
                bldr.append(channel.getName()).append("\n");
            }
        }
        return bldr.toString();
    }

    @NotNull
    public String startPlayer(List<String> users) {
        StringBuilder bldr = new StringBuilder();
        Collections.shuffle(users);
        int i = 1;
        for (String user : users) {
            bldr.append(user + " is in seat " + (i++) + "\n");
        }
        return bldr.toString();
    }
}
