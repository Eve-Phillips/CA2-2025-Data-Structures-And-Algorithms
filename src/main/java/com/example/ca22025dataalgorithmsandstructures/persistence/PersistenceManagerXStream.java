package com.example.ca22025dataalgorithmsandstructures.persistence;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import elections.model.ElectionSystemManager;

import java.io.*;

/**
 * PersistenceManagerXStream is responsible for saving/loading the application's state to/from XML.
 */
public final class PersistenceManagerXStream {

    /**
     * Utility class pattern:
     * - final class + private constructor prevents instantiation/subclassing.
     * - All behaviour is exposed via static methods.
     */
    private PersistenceManagerXStream() {}

    /**
     * Builds and configures an XStream instance consistently for this project.
     */
    private static XStream createXStream() {
        // StAX driver uses a streaming XML API; good for performance and lower memory usage than DOM-style parsing.
        XStream xs = new XStream(new StaxDriver());

        // --- SECURITY NOTE (important) ---
        // Modern XStream versions require explicit type permissions to reduce deserialization attacks.
        // Without this, XStream will throw ForbiddenClassException when reading, or worse, be unsafe if misconfigured.
        //
        // Here we enable default security then whitelist ONLY our application packages.
        XStream.setupDefaultSecurity(xs);
        xs.allowTypesByWildcard(new String[] {
                "elections.**",
                "com.example.ca22025dataalgorithmsandstructures.**"
        });

        // Enables @XStreamAlias and similar annotations (if used in the model) so XML is cleaner and stable.
        xs.autodetectAnnotations(true);

        return xs;
    }

    /**
     * Saves a snapshot of the entire system to a single XML file.
     *
     * @param manager  the root object representing the system state (must not be null)
     * @param filename output path for the XML file
     * @throws IOException if the file can't be written
     */
    public static void save(ElectionSystemManager manager, String filename) throws IOException {
        // Defensive programming: fail fast with a clear message rather than NPEs later.
        if (manager == null) throw new IllegalArgumentException("manager cannot be null");

        // Create a fresh XStream instance so configuration is always correct and thread-safe.
        // (XStream can be used as a singleton, but per-call avoids shared mutable state problems.)
        XStream xs = createXStream();

        // Ensure parent directories exist before attempting to write.
        File file = new File(filename);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        // Try-with-resources guarantees the stream is closed even if serialization throws.
        // BufferedOutputStream reduces IO overhead by batching writes.
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            // Serializes the object graph rooted at manager to XML.
            xs.toXML(manager, out);
        }
    }

    /**
     * Loads a snapshot from an XML file.
     *
     * Behaviour:
     * - If the file doesn't exist, returns a brand new manager (fresh system).
     * - If the XML doesn't contain the expected type, returns a fresh manager (safe fallback).
     *
     * @param filename path to the XML file
     * @return an ElectionSystemManager loaded from disk, or a new one if load is not possible
     * @throws IOException if the file exists but can't be read
     */
    public static ElectionSystemManager load(String filename) throws IOException {
        File file = new File(filename);

        // Missing save file is not considered an error in this application:
        // we treat it as "first run" / "no data yet".
        if (!file.exists()) {
            return new ElectionSystemManager();
        }

        // Use the same XStream config as save() so types + annotations match exactly.
        XStream xs = createXStream();

        // BufferedInputStream reduces the number of disk reads.
        // Try-with-resources ensures the file handle is always closed.
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            // Deserialization: reads XML and reconstructs the object graph.
            Object obj = xs.fromXML(in);

            // Pattern matching keeps this concise and type-safe.
            if (obj instanceof ElectionSystemManager mgr) {
                return mgr;
            }

            // Wrong file contents (or unexpected root object) -> fall back safely.
            return new ElectionSystemManager();
        }
    }
}
