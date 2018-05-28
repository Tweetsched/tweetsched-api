package gk.tweetsched.api.util;

/**
 * Constants class.
 * <p>
 * Date: May 28, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class Constants {
    public static final String REDIS_URL = "REDIS_URL";
    public static final String REDIS_PORT = "REDIS_PORT";
    public static final String REDIS_PASSWORD = "REDIS_PASSWORD";

    public static final String PORT = "PORT";

    public static final String CONTENT_TYPE = "content-type";
    public static final String APPLICATION_JSON = "application/json; charset=utf-8";

    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int NOT_FOUND = 404;

    public static final String ROOT_PATH = "/";
    public static final String ASSETS_PATH = "/assets";
    public static final String TWEETS_PATH = "/api/tweets";
    public static final String HEALTHCHECK_PATH = "/api/healthcheck";

    public static final String HEALTHCHECK_STATUS = "status";
    public static final String STATUS_PASS = "pass";

    public static final String ID = "id";
    public static final String MESSAGE = "message";
}
