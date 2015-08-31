package edu.drexel.cs.jah473.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * An extension of {@code BufferedReader} that allows line by line reading via
 * <em>for each</em> syntax.
 * 
 * @author Justin Horvitz
 *
 */
public class IterableReader extends BufferedReader implements Iterable<String> {

    /**
     * Constructs a new iterable reader from the specified reader.
     * 
     * @param in
     *            the reader
     */
    public IterableReader(Reader in) {
        super(in);
    }

    /**
     * Returns an iterator for this iterable reader. The iterator reads one line
     * at a time.
     */
    @Override
    public Iterator<String> iterator() {
        try {
            return new LineByLineIterator();
        } catch (IOException e) {
            return null;
        }
    }

    class LineByLineIterator implements Iterator<String> {

        String line;

        LineByLineIterator() throws IOException {
            line = readLine();
        }

        @Override
        public boolean hasNext() {
            return line != null;
        }

        @Override
        public String next() {
            String ret = line;
            try {
                line = readLine();
            } catch (IOException e) {
                line = null;
            }
            return ret;
        }

    }

}
