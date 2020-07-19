package bot;

import comms.IDraftMaster;
import comms.Muxer;
import comms.Prompt;
import comms.PromptQueue;
import genericDraft.GenericDraftMaster;
import genericDraft.GenericPickable;
import genericDraft.MessagePublisher;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import trains1846.DraftMaster;
import trains18EU.Minor18EU;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by micha on 6/7/2020.
 */
public class PublisherGeneric implements IDraftMaster, MessagePublisher {

    private final Runnable exitCallback;
    private final GenericDraftMaster draftMaster;
    private MessageChannel channel;
//    private DraftMaster draftMaster;
    private ArrayList<Member> players = new ArrayList<>();
    private List<PromptQueue> promptQueues = new ArrayList<>();
//    private User activePlayer;

    public PublisherGeneric(MessageReceivedEvent event, Runnable exitCallback) {
        channel = event.getChannel();
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

        for (Member user : event.getMessage().getMentionedMembers()) {
            for (int i = 0; i < event.getMessage().getMentionedUsersBag().getCount(user.getUser()); i++) {
                players.add(user);
            }
        }

        while (players.size() < 2) {
            players.add(event.getGuild().retrieveMember(event.getAuthor()).complete());
        }

        Collections.shuffle(players);

        StringBuilder start = new StringBuilder();
        int i = 1;
        for (Member u : players) {
            start.append("User " + u.getAsMention() + " has seat " + i + "\n");
            i++;
        }
        publishToAll(start.toString());

        Collections.reverse(players);
        List<Future<PrivateChannel>> futures = new ArrayList<>();

        players.forEach(p -> futures.add(p.getUser().openPrivateChannel().submit()));

        for (Member player : players) {
            promptQueues.add(Muxer.getTheMuxer().openAChannel(player.getUser()));
        }

        ArrayList draftObjects = new ArrayList<GenericPickable>();
        draftObjects.addAll(Arrays.asList(Minor18EU.values()));
        //Take out private #0 and #1 for odd player games, else take out #1B for even games
        if (players.size() % 2 == 0) {
            draftObjects.remove(Minor18EU.ONEB);
        } else {
            draftObjects.remove(Minor18EU.ZERO);
            draftObjects.remove(Minor18EU.ONE);
        }

        //players shuffle goes here
        draftMaster = new GenericDraftMaster(this, players.size(), draftObjects);
        this.exitCallback = exitCallback;
    }

    @Override
    public void publishToAll(String message) {
        channel.sendMessage(message).complete();
    }

    public void publishToPlayer(String message, int player) {
        promptQueues.get(player).sendInfo(pFromIndex(player) +  message);
    }

    @NotNull
    private String pFromIndex(int player) {
        return "P" + (players.size() - player);
    }

    @Override
    public void abortDraft() {
        for (int i = 0; i < players.size(); i++) {
            publishToPlayer("---The draft has ended!---", i, false, true);
            Muxer.getTheMuxer().closeChannel(players.get(i).getUser());
        }
        exitCallback.run();
    }

    public String mentionPlayer(int activePlayer) {
        return players.get(activePlayer).getAsMention();
    }

    @Override
    public String namePlayer(int activePlayer) {
        return players.get(activePlayer).getEffectiveName();
    }

    @Override
    public void publishToPlayer(String message, int player, boolean advancePlayer, boolean info) {
        if (info) {
            promptQueues.get(player).sendInfo(makeChannelRef(channel) + ": " + message);
        } else {
            promptQueues.get(player).promptUser(new Prompt() {
                @Override
                public String promptToSend() {
                    return makeChannelRef(channel) + ": " + message;
                }

                @Override
                public Function<String, Boolean> handleResponse() {
                    return (String s) -> {
                        try {
                            int i = Integer.parseInt(s);
                            return draftMaster.gotMessage(player, i);
                        } catch (NumberFormatException e) {
                            draftMaster.gotMessage(player, -1);
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
    }
}