package cleaner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;


public class FileCrawler implements Callable<LinkedBlockingQueue<File>> {
    private List<Future> Jobs = new CopyOnWriteArrayList<Future>();
    private LinkedBlockingQueue<File> CumulativedResults = new LinkedBlockingQueue<File>();
    private String start;

    public FileCrawler(String whereToStart) {
        this.start=whereToStart;
    }

    @Override
    public LinkedBlockingQueue<File> call() {
        ExecutorService executor = Executors.newCachedThreadPool();
        Jobs.add(executor.submit(new SearchWorker(start)));
        int ActiveJobCount = 1;
        try {
            while (ActiveJobCount > 0) {
                for (Future future : Jobs) {
                    if (future.isDone()) {
                        List<File> JobFindings = new CopyOnWriteArrayList<File>();
                        JobFindings.addAll((Collection<? extends File>) future.get());

                        for (File diritem : JobFindings) {
                            if (diritem.isDirectory()) {
                                Jobs.add(executor.submit(new SearchWorker(diritem.toString())));
                                ActiveJobCount++;
                                JobFindings.remove(diritem);
                            }
                        }
                        CumulativedResults.addAll(JobFindings);
                        Jobs.remove(future);
                        ActiveJobCount--;
                    }
                }
            }
            executor.shutdown();
            executor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return CumulativedResults;
    }
}
