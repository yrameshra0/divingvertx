import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;
import rx.functions.Action1;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class MyFirstVerticleTest {
    private Vertx vertx;
    private Integer port;

    /**
     * Before actually going ahead and executing the tests the Verticle under test should be
     * ready for accepting the requests
     */
    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        vertx.deployVerticle(MyFirstVerticle.class.getName(), createDeploymentOptions(), context.asyncAssertSuccess());
    }

    private DeploymentOptions createDeploymentOptions() throws IOException {
        // Making the verticle listen at Test Port
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();

        return new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test(timeout = 1000L)
    public void hello_world_from_my_first_verticle(TestContext context) throws Exception {
        vertx.deployVerticle(ReadVerticle.class.getName()); // Registering ReadVerticle

        Async async = context.async();
        Observable<HttpClientResponse> responseObservable = fetchHttpClientResponseForIndexPage();
        Action1<HttpClientResponse> successResponse = assertIndexSuccessResponse(context, async);
        responseObservable.subscribe(successResponse);
    }

    private Observable<HttpClientResponse> fetchHttpClientResponseForIndexPage() {
        HttpClient httpClient = vertx.createHttpClient();
        return RxHelper.get(httpClient, port, "localhost", "/");
    }

    private Action1<HttpClientResponse> assertIndexSuccessResponse(TestContext context, Async async) {
        return response -> {
            context.assertEquals(response.statusCode(), 200);
            context.assertEquals(response.headers().get("content-type"), "text/html;charset=UTF-8");

            response.bodyHandler(body -> {
                context.assertEquals(body.toString(), "<h1>HELLO THERE FROM -> READ VERTICLE !!</h1>");
                async.complete();
            });
        };
    }

    @Test
    public void fail_when_no_service_verticle_registered(TestContext context) throws Exception {
        Async async = context.async();
        Observable<HttpClientResponse> responseObservable = fetchHttpClientResponseForIndexPage();
        Action1<HttpClientResponse> errorResponse = response -> {
            context.assertEquals(response.statusCode(), 500);
            async.complete();
        };
        responseObservable.subscribe(errorResponse);
    }
}