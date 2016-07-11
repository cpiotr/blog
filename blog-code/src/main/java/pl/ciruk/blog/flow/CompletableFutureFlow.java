package pl.ciruk.blog.flow;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class CompletableFutureFlow {

    static CompletableFuture<Integer> flowWithId(int id, ExecutorService pool) {
        return firstOperation(id, pool)
                .thenCompose(__ -> secondOperation(id, pool))
                .thenCompose(__ -> thirdOperation(id, pool));
    }

    private static CompletableFuture<Integer> firstOperation(int id, ExecutorService pool) {
        return slowOperationAsync(id, 1, pool);
    }

    private static CompletableFuture<Integer> slowOperationAsync(int flow, int step, ExecutorService pool) {
        return CompletableFuture.supplyAsync(
                () -> slowOperation(flow, step),
                pool
        );
    }

    private static int slowOperation(int flow, int step) {
        System.out.println(String.format("[Flow:%d][Step:%d] - %s", flow, step, Thread.currentThread().getName()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().isInterrupted();
            e.printStackTrace();
        }

        return flow;
    }

    private static CompletableFuture<Integer> secondOperation(int id, ExecutorService pool) {
        return slowOperationAsync(id, 2, pool);
    }

    private static CompletableFuture<Integer> thirdOperation(int id, ExecutorService pool) {
        return slowOperationAsync(id, 3, pool);
    }

    static void getFuture(CompletableFuture<Integer> future) {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
