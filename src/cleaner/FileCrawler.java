package cleaner;

import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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

        FutureTask<List<Path>> firstScanTask = new FutureTask(new SearchWorker(startDir));

        Futures.add(firstScanTask);
        new Thread(firstScanTask).start();
        int i=0;
        try {
            while (!Futures.isEmpty()) {

                List<Future> FeaturesPendingProcessingResults = new ArrayList();
                for (Future f:Futures) {
                    if (f.isDone()&& !f.isCancelled()){
                        FeaturesPendingProcessingResults.add(f);
                    }
                }
                if (FeaturesPendingProcessingResults.size()<1){
                    continue;
                }
                System.out.println("F "+Futures.size()+" P "+FeaturesPendingProcessingResults.size()+ " i "+ i);
/////////////////////////////////////////////////////////////////////////

                for (Future future : FeaturesPendingProcessingResults) {
                    try {
                        List<Path> futureResult = (List<Path>) future.get();
                        Map<Boolean, List<Path>> PathClassifiedAsDir = futureResult.stream().
                                 collect(Collectors.partitioningBy(
                                         r->r.toFile().isDirectory())
                                 );
                        ArrayList<Path> isFile = new ArrayList<>();
                                isFile.addAll(PathClassifiedAsDir.get(false));
                        CumulativeResults.addAll(isFile);



                        for (Path d : PathClassifiedAsDir.get(true)) {
                            System.out.println(d.toString());
                            FutureTask<List<Path>> nextLevelSearch = new FutureTask (new SearchWorker(d));
                            Futures.add(nextLevelSearch);
                            new Thread(nextLevelSearch).start();
                        }
                        Futures.remove(future);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        System.err.println("failed to tet partial results. Future contained: \n" +future.get().toString());

                    }


                }

                FeaturesPendingProcessingResults = null;

            } //end of while != empty
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



