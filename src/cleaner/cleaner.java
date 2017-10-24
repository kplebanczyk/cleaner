package cleaner;

        import org.junit.Test;

        import javax.swing.filechooser.FileSystemView;
        import java.io.File;
        import java.nio.file.Path;
        import java.nio.file.Paths;


public class cleaner {
    private static String startDirectory;
    public static void main(String[] args) {
        startDirectory = "D:\\Moje Obrazy\\foto_tymczasowe";
        HomeImageCollection imageCollection = new HomeImageCollection(Paths.get(startDirectory));
        imageCollection.fixRawDirectoryStructure();
    }
}
