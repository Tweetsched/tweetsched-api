package gk.tweetsched.api;

import gk.tweetsched.api.data.Tweet;
import gk.tweetsched.api.repository.TweetRepository;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * ApiVerticle class.
 * <p>
 * Date: May 26, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class ApiVerticle extends AbstractVerticle {
    private static final String CONTENT_TYPE = "content-type";
    private static final String APPLICATION_JSON = "application/json; charset=utf-8";
    private static final String PORT_PROPERTY = "HTTP_PORT";
    private static final int DEFAULT_PORT = 8080;
    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int NO_CONTENT = 204;
    private static final int BAD_REQUEST = 400;
    private static final int NOT_FOUND = 404;
    private TweetRepository tweetService = new TweetRepository();

    @Override
    public void start(Future<Void> fut) {
        Router router = Router.router(vertx);
        configureRoutes(router);

        ConfigRetriever.create(vertx).getConfig(
            config -> {
                if (config.failed()) {
                    fut.fail(config.cause());
                } else {
                    vertx.createHttpServer()
                            .requestHandler(router::accept)
                            .listen(
                                config.result().getInteger(PORT_PROPERTY, DEFAULT_PORT),
                                        result -> {
                                            if (result.succeeded()) {
                                                fut.complete();
                                            } else {
                                                fut.fail(result.cause());
                                            }
                                        }
                            );
                }
            }
        );
    }

    private void configureRoutes(Router router) {
        router.route("/").handler(routingContext -> {
            routingContext.reroute("/assets/index.html");
        });
        router.route("/assets/*").handler(StaticHandler.create("assets"));
        router.get("/api/tweets").handler(this::getAll);
        router.get("/api/tweets/:id").handler(this::getOne);
        router.route("/api/tweets*").handler(BodyHandler.create());
        router.post("/api/tweets").handler(this::addOne);
        router.delete("/api/tweets/:id").handler(this::deleteOne);
        router.put("/api/tweets/:id").handler(this::updateOne);
        router.get("/api/health").handler(this::checkHealth);
    }

    private void getAll(RoutingContext routingContext) {
        routingContext.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(tweetService.getAll()));
    }

    private void addOne(RoutingContext routingContext) {
        Tweet tweet = routingContext.getBodyAsJson().mapTo(Tweet.class);
        tweetService.save(tweet);
        routingContext.response()
                .setStatusCode(CREATED)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(tweet));
    }

    private void deleteOne(RoutingContext routingContext) {
        tweetService.delete(routingContext.request().getParam("id"));
        routingContext.response().setStatusCode(NO_CONTENT).end();
    }

    private void getOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        Tweet tweet = tweetService.get(id);
        if (tweet == null) {
            routingContext.response().setStatusCode(NOT_FOUND).end();
        } else {
            routingContext.response()
                    .setStatusCode(OK)
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .end(Json.encodePrettily(tweet));
        }
    }

    private void updateOne(RoutingContext routingContext) {
        Tweet tweet = tweetService.get(routingContext.request().getParam("id"));
        if (tweet == null) {
            routingContext.response().setStatusCode(NOT_FOUND).end();
        } else {
            JsonObject body = routingContext.getBodyAsJson();
            tweet.setMessage(body.getString("message"));
            tweetService.save(tweet);
            routingContext.response()
                    .setStatusCode(OK)
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .end(Json.encodePrettily(tweet));
        }
    }

    private void checkHealth(RoutingContext routingContext) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "Connection successful");
        routingContext.response()
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .end(Json.encodePrettily(response));
    }
}
