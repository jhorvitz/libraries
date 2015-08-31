package edu.drexel.cs.jah473.args;

/**
 * Contract for specifying parameters for argument parsing.
 * 
 * @author Justin Horvitz
 *
 */
public interface ArgParseParams {
    /**
     * Returns an array of the required command line arguments in the order they
     * are to be given.
     * 
     * @return the required command line arguments
     */
    public Arg[] getRequiredArgs();

    /**
     * Returns an array of the optional command line arguments.
     * 
     * @return the optional command line arguments
     */
    public Arg[] getOptionalArgs();

    /**
     * Returns the usage message to be printed for invalid usage or when help
     * flag is present.
     * 
     * @return the usage message
     */
    public String getUsageMessage();

    /**
     * Defines whether or not to print a help message if invalid arguments are
     * passed. The default implementation returns {@code true}.
     * 
     * @return whether to print a help message if invalid arguments are passed
     */
    public default boolean printHelpMessageOnError() {
        return true;
    }
}
