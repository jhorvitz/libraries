package edu.drexel.cs.jah473.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class containing various English parts of speech. Each part of speech is
 * represented by an unmodifiable list of English words. The class cannot be
 * instantiated.
 * 
 * @author Justin Horvitz
 *
 */
public final class PartsOfSpeech {

    /**
     * "a", "an", "the"
     */
    public static final List<String> ARTICLES = Collections.unmodifiableList(Arrays.asList("a", "an", "the"));
    /**
     * "and", "because", "but", "for","however", "if", "nor", "or", "so",
     * "whenever", "whichever", "yet"
     */
    public static final List<String> CONJUNCTIONS = Collections.unmodifiableList(Arrays.asList("and", "because", "but",
            "for", "however", "if", "nor", "or", "so", "whenever", "whichever", "yet"));
    /**
     * "can", "do", "does", "did", "had", "has", "have", "will", "shall"
     */
    public static final List<String> HELPING_VERBS = Collections.unmodifiableList(Arrays.asList("can", "do", "does",
            "did", "had", "has", "have", "will", "shall"));
    /**
     * "am", "are", "as", "be", "been", "being", "could", "is", "may", "might",
     * "must", "should", "was", "were", "will", "would"
     */
    public static final List<String> LINKING_VERBS = Collections.unmodifiableList(Arrays.asList("am", "are", "as",
            "be", "been", "being", "could", "is", "may", "might", "must", "should", "was", "were", "will", "would"));
    /**
     * "aboard", "about", "above", "across", "after", "against", "along",
     * "amid", "among", "anti", "around", "as", "at", "before", "behind",
     * "below", "beneath", "beside", "besides", "between", "beyond", "but",
     * "by", "concerning", "considering", "despite", "down", "during", "except",
     * "excepting", "excluding", "following", "near", "of", "off", "on", "onto",
     * "opposite", "outside", "over", "past", "per", "plus", "regarding",
     * "round", "save", "since", "than", "there", "through", "to", "toward",
     * "towards", "under", "underneath", "unlike", "until", "up", "upon",
     * "versus", "via", "with", "within", "without"
     */
    public static final List<String> PREPOSITIONS = Collections.unmodifiableList(Arrays.asList("aboard", "about",
            "above", "across", "after", "against", "along", "amid", "among", "anti", "around", "as", "at", "before",
            "behind", "below", "beneath", "beside", "besides", "between", "beyond", "but", "by", "concerning",
            "considering", "despite", "down", "during", "except", "excepting", "excluding", "following", "for", "from",
            "in", "inside", "into", "like", "minus", "near", "of", "off", "on", "onto", "opposite", "out", "outside",
            "over", "past", "per", "plus", "regarding", "round", "save", "since", "than", "there", "through", "to",
            "toward", "towards", "under", "underneath", "unlike", "until", "up", "upon", "versus", "via", "with",
            "within", "without"));
    /**
     * "all", "another", "any", "anybody", "anyone", "anything", "both", "each",
     * "either", "everybody", "everyone", "everything", "few", "he", "her",
     * "hers", "herself", "him", "himself", "his", "I", "it", "its", "itself",
     * "many", "me", "mine", "more", "most", "much", "my", "myself", "neither",
     * "nobody", "none", "nothing", "one", "other", "others", "our", "ours",
     * "ourselves", "several", "she", "some", "somebody", "someone",
     * "something", "that", "their", "theirs", "them", "themselves", "these",
     * "they", "this", "those", "us", "we", "what", "whatever", "which",
     * "whichever", "who", "whoever", "whom", "whomever", "whose", "you",
     * "your", "yours", "yourself", "yourselves"
     */
    public static final List<String> PRONOUNS = Collections.unmodifiableList(Arrays.asList("all", "another", "any",
            "anybody", "anyone", "anything", "both", "each", "either", "everybody", "everyone", "everything", "few",
            "he", "her", "hers", "herself", "him", "himself", "his", "I", "it", "its", "itself", "many", "me", "mine",
            "more", "most", "much", "my", "myself", "neither", "nobody", "none", "nothing", "one", "other", "others",
            "our", "ours", "ourselves", "several", "she", "some", "somebody", "someone", "something", "that", "their",
            "theirs", "them", "themselves", "these", "they", "this", "those", "us", "we", "what", "whatever", "which",
            "whichever", "who", "whoever", "whom", "whomever", "whose", "you", "your", "yours", "yourself",
            "yourselves"));
    /**
     * "how", "what", "when", "where", "which", "who", "why"
     */
    public static final List<String> QUESTION_WORDS = Collections.unmodifiableList(Arrays.asList("how", "what", "when",
            "where", "which", "who", "why"));

    /* Private constructor to prevent instantiation */
    private PartsOfSpeech() {

    }
}
