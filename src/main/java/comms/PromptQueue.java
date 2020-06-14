package comms;

import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by micha on 6/13/2020.
 */
public class PromptQueue {
    private final MessageChannel channel;
    private List<Prompt> queue = new LinkedList<>();
    private Prompt currentPrompt = null;

    public PromptQueue(MessageChannel channel) {
        this.channel = channel;
    }

    public void promptUser(Prompt prompt) {
        queue.add(prompt);
        if (!isWaitingForResponse()) {
            popPromptAndInitiate();
        }
    }

    public void handleResponse(String message) {
        if (currentPrompt != null) {
            if (currentPrompt.handleResponse().apply(message)) {
                popPromptAndInitiate();
            }
        }
    }

    private synchronized void popPromptAndInitiate() {
        if (!queue.isEmpty()) {
            currentPrompt = queue.remove(0);
        }
        channel.sendMessage(currentPrompt.promptToSend()).complete();
    }

    private synchronized boolean isWaitingForResponse() {
        return currentPrompt != null;
    }

    public void sendInfo(String s) {
        channel.sendMessage(s).complete();
    }
}
