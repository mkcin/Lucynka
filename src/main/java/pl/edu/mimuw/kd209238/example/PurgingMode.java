package pl.edu.mimuw.kd209238.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PurgingMode implements Mode{

    public PurgingMode(String[] args, Watcher watch){}

    public void run(String[] args, Watcher watch) {
        if(args.length==1) {
            for (File file: new File("index").listFiles()) {
                file.delete();
            }
            try {
                Files.createFile(Paths.get(indexedCatalogs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("nieprawidlowa komenda");
            System.exit(1);
        }
    }
}
