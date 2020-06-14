package genericDraft;

import comms.Prompt;
import net.dv8tion.jda.api.entities.User;

import java.util.function.Function;

/**
 * Created by micha on 6/13/2020.
 */
public class PickerPrompt implements Prompt {
    @Override
    public String promptToSend() {
        return null;
    }

    @Override
    public Function<String, Boolean> handleResponse() {
        return null;
    }

    @Override
    public User userToPrompt() {
        return null;
    }
}
