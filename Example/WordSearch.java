import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.drexel.cs.jah473.args.Arg;
import edu.drexel.cs.jah473.args.ArgParseParams;
import edu.drexel.cs.jah473.args.Args;
import edu.drexel.cs.jah473.datastructures.Trie;
import edu.drexel.cs.jah473.util.Input;
import static edu.drexel.cs.jah473.args.Type.*;

public class WordSearch {

    static File wordBankFile;
    static File puzzleFile;
    static ArgParseParams params = new ArgParseParams() {
        @Override
        public Arg[] getRequiredArgs() {
            Arg[] req = new Arg[2];
            req[0] = new Arg("wordBankFile", EXISTING_FILE, "the file containing the word bank, one word per line",
                    "wordBank");
            req[1] = new Arg("puzzleFile", EXISTING_FILE, "the file containing the word search puzzle", "puzzle");
            return req;
        }

        @Override
        public Arg[] getOptionalArgs() {
            return new Arg[0];
        }

        @Override
        public String getUsageMessage() {
            return "wordsearch wordBank puzzle\n" + "Solve any word search!";
        }

    };

    public static void main(String[] args) {
        Args.parse(args, params, WordSearch.class);
        Trie wordBank = new Trie();
        BufferedReader in = null;
        try {
            in = Input.fromFile(wordBankFile);
            String word;
            while ((word = in.readLine()) != null) {
                wordBank.add(word);
            }
        } catch (IOException e) {
            System.err.println("Error reading from word bank file " + wordBankFile);
            Input.close(in);
            return;
        }
        Input.close(in);
        List<String> rows = new ArrayList<>();
        try {
            in = Input.fromFile(puzzleFile);
            String row;
            while ((row = in.readLine()) != null) {
                row = row.replace(" ", "");
                rows.add(row);
            }
        } catch (IOException e) {
            System.err.println("Error reading from puzzle file " + puzzleFile);
            Input.close(in);
            return;
        }
        Input.close(in);
        final int r = rows.size();
        if (r == 0) {
            System.out.println("Puzzle file " + puzzleFile + " is empty");
            return;
        }
        char[][] puzzle = new char[r][];
        final int c = rows.get(0).length();
        if (c == 0) {
            System.out.println("Puzzle file " + puzzleFile + " contains an empty line");
            return;
        }
        for (int i = 0; i < r; i++) {
            String row = rows.get(i);
            if (row.length() != c) {
                System.out.println("Puzzle invalid, row length varies");
                return;
            }
            puzzle[i] = row.toCharArray();
        }
        System.out.println();
        System.out.print("   ");
        for (int i = 0; i < c; i++) {
            System.out.format("%-3s", " " + (i + 1));
        }
        System.out.println();
        System.out.print("  ");
        for (int i = 0; i < c; i++) {
            System.out.print("---");
        }
        System.out.println("-");
        for (int i = 0; i < r; i++) {
            System.out.format("%-2s", i + 1);
            System.out.print("| ");
            for (char ch : puzzle[i]) {
                System.out.format("%-3s", ch + " ");
            }
            System.out.println();
        }
        System.out.format("\n%-15s%-12s%s\n","word","(row,col)","direction");
        System.out.println("-----------------------------------------");
        Direction[] directions = { new Direction("down", p -> p.row++), 
                                   new Direction("up", p -> p.row--),
                                   new Direction("right", p -> p.col++),
                                   new Direction("left", p -> p.col--),
                                   new Direction("down right", p -> {p.row++; p.col++;}),
                                   new Direction("down left", p -> {p.row++; p.col--;}),
                                   new Direction("up right", p -> { p.row--; p.col++;}),
                                   new Direction("up left", p -> { p.row--; p.col--;}) 
                                 };
        solver:
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                for (Direction dir : directions) {
                    Position at = new Position(i, j);
                    String word = "";
                    while (wordBank.isPrefix(word) && isInBounds(at, r, c)) {
                        word += puzzle[at.row][at.col];
                        if (wordBank.contains(word)) {
                            System.out.format("%-15s", word.toLowerCase());
                            System.out.format("%-12s", ("(" + (i + 1) + "," + (j + 1) + ")"));
                            System.out.println(dir);
                            wordBank.remove(word);
                            if (wordBank.isEmpty()) {
                                break solver;
                            }
                        }
                        dir.move(at);
                    }
                }
            }
        }
        if (wordBank.isEmpty()) {
            System.out.println("\nPuzzle solved!");
        } else {
            System.out.println("\nWords not found:");
            for (String word : wordBank) {
                System.out.println(word);
            }
        }
        System.out.println();
    }

    static boolean isInBounds(Position pos, int r, int c) {
        return pos.row >= 0 && pos.row < r && pos.col >= 0 && pos.col < c;
    }
}
