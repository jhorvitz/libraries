package edu.drexel.cs.jah473.args;

import java.util.Arrays;

/**
 * Represents a command line argument.
 * 
 * @author Justin Horvitz
 *
 */
public class Arg {
    String[] flags;
    Type type;
    String description;
    String fieldName;

    /**
     * Constructs a new argument.
     * 
     * @param fieldName
     *            the name of the field to be set as a result of this argument,
     *            must be static
     * @param type
     *            the type of argument expected
     * @param description
     *            a description of this argument for purposes of the help
     *            message
     * @param flags
     *            the flag(s) corresponding to this argument, or some identifier
     *            if this argument is required
     */
    public Arg(String fieldName, Type type, String description, String... flags) {
        if (flags.length == 0) {
            throw new IllegalArgumentException(fieldName+ ": arg must have at least one identifier");
        }
        this.description = description;
        this.flags = flags;
        this.type = type;
        this.fieldName = fieldName;
    }

    /**
     * Returns a string representation of this argument.
     */
    @Override
    public String toString() {
        return (flags.length == 1 ? flags[0] : Arrays.toString(flags)) + (type==Type.FLAG_ONLY?"":" <" + type + ">")+": " + description;
    }
}
