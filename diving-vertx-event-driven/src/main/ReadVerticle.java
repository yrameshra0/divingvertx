import io.vertx.core.Future;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.MessageConsumer;

public class ReadVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        MessageConsumer<Object> consumer = vertx.eventBus().consumer("READ_FROM_ME");

        consumer.toObservable().subscribe(listener -> {
            listener.reply("HELLO THERE FROM -> READ VERTICLE !!");
            // Done primarily because of this issue -- https://github.com/eclipse/vert.x/issues/1625
            consumer.unregister();
        });

        startFuture.complete();
    }
}
