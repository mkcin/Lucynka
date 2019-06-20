package pl.edu.mimuw.kd209238.example;

import java.io.*;
import java.nio.file.Paths;

public class RemovalMode implements Mode{

    public RemovalMode(String[] args, Watcher watch){

    }

    public void run(String[] args, Watcher watcher){
        if(args.length==2) {
            String path = args[1];
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
        else {
            System.out.println("nieprawidlowa komenda");
            System.exit(1);
        }
    }
}
