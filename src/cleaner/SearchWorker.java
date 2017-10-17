package cleaner;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Finds all matching files in specified directory
 */
public class SearchWorker implements Callable<ArrayList<Path>> {
    private final Path startDir;
    private ArrayList<Path> searchResult;

    public SearchWorker(String dir) {
        this.startDir = Paths.get(dir);
    }

    @Override
    public ArrayList<Path> call() {
        search();
        return searchResult;
    }


    private void search() {
         try(DirectoryStream<Path> stream = Files.newDirectoryStream(startDir,"{*.ARW,*.DNG}")){
            for (Path dir:stream) {
                searchResult.add(dir);
            }
        }
        catch(IOException e){
             e.printStackTrace();
        }

    }


}

