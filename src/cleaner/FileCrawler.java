package cleaner;

import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.*;

import java.util.function.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class FileCrawler  {
    private ArrayList<Future> Futures = new ArrayList<>();
    private ArrayList<Path> CumulativeResults = new ArrayList<>();
    private String startDir;

    public FileCrawler(String startDir) {
        this.startDir = startDir;
    }


    public List<Path> getRAW() {
        ExecutorService executor = Executors.newCachedThreadPool();

        //Future<ArrayList<Path>> TopLevelResult = executor.submit(new SearchWorker(startDir));

        Futures.add(executor.submit(new SearchWorker(startDir)));


        try {

            while (!Futures.isEmpty()) {

                Map<Boolean, List<Path>> paths =
                        Futures.stream().
                        filter(future -> future.isDone()).
                        filter(future -> !future.isCancelled()).
                        map(future ->
                        {
                            try {
                                ArrayList<Path> partial = (ArrayList)future.get();
                                return partial;
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                System.err.println("Future get() method error");
                                return null;
                            }
                        }).
                        flatMap(Collection::stream).
                        collect(Collectors.partitioningBy( p->p.toFile().isDirectory()) );


                }


                //ArrayList<Path> partialResults

            //use dirs and nondirs
                //CumulativeResults.addAll(partialResults);


            //Futures.remove(future);

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);

        }
        catch (Exception e){
            e.printStackTrace();
        }


        return CumulativeResults;
    }

    public String getStartDir() {
        return startDir;
    }
}



