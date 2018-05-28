package gk.tweetsched.api;

import gk.tweetsched.api.ApiVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * ApiVerticleTest class.
 * <p>
 * Date: May 26, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
//@RunWith(VertxUnitRunner.class)
public class ApiVerticleTest {
//    private int port = 8081;
//    private Vertx vertx;
//
//    @Before
//    public void setUp(TestContext context) throws IOException {
//        vertx = Vertx.vertx();
//        ServerSocket socket = new ServerSocket(0);
//        port = socket.getLocalPort();
//        socket.close();
//
//        DeploymentOptions options = new DeploymentOptions()
//                .setConfig(new JsonObject().put(PORT_PROPERTY, port));
//        vertx.deployVerticle(ApiVerticle.class.getName(), options, context.asyncAssertSuccess());
//    }
//
//    @After
//    public void tearDown(TestContext context) {
//        vertx.close(context.asyncAssertSuccess());
//    }
//
//    @Test
//    public void testMyApplication(TestContext context) {
//        final Async async = context.async();
//
//        vertx.createHttpClient().getNow(port, "localhost", "/api/health",
//                response ->
//                        response.handler(body -> {
//                            context.assertTrue(body.toString().contains("Connection successful"));
//                            async.complete();
//                        }));
//    }
}