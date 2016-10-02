import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by apple on 2016/9/25.
 */
public class FutureTest {


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        new FutureTest().f();

    }

    private void f() throws ExecutionException, InterruptedException {

        RPCFuture future = new RPCFuture();

        System.out.println("hello......");
       // future.done();
        System.out.println("ss===="+future.isDone());
        while (!future.isDone()) {
            doSomthing();
        }

        System.out.println("hhhhhh");
        Object obj = future.get();
        System.out.println("wwww");
        System.out.println("obj===" + obj);

    }

    private static void doSomthing() {
        System.out.println(" do some thing haha");
    }

}

class RPCFuture implements Future<Object>{


    private Sync sync;

    public RPCFuture(){
        this.sync = new Sync();
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        return "hello java";
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
           return "hello xxx";
        } else {
            throw new RuntimeException("exception ");
        }
    }

    public void done(){
        sync.release(1);
    }

    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;

        protected boolean tryAcquire(int acquires) {

            return acquires == done ? true : false;
        }

        protected boolean tryRelease(int releases) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }
}
