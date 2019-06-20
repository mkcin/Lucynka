package pl.edu.mimuw.kd209238.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class AddiotionMode implements Mode{

    public AddiotionMode(String[] args, Watcher watch){ }

    public void run(String[] args, Watcher watch){
        if(args.length==2) {
            String path = args[1];
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
        else {
            System.out.println("nieprawidlowa komenda");
            System.exit(1);
        }
    }
}
