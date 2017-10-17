/*
package cleaner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HomeImageCollection {

    private FileCrawler crawler;
    private PrintWriter log = null;
    private Set<File> unfixableDirs = new HashSet<>();
    private Set<File> deleteLater = new HashSet<>();
    private LinkedBlockingQueue<Path> rawFiles;
    private String logFileName = this.crawler.getStartDir()+"changes.log";
    private int skippedFiles=0;
    private Path parentFolderPath ;
    private String parentPath ;

    public HomeImageCollection(FileCrawler fileCrawler) {
        this.crawler = fileCrawler;
    }

    public Collection<File> fixRawDirectoryStructure() {

        startLogging(logFileName);

        rawFiles = crawler.getRAW();
        for (Path rawFile : rawFiles) {
            //log.println("Processing " + rawFile.toString());
            if (rawFile.toFile().isDirectory()) {
                log.println("Skipping, is directory: " + rawFile.toString());
                continue;
            }

            parentPath = rawFile.getParent();
            Boolean parentHasNewRAW = Pattern.compile("RAW").matcher(parentPath.toString()).find();
            Matcher OldRawMatcher = Pattern.compile("ARW|DNG").matcher(parentPath.toString());
            Boolean parentHasOldRAW = OldRawMatcher.find();


            if (!parentHasNewRAW && !parentHasOldRAW) {
                moveRAWfile(rawFile);
            } else if (parentHasNewRAW) {
                skippedFiles++;
                continue;
            } else if (parentHasOldRAW) {
                refactorRAW(rawFile);
            }
        }

        deleteDirLater();

        log.flush();
        log.close();

        return this.unfixableDirs;
    }
    public void mergeJPGs(){

    }


    private void refactorRAW(File rawF){
        Matcher OldRawMatcher = Pattern.compile("ARW|DNG").matcher(this.parentPath);
        log.println("Refactoring " + parentPath);
        Path refactoredPath = Paths.get(OldRawMatcher.replaceAll("RAW"));
        try {
            Files.createDirectory(refactoredPath);
        } catch (java.nio.file.FileAlreadyExistsException e) {
            log.println("Unable to create, directory already exists " + refactoredPath.toString());
        } catch (IOException e) {
            //e.printStackTrace();
            unfixableDirs.add(rawFile);
            log.println("Unable to create directory "+refactoredPath);
            continue;
        }

        try {
            Files.move(rawFile.toPath(), refactoredPath.resolve(rawFile.getName()));
            log.println("Refactoring " + rawFile.toPath().toString() + " to " + refactoredPath.resolve(rawFile.getName()).toString());
            Files.deleteIfExists(java.nio.file.FileSystems.getDefault().getPath(parentPath));
        } catch (java.nio.file.DirectoryNotEmptyException e) {
            //log.println("Directory not empty, will try again later: " + parentName);
            deleteLater.add(parentFolderPath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveRAWfile(File rawFile){
        File subfolderNewRAW = new File(parentPath, "RAW");
        try {
            if (!subfolderNewRAW.exists()) {
                Files.createDirectory(subfolderNewRAW.toPath());
                log.println("Created directory " + subfolderNewRAW.toString());
            }
        } catch (java.nio.file.FileAlreadyExistsException e) {
            log.println("Already exists " +subfolderNewRAW.toString());
        } catch (IOException e) {
            log.println(e.getLocalizedMessage());
            unfixableDirs.add(rawFile);
        }

        Path sourcePath = rawFile.toPath();
        Path destinationPath = subfolderNewRAW.toPath();
        log.println("Moving file " + sourcePath.getFileName() + " to: " + destinationPath.resolve(sourcePath.getFileName()));
        try {
            Files.move(sourcePath, destinationPath.resolve(sourcePath.getFileName()));
        } catch (IOException e) {
            log.println(e.getLocalizedMessage());
            unfixableDirs.add(rawFile);
        }
    }

    private void deleteDirLater(){
        for (File emptyDir : this.deleteLater) {
            try {
                Files.deleteIfExists(emptyDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.unfixableDirs.addAll(this.deleteLater);
        log.println("\nDone " + rawFiles.size() + " RAW files, skipped "+skippedFiles);
        if (this.unfixableDirs.size() > 0) {
            log.println("Failed to process " + this.unfixableDirs.size() + " RAW files");
            for (File unfixable: this.unfixableDirs) {
                log.println("Failed: " + unfixable.getAbsolutePath());
            }
        }
    }
    private void startLogging(String logFileName){
        try {
            this.log = new PrintWriter(logFileName);
            this.log.println("Started at " + DateFormat.getDateTimeInstance().format(new Date()) +"\n");
            Files.createFile(Paths.get(logFileName));
            this.log = new PrintWriter(logFileName);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (java.nio.file.FileAlreadyExistsException e){
            //log.println("");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}



*/
