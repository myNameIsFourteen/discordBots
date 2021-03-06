package bot;

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

/**
 * Created by micha on 6/7/2020.
 */
public class Publisher46 implements MessagePublisher {

    private final Runnable exitCallback;
    private MessageChannel channel;
    private List<PrivateChannel> privateChannels = new ArrayList<>();
    private DraftMaster draftMaster;
    private ArrayList<User> players = new ArrayList<>();
    private User activePlayer;

    public Publisher46(MessageReceivedEvent event, Runnable exitCallback) {
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

        Consumer<PrivateChannel> messageChannelFilter = (PrivateChannel prvChannel) -> privateChannels.add(prvChannel);

        List<Future<PrivateChannel>> futures = new ArrayList<>();

        players.forEach(p -> futures.add(p.openPrivateChannel().submit()));

        for (Future<PrivateChannel> future : futures) {
            try {
                privateChannels.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
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
        publishToPlayer(message, player, true);
    }

    public void publishToPlayer(String message, int player, boolean advancePlayer) {
        privateChannels.get(player).sendMessage(pFromIndex(player) + message).complete();
        if (advancePlayer) {
            activePlayer = players.get(player);
        }
    }

    @NotNull
    private String pFromIndex(int player) {
        return "P" + (players.size() - player);
    }

    public synchronized void handleMessage(MessageReceivedEvent event) {
        if (event.getChannelType() == ChannelType.PRIVATE) {
            try {
                int i = Integer.parseInt(event.getMessage().getContentRaw());
                if (event.getAuthor().getId().equals(activePlayer.getId())) {
                    draftMaster.gotMessage(i);
                } else {
                    event.getChannel().sendMessage("Hey, it isn't your turn right now").complete();
                }
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("Sorry, I was expecting an integer").complete();
            }
        }
    }

    @Override
    public void abortDraft() {
        for (int i = 0; i < players.size(); i++) {
            publishToPlayer("---The draft has ended!---", i, false);
        }
        exitCallback.run();
    }

    @Override
    public String mentionPlayer(int activePlayer) {
        return players.get(activePlayer).getAsMention();
    }
}