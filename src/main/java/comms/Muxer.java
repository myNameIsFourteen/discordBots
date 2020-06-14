package comms;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by micha on 6/13/2020.
 */
public class Muxer {
    Bag<User> openCounts = new HashBag<>();
    Map<User, PromptQueue> openChannels = new HashMap<>();

    private static Muxer singleton;

    public static synchronized Muxer getTheMuxer() {
        if (singleton == null) {
            singleton = new Muxer();
        }
        return singleton;
    }

    private Muxer() {

    }

    public synchronized PromptQueue openAChannel(User user) {
        if (openCounts.contains(user)) {
            openCounts.add(user);
            return openChannels.get(user);
        } else {
            openCounts.add(user);
            PromptQueue ret = new PromptQueue(user.openPrivateChannel().complete());
            openChannels.put(user, ret);
            return ret;
        }
    }

    public synchronized void closeChannel(User user) {
        openCounts.remove(user, 1);
        if (!openCounts.contains(user)) {
            openChannels.remove(user);
        }
    }

    public void messageIn(MessageReceivedEvent event) {
        if (event.getChannelType() == ChannelType.PRIVATE) {
            PromptQueue promptQueue = openChannels.get(event.getAuthor());
            if (promptQueue != null) {
                promptQueue.handleResponse(event.getMessage().getContentDisplay());
            }
        }
    }
}
