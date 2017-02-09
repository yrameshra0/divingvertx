import io.vertx.rxjava.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.functions.Action1;

@RunWith(VertxUnitRunner.class)
public class ReadVerticleTest {
    private Vertx vertx;

    @Before
    public void setUp() throws Exception {
        vertx = Vertx.vertx();
        vertx.deployVerticle(ReadVerticle.class.getName());
    }

    @Test
    public void responds_to_read_me_event(TestContext context) throws Exception {
        Async async = context.async();
        vertx.eventBus()
                .sendObservable("READ_FROM_ME", null)
                .subscribe(assertReplyFromReadVerticle(context, async));
    }

    private Action1<Message<Object>> assertReplyFromReadVerticle(TestContext context, Async async) {
        return response -> {
            context.assertEquals(response.body().toString(), "HELLO THERE FROM ME");
            async.complete();
        };
    }
}
