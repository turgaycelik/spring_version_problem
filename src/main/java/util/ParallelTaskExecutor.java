package util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class ParallelTaskExecutor {
    private List<Future> tasks = new ArrayList<>();
    private ExecutorService executors;
    private BlockingQueue<Runnable> workQueue;

    public ParallelTaskExecutor() {
        this(10);
    }

    public ParallelTaskExecutor(int numberOfThreads) {
        this(numberOfThreads, Integer.MAX_VALUE);
    }

    public ParallelTaskExecutor(int numberOfThreads, int maxQueSize) {
        workQueue = new LinkedBlockingQueue<>(maxQueSize);
        executors = ExecutorsUtils.newNamedFixedThreadPool("ParallelTaskExecutor", numberOfThreads, workQueue);
    }

    public void submit(Callable callable) {
        Future f = executors.submit(callable);
        tasks.add(f);
    }

    public void waitForTasks() throws InterruptedException, ExecutionException {
        try {
            for (Future task : tasks) {
                task.get();
                //TODO should we wait for all tasks to finish if an exception occurs?
            }
        } finally {
            tasks.clear();
            ExecutorsUtils.destroy(executors, 2000);
        }
    }

    public int getQueueSize() {
        return workQueue.size();
    }
}
