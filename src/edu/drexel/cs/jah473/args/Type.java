package edu.drexel.cs.jah473.args;

/**
 * Possible types for command line options.
 * 
 * @author Justin Horvitz
 *
 */
public enum Type {
    /**
     * An integer.
     */
    INT,
    /**
     * A character.
     */
    CHAR,
    /**
     * A float.
     */
    FLOAT,
    /**
     * A double.
     */
    DOUBLE,
    /**
     * A long.
     */
    LONG,
    /**
     * A string.
     */
    STRING,
    /**
     * No associated value (flag only).
     */
    FLAG_ONLY,
    /**
     * Existing file(s).
     */
    MULT_EXISTING_FILE,
    /**
     * An existing file.
     */
    EXISTING_FILE,
    /**
     * A file that may or may not exist.
     */
    FILE,
    /**
     * A PDF.
     */
    PDF_FILE,
    /**
     * An integer greater than or equal to zero.
     */
    NON_NEG_INT,
    /**
     * An integer greater than zero.
     */
    POS_INT,
    /**
     * A long greater than or equal to zero.
     */
    NON_NEG_LONG,
    /**
     * A long greater than zero.
     */
    POS_LONG,
    /**
     * A float greater than or equal to zero.
     */
    NON_NEG_FLOAT,
    /**
     * A float greater than zero.
     */
    POS_FLOAT,
    /**
     * A double greater than or equal to zero.
     */
    NON_NEG_DOUBLE,
    /**
     * A double greater than zero.
     */
    POS_DOUBLE;

    /**
     * Returns a string representation of this type.
     */
    @Override
    public String toString() {
        String ret = null;
        switch (this) {
        case INT:
        case LONG:
            ret = "integer";
            break;
        case EXISTING_FILE:
            ret = "existing file";
            break;
        case NON_NEG_INT:
        case NON_NEG_LONG:
            ret = "non-negative integer";
            break;
        case POS_INT:
        case POS_LONG:
            ret = "positive integer";
            break;
        case DOUBLE:
        case FLOAT:
            ret = "numerical";
            break;
        case NON_NEG_DOUBLE:
        case NON_NEG_FLOAT:
            ret = "non-negative numerical";
            break;
        case POS_DOUBLE:
        case POS_FLOAT:
            ret = "positive numerical";
            break;
        case CHAR:
            ret = "character";
            break;
        case PDF_FILE:
            ret = "PDF";
            break;
        case FILE:
            ret = "file name";
            break;
        case FLAG_ONLY:
            ret = "<nothing>";
            break;
        case STRING:
            ret = "string";
            break;
        case MULT_EXISTING_FILE:
            ret = "existing file(s)";
        }
        return ret;
    }
}
