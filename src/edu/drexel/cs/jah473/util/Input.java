package edu.drexel.cs.jah473.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * Utilities for file input.
 * 
 * @author Justin Horvitz
 *
 */
public final class Input {

    /**
     * Returns a new {@code IterableReader} object from the given file.
     * 
     * @param file
     *            the file
     * @return an iterable reader from the file
     * @throws FileNotFoundException
     *             if the given file does not exist
     */
    public static IterableReader fromFile(File file) throws FileNotFoundException {
        return new IterableReader(new FileReader(file));
    }

    /**
     * Returns a new {@code IterableReader} object from the given file path.
     * 
     * @param path
     *            the file path
     * @return a buffered reader from the file located at the given path
     * @throws FileNotFoundException
     *             if the given path does not lead to an existing file
     */
    public static IterableReader fromFile(String path) throws FileNotFoundException {
        return new IterableReader(new FileReader(path));
    }

    /**
     * Returns a new {@code IterableReader} object that reads from stdin.
     * 
     * @return an iterable reader that reads from stdin
     */
    public static IterableReader fromStdin() {
        return new IterableReader(new InputStreamReader(System.in));
    }

    /**
     * Closes the specified object, suppressing exceptions.
     * @param in the object to close
     */
    public static void close(AutoCloseable in) {
        try {
            in.close();
        } catch (Exception e) {
        }
    }

    /* Private constructor to prevent instantiation. */
    private Input() {
    }

}
