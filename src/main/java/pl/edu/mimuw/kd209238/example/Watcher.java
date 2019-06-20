package pl.edu.mimuw.kd209238.example;

import com.google.common.io.Files;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.List;

public class Watcher {

    private WatchDir watch;
    private Indexer indexer;

    public Watcher(String indexedCatalogs) throws IOException {
        this.watch = new WatchDir();
        this.indexer = new Indexer("index");
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(indexedCatalogs));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.isEmpty() || line.charAt(0)!='/') continue;
                watch.registerAll(Paths.get(line), false);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleChanges() throws IOException {
        Pair<Path, List<WatchEvent<?>>> events = this.watch.processEvents();
        Path dir = events.getKey();
        List<WatchEvent<?>> eventList = events.getValue();
        for(WatchEvent<?> event: eventList) {
            WatchEvent<Path> ev = WatchDir.cast(event);
            Path name = ev.context();
            Path child = dir.resolve(name);
            System.out.println("++++" + event.kind().name() + " " + child);
            if(handledExtention(child)) {
                this.indexer.indexDirectory(child, event.kind().name());
            }
        }
    }

    public void addDirectory(String path) throws IOException {
        System.out.println("add to watch");
        watch.addToWatch(Paths.get(path));
        System.out.println("add to index");
        indexer.indexDirectory(Paths.get(path), "ENTRY_CREATE");
        System.out.println("done");
    }

    public void removeDirectory(String path) {
        watch.removeFromWatched(Paths.get(path));
        indexer.indexDirectory(Paths.get(path), "ENTRY_DELETE");
    }

    public static boolean handledExtention(Path path) {
        String ext = Files.getFileExtension(path.toString());
        return "".equals(ext) || "txt".equals(ext) || "pdf".equals(ext);
    }

    public Indexer getIndexer() {
        return this.indexer;
    }
}
