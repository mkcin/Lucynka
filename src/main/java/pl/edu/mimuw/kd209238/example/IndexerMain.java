package pl.edu.mimuw.kd209238.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class IndexerMain {
    private static String indexedCatalogs = Paths.get("./index/indexed.info").toAbsolutePath().normalize().toString();

    public static void main(String args[]) {
        try {
            Watcher watch = new Watcher(indexedCatalogs);
            if(args.length == 0) {
                System.out.println("listening");
                while(true) {
                    watch.handleChanges();
                }
            }
            switch (args[0]){
                case "--add":
                    if(args.length==2) {
                        add(args[1], watch);
                    }
                    else {
                        System.out.println("nieprawidlowa komenda");
                        System.exit(1);
                    }
                    break;
                case "--rm":
                    if(args.length==2) {
                        rm(args[1], watch);
                    }
                    else {
                        System.out.println("nieprawidlowa komenda");
                        System.exit(1);
                    }
                    break;
                case "--list":
                    if(args.length==1) {
                        listCanonicalPaths();
                    }
                    else {
                        System.out.println("nieprawidlowa komenda");
                        System.exit(1);
                    }
                    break;
                case "--purge":
                    if(args.length==1) {
                        try {
                            purge();
                        }
                        catch (FileNotFoundException e) {
                            System.err.println("nic do usuniecia");
                        }
                    }
                    else {
                        System.out.println("nieprawidlowa komenda");
                        System.exit(1);
                    }
                    break;
                case "--reindex":
                    if(args.length == 1) {
                        reindex(watch);
                    }
                    else {
                        System.out.println("nieprawidlowa komenda");
                        System.exit(1);
                    }
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void reindex(Watcher watcher) {
        watcher.getIndexer().reset();
        try {
            File inputFile = new File(indexedCatalogs);

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                if(currentLine.isEmpty() || currentLine.charAt(0)!='/') continue;
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                watcher.getIndexer().indexDirectory(Paths.get(trimmedLine), "ENTRY_CREATE");
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void purge() throws FileNotFoundException{
        for (File file: new File("index").listFiles()) {
            file.delete();
        }
        try {
            Files.createFile(Paths.get(indexedCatalogs));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void listCanonicalPaths() {
        try {
            File inputFile = new File(indexedCatalogs);

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                if(currentLine.isEmpty() || currentLine.charAt(0)!='/') continue;
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                File f = new File(trimmedLine);
                System.out.println(f.getCanonicalPath());
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void rm(String path, Watcher watcher) {
        try {
            path = Paths.get(path).toAbsolutePath().normalize().toString();
            watcher.removeDirectory(path);
            File inputFile = new File(indexedCatalogs);
            File tempFile = new File("TempIndexed.info");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = path;
            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if(trimmedLine.equals(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean successful = tempFile.renameTo(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void add(String path, Watcher watch) {
        try {
            path = Paths.get(path).toAbsolutePath().normalize().toString();
            System.out.println(path+"sdfasdfasdfasdfasdf");
            watch.addDirectory(path);
            File file = new File(indexedCatalogs);
            FileWriter fr = new FileWriter(file, true);
            BufferedWriter br = new BufferedWriter(fr);
            br.write("\n" + path);

            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
