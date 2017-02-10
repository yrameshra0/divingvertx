import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
                .subscribe(response -> assertReplyFromReadVerticle(response, context, async));
    }

    private void assertReplyFromReadVerticle(Message<Object> response, TestContext context, Async async) {
        context.assertEquals(response.body().toString(), "HELLO THERE FROM -> READ VERTICLE !!");
        async.complete();
    }
}
