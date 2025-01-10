package dev.yxy.async;

import dev.yxy.util.SleepUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * @version 1.0
 * @since 2025-01-10
 */
public class CompletionServiceTests {

    @Test
    public void testCompletionServiceWithNonNullResults() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        List<Callable<String>> tasks = Arrays.asList(
                () -> {
                    SleepUtils.millisecond(1000);
                    return "Result 1";
                },
                () -> {
                    SleepUtils.millisecond(500);
                    return "Result 2";
                },
                () -> null
        );

        tasks.forEach(completionService::submit);
        List<String> actualResults = new ArrayList<>();
        int n = tasks.size();
        for (int i = 0; i < n; i++) {
            try {
                Future<String> future = completionService.take();
                String result = future.get();
                actualResults.add(result);
            } catch (InterruptedException | ExecutionException e) {
                Assert.fail();
            }
        }

        List<String> expectedResults = Arrays.asList(null, "Result 2", "Result 1");
        Assert.assertEquals(expectedResults, actualResults);

        executor.shutdown();
    }

    @Test
    public void testCompletionServiceWithFirstNonNullResult() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        List<Callable<String>> tasks = Arrays.asList(
                () -> {
                    SleepUtils.millisecond(1000);
                    throw new RuntimeException("Exception in Task 1");
                },
                () -> {
                    SleepUtils.millisecond(500);
                    return "Result 2";
                },
                () -> "Result 3"
        );

        int n = tasks.size();
        List<Future<String>> futures = new ArrayList<>(n);
        String result = null;

        try {
            for (Callable<String> task : tasks) {
                futures.add(completionService.submit(task));
            }

            for (int i = 0; i < n; i++) {
                try {
                    Future<String> future = completionService.take();
                    String res = future.get();
                    if (res != null) {
                        result = res;
                        break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Assert.fail();
                }
            }
        } finally {
            for (Future<String> future : futures) {
                future.cancel(true);
            }
        }

        Assert.assertEquals("Result 3", result);
        SleepUtils.millisecond(2000);

        executor.shutdown();
    }
}
