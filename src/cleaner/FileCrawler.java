package cleaner;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;


public class FileCrawler implements Callable<LinkedBlockingQueue<File>> {
    private ExecutorService executor;
    private List<Future<LinkedBlockingQueue<File>>> Jobs = new CopyOnWriteArrayList<Future<LinkedBlockingQueue<File>>>();
    private LinkedBlockingQueue<File> searchResults = new LinkedBlockingQueue<File>();
    private volatile int ActiveJobCount = 0;
    @Override
    public LinkedBlockingQueue<File> call(){
       executor = Executors.newCachedThreadPool();
       Jobs.add(executor.submit(new SearchWorker("D:\\Moje Obrazy\\"))) ;
       ActiveJobCount++ ;


       while (ActiveJobCount>0) {
           for (Future future : Jobs) {
               if (future.isDone()) {
                   LinkedBlockingQueue<File> FutureResults = new LinkedBlockingQueue<File>();
                   try {
                       FutureResults.addAll(future.get());
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   } catch (ExecutionException e) {
                       e.printStackTrace();
                   }
                   for (File diritem : FutureResults) {
                       if (diritem.isDirectory()) {
                           Jobs.add(executor.submit(new SearchWorker(diritem.toString())));
                           FutureResults.remove(diritem);
                       }
                   }
                   searchResults.addAll(FutureResults);


               } else {
                   ActiveJobCount--;
               }
           }

       }
       return searchResults;
    }





}
