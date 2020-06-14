package comms;

import net.dv8tion.jda.api.entities.User;

import java.util.function.Function;

/**
 * Created by micha on 6/13/2020.
 */
public interface Prompt {
    public String promptToSend();
    public Function<String, Boolean> handleResponse();
    public User userToPrompt();
}
