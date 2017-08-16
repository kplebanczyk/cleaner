package cleaner;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

/**
 * Finds all matching files in specified directory
 */
public class FileCrawler implements Runnable{

    private  File startDir;
    private static LinkedBlockingQueue<File> searchResult =  new LinkedBlockingQueue();


    public FileCrawler(final String dir){
        this.startDir = new File(dir);
    }


    @Override
    public void run() {
        searchResult.addAll(search(startDir));
        //System.out.println(FileCrawler.searchResult.size());
        return;
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
            else if (element.isDirectory()) {
                CurrentDirResults.remove(element);
                Thread tt = new Thread(new FileCrawler(element.toString())); // jest wielowątkowy search, ale czy można zrobić kolejkę wyniku jako static ?
                tt.start();
            }
        }

        return CurrentDirResults;
    }

    /**
     * compares File elements against regex condition
     */
    class DirFilter implements FileFilter {
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