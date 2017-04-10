package pl.ciruk.blog.jitest;

import org.junit.Test;

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
    public void shouldEliminateEmptyDoubleLoop() throws Exception {
        for (int i = 0; i < 100; i++) {
            runAndMeasureTime(this::doubleLoop);
        }
    }

    private void intLoop() {
        for (int i = 0; i < 20_000_000; i++) {

        }
    }

    private void intToLongLoop() {
        for (int i = 0; i < 200_000_000L; i++) {
            // no-op
        }
    }

    private void doubleLoop() {
        for (double i = 0.0; i < 20_000_000.0; i+=1.0) {
            // no-op
        }
    }

    @Test(timeout = 100_000L)
    public void shouldEliminateEmptyLongLoop() throws Exception {
        for (int i = 0; i < 100; i++) {
            runAndMeasureTime(this::longLoop);
        }
    }

    private void longLoop() {
        for (long i = 0; i < 20_000_000L; i++) {
            // no-op
        }
    }

    private void runAndMeasureTime(Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        System.out.printf("%d ms%n", (System.currentTimeMillis() - start));
    }
}
