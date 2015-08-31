import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.drexel.cs.jah473.args.Arg;
import edu.drexel.cs.jah473.args.ArgParseParams;
import edu.drexel.cs.jah473.args.Args;
import edu.drexel.cs.jah473.autocorrect.Autocorrect;
import edu.drexel.cs.jah473.util.Input;
import edu.drexel.cs.jah473.util.IterableReader;
import static edu.drexel.cs.jah473.args.Type.*;

public class AutocorrectExample {
    static String word;
    static boolean autocomplete = false;
    static int ledDist = 0;
    static boolean whitespace = false;
    static int n = 5;
    static ArgParseParams params = new ArgParseParams() {

        @Override
        public Arg[] getRequiredArgs() {
            Arg[] req = new Arg[1];
            req[0] = new Arg("word", STRING, "the word to correct", "word");
            return req;
        }

        @Override
        public Arg[] getOptionalArgs() {
            Arg[] opt = new Arg[4];
            opt[0] = new Arg("autocomplete", FLAG_ONLY, "turns on autocomplete", "-a", "--autocomplete");
            opt[1] = new Arg("ledDist", POS_INT, "turns on LED search with the given maximum edit distance", "-l",
                    "--led");
            opt[2] = new Arg("whitespace", FLAG_ONLY, "turns on whitespace", "-w", "--whitespace");
            opt[3] = new Arg("n", POS_INT, "the number of suggestions to display", "-n");
            return opt;
        }

        @Override
        public String getUsageMessage() {
            return "autocorrect word [options]";
        }

    };

    public static void main(String[] args) {
        if (args.length == 1) {
            word = args[0];
            ledDist = 3;
        }
        Args.parse(args, params, AutocorrectExample.class);
        IterableReader in;
        try {
            in = Input.fromFile("/usr/share/dict/words");
        } catch (FileNotFoundException e) {
            System.err.println("Error reading from dictionary");
            return;
        }
        List<String> corpus = new ArrayList<>();
        for (String word : in) {
            corpus.add(word);
        }
        Input.close(in);
        Autocorrect ac = new Autocorrect(corpus, true);
        ac.useAutocomplete = autocomplete;
        ac.useLED = ledDist > 0;
        ac.setMaxLED(ledDist);
        ac.useWhitespace = whitespace;
        ac.suggest(word).stream().limit(n).forEachOrdered(System.out::println);
    }
}
