package cleaner;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.List;


public class SearchWorker implements Callable<List<Path>> {
    private final Path startDir;
    private List<Path> searchResult;

    public SearchWorker(Path startDir) {
        this.startDir = startDir;
        this.searchResult = new ArrayList<>();
    }

    @Override
    public List<Path> call() {
        Path [] found ;
        try {
            found=Files.walk(startDir,Integer.MAX_VALUE,FileVisitOption.FOLLOW_LINKS).
            filter(path -> {
                return path.toFile().isDirectory()||
                        path.getFileName().toString().toLowerCase().contains(".arw")||
                        path.getFileName().toString().toLowerCase().contains(".raw");
            }).
            distinct().
            toArray(Path[]::new);

            searchResult=Arrays.asList(found);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }



}

