package gk.tweetsched.api;

import gk.tweetsched.api.router.ResourceRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
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
    private ResourceRouter resourceRouter = new ResourceRouter();

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(ApiVerticle.class.getName());
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        resourceRouter.configureRoutes(router);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(Integer.parseInt(System.getenv(PORT)));
        LOGGER.info("API initialized");
    }
}
