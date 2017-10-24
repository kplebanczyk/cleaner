package cleaner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.text.DateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HomeImageCollection {
    private Path startDir;
    private SearchWorker crawler;
    private PrintWriter log = null;
    private Set<Path> unableToFix = new HashSet<>();
    private Set<Path> deleteLater = new HashSet<>();
    private List<Path> scanResults;
    private int skippedFiles=0;

    public HomeImageCollection(Path startDir) {
        this.crawler = crawler(startDir);
        this.startDir =startDir;
    }

    public Set<Path> fixRawDirectoryStructure() {

        startLogging();

        FutureTask<List<Path>> scanTask = new FutureTask<List<Path>>(this.crawler);
        new Thread(scanTask).start();


        try {
            scanResults = scanTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        for (Path rawFile : scanResults) {
            //log.println("Processing " + rawFile.toString());
            if (rawFile.toFile().isDirectory()) {
                log.println("Skipping, is directory: " + rawFile.toString());
                continue;
            }

            Path parentPath = rawFile.getParent();
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

        return this.unableToFix;
    }

    public void mergeJPGs(){

    }

    private void refactorRAW(Path rawFile){
        Path parentPath = rawFile.getParent();
        Matcher OldRawMatcher = Pattern.compile("ARW|DNG").matcher(parentPath);
        log.println("Refactoring " + parentPath);
        Path refactoredPath = Paths.get(OldRawMatcher.replaceAll("RAW"));
        try {
            Files.createDirectory(refactoredPath);
        } catch (java.nio.file.FileAlreadyExistsException e) {
            log.println("Unable to create, directory already exists " + refactoredPath.toString());
        } catch (IOException e) {
            //e.printStackTrace();
            unableToFix.add(rawFile);
            log.println("Unable to create directory "+refactoredPath);
            continue;
        }

        try {
            Files.move(rawFile, refactoredPath.resolve(rawFile.getName()));
            log.println("Refactoring " + rawFile.toString() + " to " + refactoredPath.resolve(rawFile.getName()).toString());
            Files.deleteIfExists(java.nio.file.FileSystems.getDefault().getPath(parentPath));
        } catch (java.nio.file.DirectoryNotEmptyException e) {
            //log.println("Directory not empty, will try again later: " + parentName);
            deleteLater.add(parentFolderPath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveRAWfile(Path rawFile){
        File subfolderNewRAW = new File(rawFile.getParent().toString(), "RAW");
        try {
            if (!subfolderNewRAW.exists()) {
                Files.createDirectory(subfolderNewRAW.toPath());
                log.println("Created directory " + subfolderNewRAW.toString());
            }
        } catch (java.nio.file.FileAlreadyExistsException e) {
            log.println("Already exists " +subfolderNewRAW.toString());
        } catch (IOException e) {
            log.println(e.getLocalizedMessage());
            unableToFix.add(rawFile);
        }

        Path sourcePath = rawFile;
        Path destinationPath = subfolderNewRAW.toPath();
        log.println("Moving file " + sourcePath.getFileName() + " to: " + destinationPath.resolve(sourcePath.getFileName()));
        try {
            Files.move(sourcePath, destinationPath.resolve(sourcePath.getFileName()));
        } catch (IOException e) {
            log.println(e.getLocalizedMessage());
            unableToFix.add(rawFile);
        }
    }

    private void deleteDirLater(){
        for (Path emptyDir : this.deleteLater) {
            try {
                Files.deleteIfExists(emptyDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.unableToFix.addAll(this.deleteLater);
        log.println("\nDone " + scanResults.size() + " RAW files, skipped "+skippedFiles);
        if (this.unableToFix.size() > 0) {
            log.println("Failed to process " + this.unableToFix.size() + " RAW files");
            for (File unfixable: this.unableToFix) {
                log.println("Failed: " + unfixable.getAbsolutePath());
            }
        }
    }

    private void startLogging(){
        String logFileName = new StringBuilder("cleaner_").append( LocalTime.now()).toString();
        //Files.exists(Paths.get(startDir.toString(),logFileName));
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



