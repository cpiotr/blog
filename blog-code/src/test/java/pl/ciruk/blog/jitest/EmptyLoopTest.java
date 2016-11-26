package pl.ciruk.blog.jitest;

import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class EmptyLoopTest {
    @Test(timeout = 100_000L)
    public void shouldEliminateEmptyIntLoop() throws Exception {
        for (int i = 0; i < 100; i++) {
            runAndMeasureTime(this::intLoop);
        }
    }

    @Test(timeout = 100_000L)
    public void shouldEliminateEmptyIntToLongLoop() throws Exception {
        for (int i = 0; i < 100; i++) {
            runAndMeasureTime(this::intToLongLoop);
        }
    }

    @Test(timeout = 100_000L)
    public void shouldEliminateEmptyLongLoop() throws Exception {
        for (int i = 0; i < 100; i++) {
            runAndMeasureTime(this::longLoop);
        }
    }

    private void intLoop() {
        for (int i = 0; i < 20_000_000; i++) {

        }
    }

    private void intToLongLoop() {
        for (int i = 0; i < 2_000_000_000L; i++) {
            // no-op
        }
    }

    private void longLoop() {
        for (long i = 0; i < 2_000_000_000L; i++) {
            // no-op
        }
    }

    private void runAndMeasureTime(Runnable runnable) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        runnable.run();
        stopwatch.stop();
        System.out.printf("%d ms%n", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
}
