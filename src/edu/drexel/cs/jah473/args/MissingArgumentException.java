package edu.drexel.cs.jah473.args;

/**
 * An exception for when a required command line argument is not present.
 * 
 * @author Justin Horvitz
 *
 */
public class MissingArgumentException extends IllegalArgumentException {

    private static final long serialVersionUID = 4525968230084258471L;
    private String argName;

    /**
     * Constructs a new missing argument exception.
     * 
     * @param flag
     *            The option flag
     */
    public MissingArgumentException(String flag) {
        this.argName = flag;
    }

    /**
     * Retrieves the missing argument's name.
     * 
     * @return the missing argument's name
     */
    public String getArgName() {
        return argName;
    }

    /**
     * Returns a string representation of this missing argument exception.
     */
    @Override
    public String toString() {
        return "Missing argument " + argName;
    }
}
