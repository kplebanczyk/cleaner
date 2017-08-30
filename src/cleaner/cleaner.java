package cleaner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class cleaner {

    public static void main(String[] args) {
        FileCrawler crawler = new FileCrawler();
        for (File item :crawler.call()) {
            System.out.println(item);
        }





    }
}
