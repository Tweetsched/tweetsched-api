package gk.tweetsched.api.router;

import gk.tweetsched.api.repository.TweetRepository;
import gk.tweetsched.dto.Tweet;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.shiro.ShiroAuth;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.ext.auth.shiro.ShiroAuthRealmType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.FormLoginHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static gk.tweetsched.api.util.Constants.REDIS_URL;
import static gk.tweetsched.api.util.Constants.REDIS_PORT;
import static gk.tweetsched.api.util.Constants.REDIS_PASSWORD;
import static gk.tweetsched.api.util.Constants.LOGIN;
import static gk.tweetsched.api.util.Constants.ROOT_PATH;
import static gk.tweetsched.api.util.Constants.ASSETS_PATH;
import static gk.tweetsched.api.util.Constants.ASSETS;
import static gk.tweetsched.api.util.Constants.TWEETS_PATH;
import static gk.tweetsched.api.util.Constants.LOGIN_AUTH;
import static gk.tweetsched.api.util.Constants.LOGOUT;
import static gk.tweetsched.api.util.Constants.HEALTHCHECK_PATH;
import static gk.tweetsched.api.util.Constants.CONTENT_TYPE;
import static gk.tweetsched.api.util.Constants.APPLICATION_JSON;
import static gk.tweetsched.api.util.Constants.CREATED;
import static gk.tweetsched.api.util.Constants.ID;
import static gk.tweetsched.api.util.Constants.NO_CONTENT;
import static gk.tweetsched.api.util.Constants.NOT_FOUND;
import static gk.tweetsched.api.util.Constants.FOUND;
import static gk.tweetsched.api.util.Constants.OK;
import static gk.tweetsched.api.util.Constants.STATUS_PASS;
import static gk.tweetsched.api.util.Constants.HEALTHCHECK_STATUS;
import static gk.tweetsched.api.util.Constants.MESSAGE;
import static gk.tweetsched.api.util.Constants.MAIN_PAGE;
import static gk.tweetsched.api.util.Constants.LOGIN_PAGE;
import static gk.tweetsched.api.util.Constants.FAVICON;
import static gk.tweetsched.api.util.Constants.LOCATION;

/**
 * ResourceRouter class.
 * <p>
 * Date: May 29, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class ResourceRouter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceRouter.class);
    private TweetRepository tweetRepository = new TweetRepository(
            System.getenv(REDIS_URL),
            Integer.parseInt(System.getenv(REDIS_PORT)),
            System.getenv(REDIS_PASSWORD));

    public ResourceRouter() {}

    public void configureRoutes(Router router, Vertx vertx) {
        AuthProvider auth = ShiroAuth.create(vertx, new ShiroAuthOptions()
                .setType(ShiroAuthRealmType.PROPERTIES)
                .setConfig(new JsonObject().put("properties_path", "classpath:users.properties")));
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(UserSessionHandler.create(auth));
        AuthHandler authHandler = RedirectAuthHandler.create(auth, LOGIN);
        router.route(ROOT_PATH).handler(authHandler);
        router.route(ASSETS_PATH + MAIN_PAGE).handler(authHandler);
        router.route(TWEETS_PATH + "/*").handler(authHandler);
        router.get(LOGIN).handler(rc -> {rc.reroute(ASSETS_PATH + LOGIN_PAGE);});
        router.post(LOGIN_AUTH).handler(FormLoginHandler.create(auth));
        router.get(LOGOUT).handler(this::logout);
        router.route(ROOT_PATH).handler(rc -> {rc.reroute(ASSETS_PATH + MAIN_PAGE);});
        router.route(ASSETS_PATH + "/*").handler(StaticHandler.create(ASSETS));
        router.get(TWEETS_PATH).handler(this::getAll);
        router.get(TWEETS_PATH + "/:id").handler(this::getOne);
        router.route(TWEETS_PATH + "*").handler(BodyHandler.create());
        router.post(TWEETS_PATH).handler(this::addOne);
        router.delete(TWEETS_PATH + "/:id").handler(this::deleteOne);
        router.put(TWEETS_PATH + "/:id").handler(this::updateOne);
        router.get(HEALTHCHECK_PATH).handler(this::checkHealth);
        router.get(FAVICON).handler(this::getFavicon);
    }

    private void logout(RoutingContext context) {
        context.clearUser();
        context.response()
                .setStatusCode(FOUND)
                .putHeader(LOCATION, LOGIN)
                .end();
    }

    private void getAll(RoutingContext context) {
        context.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(tweetRepository.getAll()));
        LOGGER.info("Get all tweets");
    }

    private void addOne(RoutingContext context) {
        Tweet tweet = context.getBodyAsJson().mapTo(Tweet.class);
        tweet.setId(String.valueOf(UUID.randomUUID()));
        tweetRepository.save(tweet);
        context.response()
                .setStatusCode(CREATED)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(tweet));
        LOGGER.info("Add a new tweet");
    }

    private void deleteOne(RoutingContext context) {
        tweetRepository.delete(context.request().getParam(ID));
        context.response()
                .setStatusCode(NO_CONTENT)
                .end();
        LOGGER.info("Delete tweet");
    }

    private void getOne(RoutingContext context) {
        Tweet tweet = tweetRepository.get(context.request().getParam(ID));
        if (tweet == null) {
            context.response().setStatusCode(NOT_FOUND).end();
            LOGGER.info("Tweet was not found");
        } else {
            context.response()
                    .setStatusCode(OK)
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .end(Json.encodePrettily(tweet));
            LOGGER.info("Get tweet");
        }
    }

    private void updateOne(RoutingContext context) {
        Tweet tweet = tweetRepository.get(context.request().getParam(ID));
        if (tweet == null) {
            context.response().setStatusCode(NOT_FOUND).end();
        } else {
            tweet.setMessage(context.getBodyAsJson().getString(MESSAGE));
            tweetRepository.save(tweet);
            context.response()
                    .setStatusCode(OK)
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .end(Json.encodePrettily(tweet));
            LOGGER.info("Update tweet");
        }
    }

    private void checkHealth(RoutingContext context) {
        Map<String, String> response = new HashMap<>();
        response.put(HEALTHCHECK_STATUS, STATUS_PASS);
        context.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(response));
        LOGGER.info("Healthcheck");
    }

    private void getFavicon(RoutingContext context) {
        context.response().sendFile(ASSETS + FAVICON).close();
    }
}
