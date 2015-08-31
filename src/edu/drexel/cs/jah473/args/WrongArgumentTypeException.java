package edu.drexel.cs.jah473.args;

/**
 * An exception thrown when a command line argument does not match its expected
 * value.
 * 
 * @author Justin Horvitz
 *
 */
public class WrongArgumentTypeException extends IllegalArgumentException {

    private static final long serialVersionUID = -894168544350173697L;
    private String flag;
    private Type expected;
    private String actual;
    private static final String NOTHING = "<nothing>";

    /**
     * Constructs a new wrong option type exception for when an argument is
     * missing.
     * 
     * @param flag
     *            The option flag
     * @param expected
     *            The expected type
     */
    public WrongArgumentTypeException(String flag, Type expected) {
        this.flag = flag;
        this.expected = expected;
        actual = NOTHING;
    }

    /**
     * Constructs a new wrong option type exception for when an argument is
     * provided but is of the wrong type.
     * 
     * @param flag
     *            The option flag
     * @param expected
     *            The expected type
     * @param actual
     *            The invalid argument passed
     */
    public WrongArgumentTypeException(String flag, Type expected, String actual) {
        this(flag, expected);
        this.actual = '"' + actual + '"';
    }

    /**
     * Retrieves the option flag.
     * 
     * @return The option flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * Retrieves the expected type.
     * 
     * @return The expected type
     */
    public Type getExpected() {
        return expected;
    }

    /**
     * Retrieves the invalid argument passed.
     * 
     * @return The invalid argument passed
     */
    public String getActual() {
        return actual;
    }

    /**
     * Returns a string representation of this wrong option type exception. The
     * representation includes the expected and actual types of the argument as
     * well as the option flag.
     */
    @Override
    public String toString() {
        if (expected.name().contains("EXISTING") && actual != NOTHING) {
            return "File " + actual + " does not exist";
        }
        return "Wrong argument type for " + flag + " (expected " + expected + ", got " + actual + ")";
    }
}
