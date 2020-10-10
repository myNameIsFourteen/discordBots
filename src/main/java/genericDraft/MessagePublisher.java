package genericDraft;

/**
 * Created by micha on 6/7/2020.
 */
public interface MessagePublisher {
    void publishToAll(String message);
    void publishToPlayer(String message, int player);

    void abortDraft(boolean naturalEnd);

    String mentionPlayer(int activePlayer);
    String namePlayer(int activePlayer);

    void publishToPlayer(String s, int i, boolean b, boolean info);
}
