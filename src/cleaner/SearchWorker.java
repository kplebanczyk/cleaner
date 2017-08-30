package cleaner;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

/**
 * Finds all matching files in specified directory
 */
public class SearchWorker implements Callable<LinkedBlockingQueue<File>> {

    private final File startDir;
    private LinkedBlockingQueue<File> searchResult;


    public SearchWorker(String dir){
        startDir = new File(dir);
    }


    @Override
    public LinkedBlockingQueue<File> call(){
        searchResult =  new LinkedBlockingQueue<File>();
        searchResult.addAll(search(startDir));
        return searchResult;
    }


    /**
     *Search for files matching pattern in a given directory, return only files matching condition. Works multithreaded,
     * spawning new thread for every subdirectory
     */
    private LinkedBlockingQueue<File> search(File directory){
        LinkedBlockingQueue<File> CurrentDirResults = new LinkedBlockingQueue();
        CurrentDirResults.addAll( Arrays.asList( directory.listFiles() ));

        DirFilter filter = new DirFilter("\\w+[\\.]{1}(DNG|ARW)");//zmienić regex na specyficzny
        for (File element:CurrentDirResults ) {
            if (element.isFile() && !filter.accept(element)){
                CurrentDirResults.remove(element) ;
            }
        }
        return CurrentDirResults;
    }

    /**
     * compares File elements against regex condition
     */
    private class DirFilter implements FileFilter {
        private Pattern pattern;

        public DirFilter(String  regex) {
            this.pattern = Pattern.compile(regex);
        }

        @Override
        public boolean accept(File pathname) {
            return pattern.matcher(pathname.getName().toString()).matches();
        }
    }

}