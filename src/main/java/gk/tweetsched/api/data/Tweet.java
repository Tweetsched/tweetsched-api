package gk.tweetsched.api.data;

import java.util.UUID;
/**
 * Tweet class.
 * <p>
 * Date: May 26, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class Tweet {
    private final String id;
    private String message;

    public Tweet(String message) {
        this.id = String.valueOf(UUID.randomUUID());
        this.message = message;
    }

    public Tweet() {
        this.id = String.valueOf(UUID.randomUUID());
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Tweet setMessage(String message) {
        this.message = message;
        return this;
    }
}
