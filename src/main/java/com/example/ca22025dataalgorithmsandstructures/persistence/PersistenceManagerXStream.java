package com.example.ca22025dataalgorithmsandstructures.persistence;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import elections.model.ElectionSystemManager;

import java.io.*;

public final class PersistenceManagerXStream {

    private PersistenceManagerXStream() {}

    private static XStream createXStream() {
        XStream xs = new XStream(new StaxDriver());

        // Security: required in modern XStream (stops ForbiddenClassException from happening)
        XStream.setupDefaultSecurity(xs);
        xs.allowTypesByWildcard(new String[] {
                "elections.**",
                "com.example.ca22025dataalgorithmsandstructures.**"
        });

        // Nicer XML
        xs.autodetectAnnotations(true);

        return xs;
    }

    /**
     * Saves a snapshot of the entire system to a single XML file.
     */
    public static void save(ElectionSystemManager manager, String filename) throws IOException {
        if (manager == null) throw new IllegalArgumentException("manager cannot be null");

        XStream xs = createXStream();

        // Ensure parent dirs exist
        File file = new File(filename);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            xs.toXML(manager, out);
        }
    }

    /**
     * Loads a snapshot from XML file. If file doesn't exist, returns a fresh manager.
     */
    public static ElectionSystemManager load(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            return new ElectionSystemManager();
        }

        XStream xs = createXStream();

        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            Object obj = xs.fromXML(in);
            if (obj instanceof ElectionSystemManager mgr) {
                return mgr;
            }
            // Wrong file contents = fall back safely
            return new ElectionSystemManager();
        }
    }
}
