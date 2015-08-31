package edu.drexel.cs.jah473.args;

import static edu.drexel.cs.jah473.args.Type.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.drexel.cs.jah473.util.Strings;

/**
 * A class that parses command line arguments and uses reflection to set desired
 * fields based on the arguments that were passed. Rather than throwing
 * exceptions for invalid arguments, the argument parser will print a help
 * message detailing valid usage and terminate the program.
 * 
 * @author Justin Horvitz
 *
 */
public final class Args {

    /* Private constructor to prevent instantiation */
    private Args() {
    }

    /* Built-in help flags */
    private static boolean isHelpFlag(String flag) {
        return flag.equals("-h") || flag.equals("--help");
    }

    /**
     * Parses the given arguments and sets the desired fields in the given class
     * using reflection. Fields are set according to the specifications in the
     * given {@code ArgParseParams} object. The return value is a set of all
     * option flags that were seen while parsing. If multiple flags correspond
     * to the same option, then all of those flags will be present in the
     * returned set even if only one was passed from the command line. If at any
     * point one of the built in help flags ({@code -h} or {@code --help}) are
     * seen, a help message will be printed and the program will terminate.
     * 
     * @param args
     *            the command line arguments
     * @param params
     *            the specifications for the program's command line arguments
     * @param c
     *            the class whose static fields will be set, typically the class
     *            containing the main function
     * @return a set of option flags that were utilized
     */

    @SuppressWarnings("unchecked")
    public static Set<String> parse(String[] args, ArgParseParams params, Class<?> c) {
        Set<String> flagsSeen = new HashSet<>();
        Map<String, Arg> options = new HashMap<>();
        List<Character> extraFlags = new ArrayList<>();
        for (Arg a : params.getOptionalArgs()) {
            for (String flag : a.flags) {
                options.put(flag, a);
            }
        }
        Arg[] req = params.getRequiredArgs();
        int r = 0;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (isHelpFlag(arg)) {
                printHelp(params);
                System.exit(0);
            }
            Arg opt = options.get(arg);
            String val = null;
            String flag = arg;
            if (opt == null) {
                if (arg.startsWith("-") && arg.length() > 2) {
                    flag = arg.substring(0, 2);
                    if (isHelpFlag(flag)) {
                        printHelp(params);
                        System.exit(0);
                    }
                    opt = options.get(flag);
                    if (opt != null) {
                        if(opt.type!=FLAG_ONLY){
                            val = arg.substring(2);
                        }
                        else{
                            String rest=arg.substring(2);
                            for(int j=0;j<rest.length();j++){
                                extraFlags.add(rest.charAt(j));
                            }
                        }
                    }
                }
            }
            if (opt == null) {
                if (r >= req.length ) {
                    System.err.println(new UnrecognizedOptionException(arg));
                    if (params.printHelpMessageOnError()) {
                        printHelp(params);
                    }
                    System.exit(1);
                }
                val = arg;
                opt = req[r];
                if (opt.type != MULT_EXISTING_FILE) {
                    r++;
                } else if (r < req.length - 1) {
                    throw new IllegalArgumentException("MULT_EXISTING_FILE must be last required arg");
                }
            }
            if (opt.type == FLAG_ONLY) {
                for (String f : opt.flags) {
                    flagsSeen.add(f);
                }
                if (opt.fieldName != null) {
                    try {
                        Field f = c.getDeclaredField(opt.fieldName);
                        f.setAccessible(true);
                        f.set(null, true);
                    } catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                continue;
            }
            if (val == null) {
                i++;
                if (i >= args.length) {
                    System.err.println(new WrongArgumentTypeException(flag, opt.type));
                    if (params.printHelpMessageOnError()) {
                        printHelp(params);
                    }
                    System.exit(1);
                }
                val = args[i];
            }
            Object result = null;
            try {
                result = parseSingle(opt.type, val, opt.flags[0]);
            } catch (WrongArgumentTypeException e) {
                System.err.println(e);
                if (params.printHelpMessageOnError()) {
                    printHelp(params);
                }
                System.exit(1);
            }
            for (String f : opt.flags) {
                flagsSeen.add(f);
            }
            try {
                Field f = c.getDeclaredField(opt.fieldName);
                f.setAccessible(true);
                if (opt.type == MULT_EXISTING_FILE) {
                    List<File> files = (List<File>) f.get(null);
                    files.add((File) result);
                    continue;
                }
                f.set(null, result);
            } catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
                System.exit(1);
            }

        }
        if (r < req.length) {
            if (r + 1 == req.length && req[r].type == MULT_EXISTING_FILE) {
                List<File> files = null;
                try {
                    Field f = c.getDeclaredField(req[r].fieldName);
                    f.setAccessible(true);
                    files = (List<File>) f.get(null);
                } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                if (!files.isEmpty()) {
                    return flagsSeen;
                }
            }
            System.err.println(new MissingArgumentException(req[r].flags[0]));
            if (params.printHelpMessageOnError()) {
                printHelp(params);
            }
            System.exit(1);
        }
        int k=0;
        for(char f:extraFlags){
            String flag = "-"+f;
            Arg opt = options.get(flag);
            if(opt==null){
                System.err.println(new UnrecognizedOptionException(flag));
                if(params.printHelpMessageOnError()){
                    printHelp(params);
                }
                System.exit(1);
            }
            if(opt.type!=FLAG_ONLY){
                String val=Strings.fromCharacterArray(extraFlags.subList(k+1, extraFlags.size()).toArray(new Character[0]));
                Object result = null;
                try {
                    result = parseSingle(opt.type, val, flag);
                } catch (WrongArgumentTypeException e) {
                    System.err.println(e);
                    if (params.printHelpMessageOnError()) {
                        printHelp(params);
                    }
                    System.exit(1);
                }
                try {
                    Field fi = c.getDeclaredField(opt.fieldName);
                    fi.setAccessible(true);
                    if (opt.type == MULT_EXISTING_FILE) {
                        List<File> files = (List<File>) fi.get(null);
                        files.add((File) result);
                        break;
                    }
                    fi.set(null, result);
                    break;
                } catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                System.err.println(new WrongArgumentTypeException(flag,opt.type));
                if(params.printHelpMessageOnError()){
                    printHelp(params);
                }
                System.exit(1);
            }
            for (String fl : opt.flags) {
                flagsSeen.add(fl);
            }
            if (opt.fieldName != null) {
                try {
                    Field fi = c.getDeclaredField(opt.fieldName);
                    fi.setAccessible(true);
                    fi.set(null, true);
                } catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            k++;
        }
        return flagsSeen;
    }

    /* Parses a single argument */
    private static Object parseSingle(Type expected, String val, String opt) {
        Object toAdd = null;
        try {
            String[] nameArr = expected.name().split("_");
            String primType = nameArr[nameArr.length - 1];
            switch (primType) {
            case "INT":
                toAdd = Integer.parseInt(val);
                break;
            case "FILE":
                toAdd = new File(val);
                break;
            case "STRING":
                toAdd = val;
                break;
            case "DOUBLE":
                toAdd = Double.parseDouble(val);
                break;
            case "LONG":
                toAdd = Long.parseLong(val);
                break;
            case "FLOAT":
                toAdd = Float.parseFloat(val);
                break;
            case "CHAR":
                toAdd = Strings.asChar(val);
                break;
            }
            if (expected.name().contains("NON_NEG") && Double.parseDouble(val) < 0) {
                Integer.parseInt("causeError");
            } else if (expected.name().contains("POS") && !(Double.parseDouble(val) > 0)) {
                Integer.parseInt("causeError");
            } else if (expected.name().contains("EXISTING") && (!((File) toAdd).exists() || ((File) toAdd).isDirectory())) {
                Integer.parseInt("causeError");
            } else if (expected.name().contains("PDF") && !val.endsWith(".pdf")) {
                Integer.parseInt("causeError");
            }
        } catch (NumberFormatException | ClassCastException e) {
            throw new WrongArgumentTypeException(opt, expected, val);
        }
        return toAdd;
    }

    /**
     * Prints a custom help message detailing usage for the program based on the
     * given {@code ArgParseParams} object.
     * 
     * @param params
     *            the specifications for the program's command line arguments
     */
    public static void printHelp(ArgParseParams params) {
        System.out.println();
        String usage = params.getUsageMessage();
        Arg[] required = params.getRequiredArgs();
        Arg[] optional = params.getOptionalArgs();
        if (usage != null) {
            System.out.println("Usage:\n" + usage + '\n');
        }
        if (required.length > 0) {
            System.out.println("Required arguments:");
            for (int i = 0; i < required.length; i++) {
                System.out.println(required[i]);
            }
        }
        if (optional.length > 0) {
            System.out.println("\nOptions:");
            for (int i = 0; i < optional.length; i++) {
                System.out.println(optional[i]);
            }
        }
        System.out.println();
        System.exit(0);
    }
}