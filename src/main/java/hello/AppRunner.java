package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import util.ParallelTaskExecutor;

import static org.junit.Assert.assertNotNull;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info(".... Fetching books");
        final String isbn1 = "isbn-1234";
        logger.info("isbn-1234 -->" + bookRepository.getByIsbn(isbn1));
//        logger.info("isbn-4567 -->" + bookRepository.getByIsbn("isbn-4567"));



        int threadCount = 10;
        ParallelTaskExecutor executor = new ParallelTaskExecutor(threadCount);

        for (int y = 0; y < threadCount; y++) {
            executor.submit(() -> {
                for (int x = 0; x < 20000; x++) {
                    if (x % 100 == 0)
                        System.err.println(Thread.currentThread().getName() + " get count " + x + " " + bookRepository.getByIsbn(isbn1));
                    try {

                        final Book byIsbn = bookRepository.getByIsbn(isbn1);

                        assertNotNull(Thread.currentThread().getName() + " at count " + x + " Null gelmemesi gelen yerde geldi", byIsbn);
                        System.err.println( Thread.currentThread().getName() + " book --> " + byIsbn);
                    } catch (Throwable e) {
                        System.err.println(e);
                        throw e;
                    }
                    if (x % 10 == 0) {
                        bookRepository.deleteBook(isbn1);
                        System.err.println(Thread.currentThread().getName()+" removed from cache at count "+x);
                    }
                }
                return null;
            });
        }


        executor.waitForTasks();




        logger.info("isbn-1234 -->" + bookRepository.getByIsbn(isbn1));
        logger.info("isbn-4567 -->" + bookRepository.getByIsbn("isbn-4567"));
        logger.info("isbn-1234 -->" + bookRepository.getByIsbn(isbn1));
        logger.info("isbn-1234 -->" + bookRepository.getByIsbn(isbn1));
    }


    public static ExecutorService newNamedFixedThreadPool(String threadNamePrefix, int numberOfThreads, BlockingQueue<Runnable> workQueue) {
        return new ThreadPoolExecutor(numberOfThreads,
                numberOfThreads,
                0L,
                TimeUnit.MILLISECONDS,
                workQueue,
                new NamedThreadFactory(threadNamePrefix));
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

    public static String createThreadName(String prefix, long id) {
        return prefix + "-" + id;
    }

}