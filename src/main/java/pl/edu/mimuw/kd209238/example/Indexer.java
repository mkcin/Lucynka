package pl.edu.mimuw.kd209238.example;

import com.mchange.v2.io.IndentedWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRefBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class Indexer {

    private String indexPath;
    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        this.indexPath = indexDir;
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        iwc.setRAMBufferSizeMB(256.0);

        this.writer = new IndexWriter(dir, iwc);
    }

    public void reset() {
        try {
            writer.close();
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter deleter = new IndexWriter(dir, iwc);
            deleter.close();
//            iwc = new IndexWriterConfig(analyzer);
//            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
//            this.writer = new IndexWriter(dir, iwc);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeWriter() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void indexDirectory(Path path, String type) {
        System.out.println("type - " + type + ", path - " + path.toString());

//        if (!Files.isReadable(path)) {
//            System.out.println("Document directory '" +path.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
//            System.exit(1);
//        }
        Date start = new Date();

        try {
            System.out.println("Indexing to directory '" + indexPath + "'...");

            indexDocs(writer, path, type);

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");
            writer.maybeMerge();
        } catch (
                IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }
    }

    private void indexDocs(final IndexWriter writer, Path path, String type) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        if("ENTRY_DELETE".equals(type))
                            removeDoc(writer, file);
                        else
                            indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    } catch (IOException ignore) {
                        // don't index files that can't be read.
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException io)
                {
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        } else {
            if("ENTRY_DELETE".equals(type)) {
                removeDoc(writer, path);
            }
            else {
                indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
            }
        }
    }

    private void removeDoc(IndexWriter writer, Path path) {
        if(Watcher.handledExtention(path)) {
            try {
                System.out.println("usuwam " + path);
                writer.deleteDocuments(new Term("path", path.toString()));
                writer.commit();
                writer.flush();
            }
            catch (Exception e) {
                System.out.println("nie udalo sie usunac");
                System.out.println(e);
            }
        }
    }

    /** Indexes a single document */
    private void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        if(!Watcher.handledExtention(file)) return;

        try (InputStream stream = Files.newInputStream(file)) {
            Document doc = new Document();

            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);

            doc.add(new LongPoint("modified", lastModified));

            doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

            if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                System.out.println("adding " + file);
                writer.addDocument(doc);
            } else {
                System.out.println("updating " + file);
                writer.updateDocument(new Term("path", file.toString()), doc);
                writer.commit();
                writer.flush();
            }
        }
    }

}
