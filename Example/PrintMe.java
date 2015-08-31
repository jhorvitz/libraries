import edu.drexel.cs.jah473.args.Arg;
import edu.drexel.cs.jah473.args.ArgParseParams;
import edu.drexel.cs.jah473.args.Args;
import edu.drexel.cs.jah473.util.Strings;
import static edu.drexel.cs.jah473.args.Type.*;

public class PrintMe {

    static String str;
    static int n = 10;
    static boolean reverse = false;
    static boolean allCaps = false;
    static char delim = ' ';

    static ArgParseParams params = new ArgParseParams() {

        @Override
        public Arg[] getRequiredArgs() {
            Arg[] req = new Arg[1];
            req[0] = new Arg("str", STRING, "the string you wish to print", "stringToPrint");
            return req;
        }

        @Override
        public Arg[] getOptionalArgs() {
            Arg[] opt = new Arg[4];
            opt[0] = new Arg("n", POS_INT, "the number of times to print the string (default 10)", "-n", "--number");
            opt[1] = new Arg("reverse", FLAG_ONLY, "prints the string in reverse", "-r", "--reverse");
            opt[2] = new Arg("allCaps",FLAG_ONLY,"prints the string in ALL CAPITAL LETTERS","-c","--caps");
            opt[3] = new Arg("delim", CHAR, "the delimiter to use (default space)", "-d", "--delimiter");
            return opt;
        }

        @Override
        public String getUsageMessage() {
            return "print-me stringToPrint [options]\n" + "Print any string over and over!\n"
                    + "Example: printMe \"Hi there!\" -n5";
        }
    };

    public static void main(String[] args) {
        Args.parse(args, params, PrintMe.class);
        if (reverse) {
            str = Strings.reverse(str);
        }
        if(allCaps){
            str = str.toUpperCase();
        }
        for (int i = 0; i < n - 1; i++) {
            System.out.print(str + delim);
        }
        System.out.println(str);
    }

}
