package cleaner;

        import javax.swing.filechooser.FileSystemView;
        import java.io.File;
        import java.nio.file.Path;


public class cleaner {

    public static void main(String[] args) {
        FileSystemView.getFileSystemView().getHomeDirectory();
        FileCrawler crawler = new FileCrawler("D:\\Moje Obrazy\\2011");
        for (Path item :crawler.getRAW()) {
            System.out.println(item);
        }

        //HomeImageCollection homeImages = new HomeImageCollection(crawler);
        //homeImages.fixRawDirectoryStructure();
    }
}
