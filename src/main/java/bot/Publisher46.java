package bot;

import comms.IDraftMaster;
import comms.Muxer;
import comms.Prompt;
import comms.PromptQueue;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import trains1846.DraftMaster;
import genericDraft.MessagePublisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by micha on 6/7/2020.
 */
public class Publisher46 implements MessagePublisher, IDraftMaster {

    private final Runnable exitCallback;
    private MessageChannel channel;
    private GuildChannel channelg;
    private DraftMaster draftMaster;
    private ArrayList<Member> players = new ArrayList<>();
    private List<PromptQueue> promptQueues = new ArrayList<>();
    private Member activePlayer;

    public Publisher46(MessageReceivedEvent event, Runnable exitCallback) {
        channel = event.getChannel();
        channelg = event.getGuild().getGuildChannelById(channel.getId());
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

        for (Member user : event.getMessage().getMentionedMembers()) {
            for (int i = 0; i < event.getMessage().getMentionedUsersBag().getCount(user.getUser()); i++) {
                players.add(user);
            }
        }

        while (players.size() < 3) {
            players.add(event.getGuild().retrieveMember(event.getAuthor()).complete());
        }

        Collections.shuffle(players);

        String help = " and the Priority Deal\n";
        StringBuilder start = new StringBuilder("Priority/Seating Order:\n");
        int i = 1;
        for (Member u : players) {
            start.append("User " + u.getAsMention() + " has seat " + i + help);
            i++;
            help = "\n";
        }
        publishToAll(start.toString());

        Collections.reverse(players);

        start = new StringBuilder("Priority/Seating Order:\n");
        i = 1;
        for (Member u : players) {
            start.append("User " + u.getEffectiveName() + " has pick " + i + help);
            i++;
            help = "\n";
        }
        publishToAll(start.toString());

        for (Member player : players) {
            promptQueues.add(Muxer.getTheMuxer().openAChannel(player.getUser()));
        }

        //players shuffle goes here
        draftMaster = new DraftMaster(this, players.size());
        this.exitCallback = exitCallback;
    }

    @Override
    public void publishToAll(String message) {
        channel.sendMessage(message).complete();
    }

    @Override
    public void publishToPlayer(String message, int player) {
        publishToPlayer(message, player, true, true);
    }

    public void publishToPlayer(String message, int player, boolean advancePlayer, boolean info) {
        if (info) {
            promptQueues.get(player).sendInfo(channel.getName() + ": " + message);
        } else {
            promptQueues.get(player).promptUser(new Prompt() {
                @Override
                public String promptToSend() {
                    return channel.getName() + ": " + message;
                }

                @Override
                public Function<String, Boolean> handleResponse() {
                    return (String s) -> {
                        try {
                            int i = Integer.parseInt(s);
                            return draftMaster.gotMessage(i);
                        } catch (NumberFormatException e) {
                            // event.getChannel().sendMessage("Sorry, I was expecting an integer").complete();
                        }
                        return false;
                    };
                }

                @Override
                public User userToPrompt() {
                    return players.get(player).getUser();
                }
            });
        }
        if (advancePlayer) {
            activePlayer = players.get(player);
        }
    }

    @NotNull
    private String pFromIndex(int player) {
        return "P" + (players.size() - player);
    }

    public synchronized boolean handleMessage(MessageReceivedEvent event) {
        if (event.getChannelType() == ChannelType.PRIVATE) {
            try {
                int i = Integer.parseInt(event.getMessage().getContentRaw());
                if (event.getAuthor().getId().equals(activePlayer.getId())) {
                    draftMaster.gotMessage(i);
                    return true;
                } else {
                    event.getChannel().sendMessage("Hey, it isn't your turn right now").complete();
                }
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Sorry, I was expecting an integer").complete();
            }
        }
        return false;
    }

    @Override
    public void abortDraft() {
        for (int i = 0; i < players.size(); i++) {
            publishToPlayer("---The draft has ended!---", i, false, true);
            Muxer.getTheMuxer().closeChannel(players.get(i).getUser());
        }
        exitCallback.run();
    }

    @Override
    public String mentionPlayer(int activePlayer) {
        return players.get(activePlayer).getAsMention();
    }

    @Override
    public String namePlayer(int activePlayer) {
        return players.get(activePlayer).getEffectiveName();
    }
}