package pl.edu.mimuw.kd209238.example;

import java.io.*;
import java.nio.file.Paths;

public class ReindexingMode implements Mode{

    public ReindexingMode(String[] args, Watcher watch){}

    public void run(String[] args, Watcher watcher){
        if(args.length == 1) {
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
        else {
            System.out.println("nieprawidlowa komenda");
            System.exit(1);
        }
    }
}
