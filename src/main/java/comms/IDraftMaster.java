package comms;

import bot.TopLevelCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Created by micha on 6/14/2020.
 */
public interface IDraftMaster {
    void publishToAll(String s);

    default String makeChannelRef(MessageChannel channel) {
        StringBuilder bldr = new StringBuilder();
        if (channel.getType() == ChannelType.TEXT) {
            TextChannel textChannel = (TextChannel) channel;
            bldr.append(textChannel.getGuild().getName()).append("::").append(textChannel.getAsMention());
        } else {
            bldr.append(channel.getName());
        }
        return bldr.toString();
    }

    void abortDraft(boolean naturalEnd);

    default void processMessage(MessageReceivedEvent event) {
        //do nothing.
    }

    default void processCommand(TopLevelCommand command) {
        if (command == TopLevelCommand.ABORT) {
            abortDraft(false);
        }
    }

    void sendHelpMessage();
}
