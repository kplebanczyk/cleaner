package cleaner;

import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import java.util.function.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class FileCrawler  {
    private ArrayList<Future> Futures = new ArrayList<>();
    private ArrayList<Path> CumulativeResults = new ArrayList<>();
    private Path startDir;

    public FileCrawler(String startDir) {
        this.startDir = Paths.get(startDir);
    }


    public List<Path> getRAW() {
        ExecutorService executor = Executors.newCachedThreadPool();

        //Future<ArrayList<Path>> TopLevelResult = executor.submit(new SearchWorker(startDir));

        Futures.add(executor.submit(new SearchWorker(startDir)));


        try {

            while (!Futures.isEmpty()) {

                List<Future<Path>> FeaturesPendingProcessingResuts =
                        (List)Arrays.asList(
                                Futures.stream().
                        filter(f -> f.isDone()).
                        filter(f -> !f.isCancelled()).
                        toArray()
                );

                for (Future <Path> future : FeaturesPendingProcessingResuts) {
                    try {
                        ArrayList<Path> futureResult = (ArrayList)future.get();
                        Map<Boolean, List<Path>> PathClassifiedAsDir = futureResult.stream().
                                 collect(Collectors.partitioningBy(
                                         r->r.toFile().isDirectory())
                                 );
                        ArrayList<Path> isDir = new ArrayList<>();
                                isDir.addAll(PathClassifiedAsDir.get(false));
                        CumulativeResults.addAll(isDir);

                        for (Path d : isDir) {
                            FutureTask<ArrayList<Path>> nextLevelSearch =
                                    new FutureTask<ArrayList<Path>>(new SearchWorker(d));
                            Futures.add(nextLevelSearch);
                        }

                    }
                    catch (Exception e){
                        e.printStackTrace();
                        System.err.println("failed to tet partial results");
                    }
                    Futures.remove(future);
                }




            } //end of while != empty
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return CumulativeResults;
    }

    public String getStartDir() {
        return startDir.toString();
    }
}



