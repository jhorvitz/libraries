import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.drexel.cs.jah473.args.Arg;
import edu.drexel.cs.jah473.args.ArgParseParams;
import edu.drexel.cs.jah473.args.Args;
import edu.drexel.cs.jah473.datastructures.TallyMap;
import edu.drexel.cs.jah473.util.Input;
import edu.drexel.cs.jah473.util.PartsOfSpeech;
import edu.drexel.cs.jah473.util.Strings;
import static edu.drexel.cs.jah473.args.Type.*;

public class WordCloud {
    static List<File> files = new LinkedList<>();
    static int n = 10;
    static ArgParseParams params = new ArgParseParams() {

        @Override
        public Arg[] getRequiredArgs() {
            Arg[] req = new Arg[1];
            req[0] = new Arg("files", MULT_EXISTING_FILE, "the file(s) upon which to perform the word frequency count",
                    "file(s)");
            return req;
        }

        @Override
        public Arg[] getOptionalArgs() {
            Arg[] opt = new Arg[1];
            opt[0] = new Arg("n", POS_INT, "the number of most frequently occuring words to display", "-n", "--number");
            return opt;
        }

        @Override
        public String getUsageMessage() {
            return "wordcloud file(s) [options]\n" + "See the most frequently used words in a file!";
        }

    };

    public static void main(String[] args) {
        Args.parse(args, params, WordCloud.class);
        TallyMap<String> tm = new TallyMap<>();
        Set<String> skipWords = new HashSet<String>();
        skipWords.addAll(Arrays.asList(new String[] { "", "i", "yes", "no", "not", "said" }));
        skipWords.addAll(PartsOfSpeech.ARTICLES);
        skipWords.addAll(PartsOfSpeech.CONJUNCTIONS);
        skipWords.addAll(PartsOfSpeech.HELPING_VERBS);
        skipWords.addAll(PartsOfSpeech.LINKING_VERBS);
        skipWords.addAll(PartsOfSpeech.PREPOSITIONS);
        skipWords.addAll(PartsOfSpeech.PRONOUNS);
        skipWords.addAll(PartsOfSpeech.QUESTION_WORDS);
        BufferedReader in;
        for (File f : files) {
            try {
                in = Input.fromFile(f);
            } catch (FileNotFoundException e) {
                continue;
            }
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        word = Strings.removePunctuation(word);
                        word = word.toLowerCase();
                        if (!skipWords.contains(word)) {
                            tm.increment(word);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading from "+f);
            }
        }
        List<String> max = tm.getMaxKeys();
        if (max.size() == 1) {
            System.out.println("\nThe most frequently used word is: " + max.get(0));
        } else {
            System.out.println("\nThe most frequently used words are: " + max);
        }
        System.out.println("\nThe top " + n + " words are:");
        int i = 0;
        for (String word : tm.getMaxKeys(n)) {
            System.out.format("%-15s", (++i + ". " + word));
            System.out.println(tm.getTally(word));
        }
        System.out.println();
    }

}
