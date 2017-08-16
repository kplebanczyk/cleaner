package cleaner;

import java.io.File;


public class cleaner {

    public static void main(String[] args) {
        FileCrawler crawler = new FileCrawler("D:\\Moje Obrazy\\");

        Thread t = new Thread (crawler);
        t.run();




    }
}
