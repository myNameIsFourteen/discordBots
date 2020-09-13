package bot;

public enum TopLevelCommand {
    STARTPLAYER("!startplayer", false),
    DRAFT1846("!1846draft",false),
    DRAFT18EU("!18eudraft", false),
    PLAY2038("!2038", true),
    HELP("!help", true);

    String invocation;
    boolean activeSupport;


    private TopLevelCommand(String invocation, boolean active) {
        this.invocation = invocation;
        activeSupport = active;
    }
}
