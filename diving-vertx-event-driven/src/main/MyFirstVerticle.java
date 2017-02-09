import io.vertx.core.Future;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;

public class MyFirstVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
        router.route("/").handler(this::communicateWithReadService);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listenObservable(getOrDefaultPort())
                .subscribe(onSuccess -> startFuture.complete(), startFuture::fail);
    }

    private Integer getOrDefaultPort() {
        return config().getInteger("http.port", 8080);
    }

    private void communicateWithReadService(RoutingContext ctx) {
        vertx.eventBus()
                .sendObservable("READ_FROM_ME", null)
                .subscribe(
                        onSuccess -> createServerResponse(ctx, onSuccess),
                        onError -> ctx.fail(500)
                );
    }

    private void createServerResponse(RoutingContext ctx, Message<Object> message) {
        ctx.response()
                .putHeader("content-type", "text/html;charset=UTF-8")
                .end(String.format("<h1>%s</h1>", message.body()));
    }
}
