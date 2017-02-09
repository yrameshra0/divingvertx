import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MyFirstVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
        router.route("/").handler(this::communicateWithReadService);

        vertx.createHttpServer().requestHandler(router::accept)
                .listen(config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded())
                                startFuture.complete();
                            else
                                startFuture.fail(result.cause());
                        });
    }

    private void communicateWithReadService(RoutingContext ctx) {
        // route REST request to event bus
        vertx.eventBus().
                send("READ_FROM_ME", null,
                        responseHandler -> defaultResponse(ctx, responseHandler));
    }

    private void defaultResponse(RoutingContext ctx, AsyncResult<Message<Object>> responseHandler) {
        if (responseHandler.failed()) {
            ctx.fail(500);
            return;
        }

        // respond to REST request
        final Message<Object> result = responseHandler.result();

        HttpServerResponse response = ctx.response();
        response.putHeader("content-type", "text/html;charset=UTF-8")
                .end(String.format("<h1>%s</h1>", result.body()));

    }

}
