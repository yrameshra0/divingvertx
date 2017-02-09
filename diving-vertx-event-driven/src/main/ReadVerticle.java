import io.vertx.core.Future;
import io.vertx.rxjava.core.AbstractVerticle;

public class ReadVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus()
                .consumer("READ_FROM_ME")
                .handler(message -> message.reply("HELLO THERE FROM ME"));

        startFuture.complete();
    }
}
