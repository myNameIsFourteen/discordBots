package trains1846;

/**
 * Created by micha on 6/7/2020.
 */
public interface MessagePublisher {
    void publishToAll(String message);
    void publishToPlayer(String message, int player);

    void abortDraft();

    String mentionPlayer(int activePlayer);
}
