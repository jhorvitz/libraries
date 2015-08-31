package edu.drexel.cs.jah473.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utilities for file output.
 * 
 * @author Justin Horvitz
 *
 */
public final class Output {
    /**
     * Returns a new {@code PrintWriter} object that writes to the given file.
     * 
     * @param file
     *            the file
     * @return a print writer object that writes to the given file
     * @throws IOException
     *             if there is a problem writing to the given file
     */
    public static PrintWriter toFile(File file) throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(file)));
    }

    /**
     * Returns a new {@code PrintWriter} object that writes to the given file
     * path.
     * 
     * @param path
     *            the file path
     * @return a print writer object that writes to the given file path
     * @throws IOException
     *             if there is a problem writing to the given file path
     */
    public static PrintWriter toFile(String path) throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(path)));
    }

    /* Private constructor to prevent instantiation. */
    private Output() {
    }
}
