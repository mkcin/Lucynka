package pl.edu.mimuw.kd209238.example;

import java.nio.file.Paths;

public interface Mode {
    static String indexedCatalogs = Paths.get("./index/indexed.info").toAbsolutePath().normalize().toString();
    public void run(String[] args, Watcher watch);
}
