package util;

import java.util.concurrent.*;

public class ExecutorsUtils {

    public static ExecutorService newNamedFixedThreadPool(String threadNamePrefix, int numberOfThreads) {
        return Executors.newFixedThreadPool(numberOfThreads, new NamedThreadFactory(threadNamePrefix));
    }

    public static ExecutorService newNamedFixedThreadPool(String threadNamePrefix, int numberOfThreads, BlockingQueue<Runnable> workQueue) {
        return new ThreadPoolExecutor(numberOfThreads,
                numberOfThreads,
                0L,
                TimeUnit.MILLISECONDS,
                workQueue,
                new NamedThreadFactory(threadNamePrefix));
    }

    public static ScheduledExecutorService newNamedScheduledThreadPool(String threadNamePrefix, int numberOfThreads) {
        return Executors.newScheduledThreadPool(numberOfThreads, new NamedThreadFactory(threadNamePrefix));
    }

    public static String createThreadName(String prefix, long id) {
        return prefix + "-" + id;
    }

    public static void destroy(ExecutorService service, long gracefullWaitTimeout) {
        if (service != null) {
            service.shutdown();
            try {
                service.awaitTermination(gracefullWaitTimeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                service.shutdownNow();
            }
        }
    }

    public static class NamedThreadFactory implements ThreadFactory {
        private String threadNamePrefix;

        public NamedThreadFactory(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(createThreadName(threadNamePrefix, thread.getId()));
            return thread;
        }
    }


}
