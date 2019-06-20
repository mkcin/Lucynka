package pl.edu.mimuw.kd209238.example;

import org.apache.poi.util.Removal;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class IndexerMain {
    private static String indexedCatalogs = Paths.get("./index/indexed.info").toAbsolutePath().normalize().toString();
    private static Mode mode;
    public static void main(String args[]) {
        try {
            Watcher watch = new Watcher(indexedCatalogs);
            mode = setMode(args, watch);
            mode.run(args, watch);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static Mode setMode(String[] args, Watcher watch){
        if(args.length == 0) return new ListeningMode(args, watch);
        if(args[0].equals("--add")) return new AddiotionMode(args, watch);
        if(args[0].equals("--rm")) return new RemovalMode(args, watch);
        if(args[0].equals("--list")) return new ListingMode(args, watch);
        if(args[0].equals("--index")) return new ReindexingMode(args, watch);
        if(args[0].equals("--purge")) return new PurgingMode(args, watch);
        return null;
    }

}
