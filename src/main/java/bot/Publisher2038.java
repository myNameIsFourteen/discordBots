package bot;

import comms.IDraftMaster;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.TreeBag;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Publisher2038 implements IDraftMaster {
    private final Runnable exitCallback;
    private MessageChannel channel;
    private Bag<String> tiles = new TreeBag<>();

    public Publisher2038(MessageReceivedEvent event, Runnable exitCallback) {
        this.channel = event.getChannel();
        this.exitCallback = exitCallback;
        tiles.add("N1", 12);
        tiles.add("N2", 12);
        tiles.add("I3", 2);
        tiles.add("I4", 5);
        tiles.add("I5", 7);
        tiles.add("R2", 2);
        tiles.add("R3", 4);
        tiles.add("R4", 6);
        tiles.add("N2N1", 12);
        tiles.add("N2N2", 8);
        tiles.add("I3N1", 6);
        tiles.add("I3N2", 4);
        tiles.add("I4N1", 4);
        tiles.add("I4N2", 4);
        tiles.add("R2N1", 4);
        tiles.add("R2N2", 2);
        tiles.add("R3N2", 2);
        tiles.add("R3N1", 2);
        tiles.add("R2I3", 2);
        tiles.add("R2I4", 2);
        tiles.add("R3I4", 2);
        tiles.add("R3I3", 2);
        publishToAll("This channel is now playing 2038!\n!tile [tile] to see a tile\n!draw to draw a tile from the bag\n!bag to review the bag contents");
        publishBagContents();
    }

    @Override
    public void publishToAll(String message) {
        channel.sendMessage(message).complete();
    }

    @Override
    public void abortDraft() {
        exitCallback.run();
    }

    @Override
    public void processMessage(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        if (content.startsWith("!tile")) {
            if (content.contains(" ")) {
                String arg = content.split(" ")[1];
                publishTile(arg);
            } else {
                publishToAll("You need to name a tile in your command.");
            }
        } else if (content.startsWith("!bag")) {
            publishBagContents();
        } else if (content.startsWith("!draw")) {
            if (tiles.isEmpty()) {
                publishToAll("Sorry, the bag is empty");
            } else {
                String tile = drawRandomTile();
                publishTile(tile);
            }
        }
    }

    private String drawRandomTile() {
        ArrayList<String> toScramble = new ArrayList<>(tiles);
        Collections.shuffle(toScramble);
        String draw = toScramble.iterator().next();
        tiles.remove(draw, 1);
        return draw;
    }

    public void publishTile(String arg) {
        File png = new File("resources\\2038tiles\\" + arg + ".png");
        if (png.exists()) {
            EmbedBuilder ebuilder = new EmbedBuilder();
            ebuilder.setImage("attachment://"+arg+".png");
            channel.sendMessage(arg + " tile").addFile(png).embed(ebuilder.build()).queue();
        } else {
            publishToAll("Sorry I don't know a tile named: " + arg);
        }
    }

    void publishBagContents() {
        StringBuilder builder = new StringBuilder();
        builder.append("The Bag Contains:\n```");
        for (String str : tiles.uniqueSet()) {
            builder.append(str + ": x" +tiles.getCount(str) + "\n");
        }
        builder.append("```");
        publishToAll(builder.toString());
    }
}
