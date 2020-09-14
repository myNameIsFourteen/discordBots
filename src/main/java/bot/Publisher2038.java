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
import java.util.Locale;

public class Publisher2038 implements IDraftMaster {
    private final Runnable exitCallback;
    private MessageChannel channel;
    private Bag<String> tiles = new TreeBag<>();
    private boolean readyToAbort = false;
    private int luckyDebt = 0;

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
        publishToAll("This channel is now playing 2038!\nType \"!help\" for the command list");
        publishBagContents();
    }

    @Override
    public void publishToAll(String message) {
        channel.sendMessage(message).complete();
    }

    @Override
    public void abortDraft() {
        publishToAll("This game is shutting down. Printing final bag contents in case you need to restore it:");
        publishBagContents();
        exitCallback.run();
    }

    @Override
    public void processMessage(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        if (content.startsWith("!")) {
            readyToAbort = false;
        }
        if (content.startsWith("!return")) {
            boolean success = false;
            if (content.contains(" ")) {
                String arg = content.split(" ")[1];
                success = returnTile(arg);
            } else {
                publishToAll("You need to name a tile in your command.");
            }
            if (success && luckyDebt > 0) {
                luckyDebt--;
            }
        } else if (content.startsWith("!tile")) {
            if (content.contains(" ")) {
                String arg = content.split(" ")[1];
                publishTile(arg);
            } else {
                publishToAll("You need to name a tile in your command.");
            }
        } else if (content.startsWith("!") && luckyDebt > 0) {
            publishToAll("Command Ignored: Lucky must return a tile to the bag first!");
        } else if (content.startsWith("!bag")) {
            publishBagContents();
        } else if (content.startsWith("!draw")) {
            if (tiles.isEmpty()) {
                publishToAll("Sorry, the bag is empty");
            } else {
                String tile = drawRandomTile();
                publishTile(tile);
            }
        } else if (content.toLowerCase(Locale.US).startsWith("!ice")) {
            String special = "I";
            drawSearch(special);
        } else if (content.toLowerCase(Locale.US).startsWith("!rare")) {
            String special = "R";
            drawSearch(special);
        } else if (content.toLowerCase(Locale.US).startsWith("!lucky")) {
            String tile1 = drawRandomTile();
            String tile2 = drawRandomTile();
            publishToAll("Lucky Draws: " + tile1 + " and " + tile2 + ".");
            publishTile(tile1);
            publishTile(tile2);
            publishToAll("Please return one with !return");
            luckyDebt++;
        }
    }

    @Override
    public void processCommand(TopLevelCommand command) {
        if (command == TopLevelCommand.ABORT) {
            if (readyToAbort) {
                abortDraft();
            } else {
                readyToAbort = true;
                publishToAll("Are you sure you want to end the game? !abort again to confirm.");
            }
        }
    }

    public void drawSearch(String special) {
        if (tiles.size() < 2) {
            publishToAll("Sorry, there are not enough tiles in the bag");
        } else {
            String tile = drawRandomTile();
            publishTile(tile);
            if (!tile.contains(special)) {
                String searchee = special.equals("I") ? "Ice" : "Rare";
                publishToAll("First draw has no "+ searchee +", drawing again.");
                String tile2 = drawRandomTile();
                publishTile(tile2);
                returnTile(tile);
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
            channel.sendMessage(arg).addFile(png).complete();
        } else {
            publishToAll("Sorry I don't know a tile named: " + arg);
        }
    }

    public void publishTwoile(String tile1, String tile2, String msg) {
        //remove this fuction
    }

    public boolean returnTile(String arg) {
        File png = new File("resources\\2038tiles\\" + arg + ".png");
        if (png.exists()) {
            tiles.add(arg, 1);
            publishToAll("Returned tile: " + arg + " to the bag");
            return true;
        } else {
            publishToAll("Sorry I don't know a tile named: " + arg);
            return false;
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

    @Override
    public void sendHelpMessage() {
        publishToAll("This channel is playing 2038\n" +
                        "!tile [tile] to see a tile\n" +
                        "!draw to draw a tile from the bag\n" +
                        "!bag to review the bag contents\n" +
                        "!ice to draw a tile using Ice Finder's special ability\n" +
                        "!rare to draw a tile using Drill Hound's special ability\n" +
                        "!lucky to draw a tile using Lucky's special ability\n" +
                        "!return [tile] to return or add a tile to the bag\n" +
                        "!abort to end the game.\n"
        );
    }
}
