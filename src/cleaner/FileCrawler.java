package cleaner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;


public class FileCrawler implements Callable<LinkedBlockingQueue<File>> {
    private ExecutorService executor;
    private List<Future> Jobs = new CopyOnWriteArrayList<Future>();
    private LinkedBlockingQueue<File> CumulativedResults = new LinkedBlockingQueue<File>();



    @Override
    public LinkedBlockingQueue<File> call(){
       executor = Executors.newCachedThreadPool();
       Jobs.add(executor.submit(new SearchWorker("D:\\Moje Obrazy\\"))) ;
       int ActiveJobCount = 1;

       while (ActiveJobCount>0) {
            for (Future future : Jobs) {
               if (future.isDone()) {
                   List<File> JobFindings  = new CopyOnWriteArrayList<File>();
                   try {
                       JobFindings.addAll((Collection<? extends File>) future.get());
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   } catch (ExecutionException e) {
                       e.printStackTrace();
                   }
                   for (File diritem : JobFindings) {
                       if (diritem.isDirectory()) {
                           Jobs.add(executor.submit(new SearchWorker(diritem.toString())));
                           ActiveJobCount++ ;
                           //System.out.println("child "+diritem.toString());
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
       return CumulativedResults;
    }


    public static void main(String[] args) {
        FileCrawler crawler = new FileCrawler();
        List<File> output = new ArrayList<File>();
        output.addAll(crawler.call());
//        System.out.println(output.size());
//        for (File file:output) {
//            System.out.println(file.toString());
//        }

    }


}
