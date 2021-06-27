package bot;

public enum TopLevelCommand {
    STARTPLAYER("!startplayer", true),
    DRAFTOLD1846("!1846old",true),
    DRAFT1846("!1846draft",true),
    DRAFT18EU("!18eudraft", true),
    PLAY2038("!2038", true),
    ABORT("!abort", true),
    HELP("!help", true);

    String invocation;
    boolean activeSupport;


    private TopLevelCommand(String invocation, boolean active) {
        this.invocation = invocation;
        activeSupport = active;
    }
}
