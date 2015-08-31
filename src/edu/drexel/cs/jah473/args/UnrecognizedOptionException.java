package edu.drexel.cs.jah473.args;

/**
 * An exception for when a command line option is not recognized.
 * 
 * @author Justin Horvitz
 *
 */
public class UnrecognizedOptionException extends IllegalArgumentException {

    private static final long serialVersionUID = 9009364910510722633L;
    private String option;

    /**
     * Constructs a new unrecognized option exception.
     * 
     * @param option
     *            The option that was not recognized
     */
    public UnrecognizedOptionException(String option) {
        this.option = option;
    }

    /**
     * Retrieves the option that was not recognized.
     * 
     * @return The option that was not recognized
     */
    public String getOption() {
        return option;
    }

    /**
     * Returns a string representation of this unsupported option exception.
     */
    @Override
    public String toString() {
        return "Unrecognized option \"" + option + "\"";
    }
}
