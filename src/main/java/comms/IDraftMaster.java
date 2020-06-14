package comms;

/**
 * Created by micha on 6/14/2020.
 */
public interface IDraftMaster {
    void publishToAll(String s);

    void abortDraft();
}
