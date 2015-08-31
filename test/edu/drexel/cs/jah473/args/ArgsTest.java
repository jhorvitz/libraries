package edu.drexel.cs.jah473.args;

import static edu.drexel.cs.jah473.args.Type.*;

import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ArgsTest {

    static int n;
    static String str;
    static boolean flag;
    static boolean flag2;
    static final double DEFAULT_D = 5.5;
    static double d = DEFAULT_D;
    static ArgParseParams params = new ArgParseParams() {

        @Override
        public Arg[] getRequiredArgs() {
            Arg[] req = new Arg[2];
            req[0] = new Arg("n", INT, "an integer", "n");
            req[1] = new Arg("str", STRING, "a string", "str");
            return req;
        }

        @Override
        public Arg[] getOptionalArgs() {
            Arg[] opt = new Arg[3];
            opt[0] = new Arg("flag", FLAG_ONLY, "a flag", "-f", "--flag");
            opt[1] = new Arg("d", DOUBLE, "a double value", "-d", "--double");
            opt[2] = new Arg("flag2", FLAG_ONLY, "another flag", "-x", "--xtra");
            return opt;
        }

        @Override
        public String getUsageMessage() {
            return "Improper usage";
        }

    };

    @Before
    public void before() {
        str = null;
        d = DEFAULT_D;
        flag = false;
        flag2 = false;
    }

    @Test
    public void noOptTest() {
        String[] args = new String[] { "4", "hello there" };
        Set<String> flags = Args.parse(args, params, ArgsTest.class);
        assertEquals(4, n);
        assertEquals(args[1], str);
        assertEquals(DEFAULT_D, d, 0);
        assertFalse(flag);
        assertFalse(flag2);
        assertFalse(flags.contains("-f"));
        assertFalse(flags.contains("--flag"));
        assertFalse(flags.contains("-d"));
        assertFalse(flags.contains("--double"));
        assertFalse(flags.contains("-x"));
        assertFalse(flags.contains("--xtra"));
    }

    @Test
    public void shortFlagsTest() {
        String[] args = new String[] { "-30", "ABCD", "-d", "-42.3", "-f" };
        Args.parse(args, params, ArgsTest.class);
        Set<String> flags = Args.parse(args, params, ArgsTest.class);
        assertEquals(-30, n);
        assertEquals(args[1], str);
        assertEquals(-42.3, d, 0);
        assertTrue(flag);
        assertFalse(flag2);
        assertTrue(flags.contains("-f"));
        assertTrue(flags.contains("--flag"));
        assertTrue(flags.contains("-d"));
        assertTrue(flags.contains("--double"));
        assertFalse(flags.contains("-x"));
        assertFalse(flags.contains("--xtra"));
    }

    @Test
    public void longFlagsTest() {
        String[] args = new String[] { "-30", "ABCD", "--double", "-42.3", "--flag" };
        Args.parse(args, params, ArgsTest.class);
        Set<String> flags = Args.parse(args, params, ArgsTest.class);
        assertEquals(-30, n);
        assertEquals(args[1], str);
        assertEquals(-42.3, d, 0);
        assertTrue(flag);
        assertFalse(flag2);
        assertTrue(flags.contains("-f"));
        assertTrue(flags.contains("--flag"));
        assertTrue(flags.contains("-d"));
        assertTrue(flags.contains("--double"));
        assertFalse(flags.contains("-x"));
        assertFalse(flags.contains("--xtra"));
    }

    @Test
    public void stackedShortFlagsTest() {
        String[] args = new String[] { "-d87.1", "2", "-fx", "hi" };
        Args.parse(args, params, ArgsTest.class);
        Set<String> flags = Args.parse(args, params, ArgsTest.class);
        assertEquals(2, n);
        assertEquals(args[3], str);
        assertEquals(87.1, d, 0);
        assertTrue(flag);
        assertTrue(flag2);
        assertTrue(flags.contains("-f"));
        assertTrue(flags.contains("--flag"));
        assertTrue(flags.contains("-d"));
        assertTrue(flags.contains("--double"));
        assertTrue(flags.contains("-x"));
        assertTrue(flags.contains("--xtra"));
    }

}
