package gk.tweetsched.api;

import gk.tweetsched.api.data.Tweet;
import gk.tweetsched.api.repository.TweetRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashMap;
import java.util.Map;

import static gk.tweetsched.api.util.Constants.CONTENT_TYPE;
import static gk.tweetsched.api.util.Constants.APPLICATION_JSON;
import static gk.tweetsched.api.util.Constants.OK;
import static gk.tweetsched.api.util.Constants.CREATED;
import static gk.tweetsched.api.util.Constants.NO_CONTENT;
import static gk.tweetsched.api.util.Constants.NOT_FOUND;
import static gk.tweetsched.api.util.Constants.ID;
import static gk.tweetsched.api.util.Constants.ROOT_PATH;
import static gk.tweetsched.api.util.Constants.ASSETS_PATH;
import static gk.tweetsched.api.util.Constants.TWEETS_PATH;
import static gk.tweetsched.api.util.Constants.HEALTHCHECK_PATH;
import static gk.tweetsched.api.util.Constants.HEALTHCHECK_STATUS;
import static gk.tweetsched.api.util.Constants.STATUS_PASS;
import static gk.tweetsched.api.util.Constants.MESSAGE;
import static gk.tweetsched.api.util.Constants.REDIS_URL;
import static gk.tweetsched.api.util.Constants.REDIS_PORT;
import static gk.tweetsched.api.util.Constants.REDIS_PASSWORD;
import static gk.tweetsched.api.util.Constants.PORT;

/**
 * ApiVerticle class.
 * <p>
 * Date: May 26, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class ApiVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiVerticle.class);
    private TweetRepository tweetRepository = new TweetRepository(
            System.getenv(REDIS_URL),
            Integer.parseInt(System.getenv(REDIS_PORT)),
            System.getenv(REDIS_PASSWORD));

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(ApiVerticle.class.getName());
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        configureRoutes(router);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(Integer.parseInt(System.getenv(PORT)));
        LOGGER.info("API initialized");
    }

    private void configureRoutes(Router router) {
        router.route(ROOT_PATH).handler(rc -> {rc.reroute(ASSETS_PATH + "/index.html");});
        router.route(ASSETS_PATH + "/*").handler(StaticHandler.create("assets"));
        router.get(TWEETS_PATH).handler(this::getAll);
        router.get(TWEETS_PATH + "/:id").handler(this::getOne);
        router.route(TWEETS_PATH + "*").handler(BodyHandler.create());
        router.post(TWEETS_PATH).handler(this::addOne);
        router.delete(TWEETS_PATH + "/:id").handler(this::deleteOne);
        router.put(TWEETS_PATH + "/:id").handler(this::updateOne);
        router.get(HEALTHCHECK_PATH).handler(this::checkHealth);
    }

    private void getAll(RoutingContext routingContext) {
        routingContext.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(tweetRepository.getAll()));
        LOGGER.info("Get all tweets");
    }

    private void addOne(RoutingContext routingContext) {
        Tweet tweet = routingContext.getBodyAsJson().mapTo(Tweet.class);
        tweetRepository.save(tweet);
        routingContext.response()
                .setStatusCode(CREATED)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(tweet));
        LOGGER.info("Add a new tweet");
    }

    private void deleteOne(RoutingContext routingContext) {
        tweetRepository.delete(routingContext.request().getParam(ID));
        routingContext.response().setStatusCode(NO_CONTENT).end();
        LOGGER.info("Delete tweet");
    }

    private void getOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam(ID);
        Tweet tweet = tweetRepository.get(id);
        if (tweet == null) {
            routingContext.response().setStatusCode(NOT_FOUND).end();
            LOGGER.info("Tweet was not found");
        } else {
            routingContext.response()
                    .setStatusCode(OK)
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .end(Json.encodePrettily(tweet));
            LOGGER.info("Get tweet");
        }
    }

    private void updateOne(RoutingContext routingContext) {
        Tweet tweet = tweetRepository.get(routingContext.request().getParam(ID));
        if (tweet == null) {
            routingContext.response().setStatusCode(NOT_FOUND).end();
        } else {
            JsonObject body = routingContext.getBodyAsJson();
            tweet.setMessage(body.getString(MESSAGE));
            tweetRepository.save(tweet);
            routingContext.response()
                    .setStatusCode(OK)
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .end(Json.encodePrettily(tweet));
            LOGGER.info("Update tweet");
        }
    }

    private void checkHealth(RoutingContext routingContext) {
        Map<String, String> response = new HashMap<>();
        response.put(HEALTHCHECK_STATUS, STATUS_PASS);
        routingContext.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(response));
        LOGGER.info("Healthcheck");
    }
}
