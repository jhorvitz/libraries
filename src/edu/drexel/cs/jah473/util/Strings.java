package edu.drexel.cs.jah473.util;

/**
 * Utilities for working with strings.
 * 
 * @author Justin Horvitz
 *
 */
public final class Strings {

    /**
     * Returns the last character of the specified string
     * 
     * @param s
     *            the string
     * @return the last character of the specified string
     */
    public static char lastChar(String s) {
        return s.charAt(s.length() - 1);
    }

    /**
     * Deletes the last character from the specified string builder.
     * 
     * @param sb
     *            the string builder
     */
    public static void pop(StringBuilder sb) {
        sb.deleteCharAt(sb.length() - 1);
    }

    /**
     * Deletes the last character from the specified string buffer.
     * 
     * @param sb
     *            the string buffer
     */
    public static void pop(StringBuffer sb) {
        sb.deleteCharAt(sb.length() - 1);
    }

    /**
     * Returns a initial caps version of the given string. The first character
     * and each character following a space will be capitalized. Every other
     * character will be in lower case.
     * 
     * @param str
     *            the given string
     * @return an initial caps version of the given string
     */
    public static String toInitCaps(String str) {
        StringBuilder sb = new StringBuilder(str.toLowerCase());
        final int len = sb.length();
        if (len > 0) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }
        for (int i = 1; i < len; i++) {
            if (sb.charAt(i - 1) == ' ') {
                sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
            }
        }
        return sb.toString();
    }

    /**
     * Returns a reversed version of the given string.
     * 
     * @param str
     *            the given string
     * @return a reversed version of the given string
     */
    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * Returns a character representation of the given string. This function
     * handles whitespace characters such as {@literal \n}.
     * 
     * @param str
     *            the given string
     * @throws ClassCastException
     *             if the given string cannot be converted to a {@code char}.
     * @return a character representation of the given string
     */
    public static char asChar(String str) {
        if (str.length() == 1) {
            return str.charAt(0);
        }
        switch (str) {
        case "\\n":
            return '\n';
        case "\\t":
            return '\t';
        case "\\b":
            return '\b';
        case "\\r":
            return '\r';
        case "\\f":
            return '\f';
        default:
            throw new ClassCastException("Cannot cast " + str + "to " + char.class.getCanonicalName());
        }
    }

    /**
     * Returns a version of the given string with all non-alphanumeric
     * characters besides spaces removed.
     * 
     * @param str
     *            the given string
     * @return a version of the given string with all non-alphanumeric
     *         characters removed
     */
    public static String removePunctuation(String str) {
        return str.replaceAll("[^A-Za-z0-9 ]", "");
    }

    /**
     * Creates a string from the given character array.
     * 
     * @param cs
     *            the character array to be converted to a string
     * @return a string built from the given character array
     */
    public static String fromCharacterArray(Character[] cs) {
        StringBuilder sb = new StringBuilder(cs.length);
        for (char c : cs) {
            sb.append(c);
        }
        return sb.toString();
    }

    /* Private constructor to prevent instantiation */
    private Strings() {
    }
}
