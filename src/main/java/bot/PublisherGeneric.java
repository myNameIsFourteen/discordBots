package bot;

import comms.IDraftMaster;
import comms.Muxer;
import comms.PromptQueue;
import genericDraft.MessagePublisher;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import trains1846.DraftMaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Created by micha on 6/7/2020.
 */
public class PublisherGeneric implements IDraftMaster {

    private final Runnable exitCallback;
    private MessageChannel channel;
//    private DraftMaster draftMaster;
    private ArrayList<User> players = new ArrayList<>();
    private List<PromptQueue> promptQueues = new ArrayList<>();
//    private User activePlayer;

    public PublisherGeneric(MessageReceivedEvent event, Runnable exitCallback) {
        channel = event.getChannel();
        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

        for (User user : event.getMessage().getMentionedUsers()) {
            for (int i = 0; i < event.getMessage().getMentionedUsersBag().getCount(user); i++) {
                players.add(user);
            }
        }

        while (players.size() < 3) {
            players.add(event.getAuthor());
        }

        Collections.shuffle(players);

        String help = " and has the Priority Deal\n";
        StringBuilder start = new StringBuilder();
        int i = players.size();
        for (User u : players) {
            start.append("User " + u.getAsMention() + " has pick " + i + help);
            i--;
            help = "\n";
        }
        publishToAll(start.toString());

        Collections.reverse(players);
        List<Future<PrivateChannel>> futures = new ArrayList<>();

        players.forEach(p -> futures.add(p.openPrivateChannel().submit()));

        for (User player : players) {
            promptQueues.add(Muxer.getTheMuxer().openAChannel(player));
        }

        //players shuffle goes here
//        draftMaster = new DraftMaster(this, players.size());
        this.exitCallback = exitCallback;
    }

    @Override
    public void publishToAll(String message) {
        channel.sendMessage(message).complete();
    }

//    @Override
//    public void publishToPlayer(String message, int player) {
//        publishToPlayer(message, player, true);
//    }

    public void publishToPlayer(String message, int player) {
        promptQueues.get(player).sendInfo(pFromIndex(player) +  message);
    }

    @NotNull
    private String pFromIndex(int player) {
        return "P" + (players.size() - player);
    }

//    public synchronized void handleMessage(MessageReceivedEvent event) {
//        if (event.getChannelType() == ChannelType.PRIVATE) {
//            try {
//                int i = Integer.parseInt(event.getMessage().getContentRaw());
//                if (event.getAuthor().getId().equals(activePlayer.getId())) {
//                    draftMaster.gotMessage(i);
//                } else {
//                    event.getChannel().sendMessage("Hey, it isn't your turn right now").complete();
//                }
//            } catch (NumberFormatException e) {
//                event.getChannel().sendMessage("Sorry, I was expecting an integer").complete();
//            }
//        }
//    }

    @Override
    public void abortDraft() {
        for (int i = 0; i < players.size(); i++) {
            publishToPlayer("---The draft has ended!---", i);
        }
        exitCallback.run();
    }

    public String mentionPlayer(int activePlayer) {
        return players.get(activePlayer).getAsMention();
    }
}