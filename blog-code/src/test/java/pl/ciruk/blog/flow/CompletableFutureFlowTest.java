package pl.ciruk.blog.flow;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class CompletableFutureFlowTest {

    public static final int POOL_SIZE = 4;
    private ExecutorService pool;

    @Test
    public void testFlowWithCommonForkJoinPool() throws Exception {
        pool = ForkJoinPool.commonPool();
        List<CompletableFuture<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            futures.add(
                    CompletableFutureFlow.flowWithId(i, pool));
        }

        futures.forEach(CompletableFutureFlow::getFuture);
    }

    @Test
    public void testFlowWithFixedThreadPool() throws Exception {
        pool = Executors.newFixedThreadPool(POOL_SIZE);

        List<CompletableFuture<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            futures.add(
                    CompletableFutureFlow.flowWithId(i, pool));
        }

        futures.forEach(CompletableFutureFlow::getFuture);
    }

    @Test
    public void testFlowWithCustomForkJoinPool() throws Exception {
        pool = new ForkJoinPool(POOL_SIZE);

        List<CompletableFuture<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            futures.add(
                    CompletableFutureFlow.flowWithId(i, pool));
        }

        futures.forEach(CompletableFutureFlow::getFuture);
    }

    @After
    public void cleanUp() throws InterruptedException {
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.SECONDS);
    }

}