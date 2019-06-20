package pl.edu.mimuw.kd209238.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class ListeningMode implements Mode{

    public ListeningMode(String[] args, Watcher watch){ }

    public void run(String[] args, Watcher watch){
        if(args.length==0) {
            System.out.println("listeing...");
            while(true){
                try {
                    watch.handleChanges();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
