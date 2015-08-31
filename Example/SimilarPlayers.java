import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.drexel.cs.jah473.args.Arg;
import edu.drexel.cs.jah473.args.ArgParseParams;
import edu.drexel.cs.jah473.args.Args;
import edu.drexel.cs.jah473.datastructures.KDTree;
import edu.drexel.cs.jah473.distance.DistanceFunction;
import edu.drexel.cs.jah473.distance.KDPoint;
import edu.drexel.cs.jah473.util.Input;
import edu.drexel.cs.jah473.util.Stats;
import static edu.drexel.cs.jah473.args.Type.*;

public class SimilarPlayers {
    static String name;
    static int numPlayers = 10;
    static boolean printStats = false;
    static final int DIM = 5;
    static final ArgParseParams params = new ArgParseParams() {

        @Override
        public Arg[] getRequiredArgs() {
            return new Arg[] { new Arg("name", STRING, "the player's name", "name") };
        }

        @Override
        public Arg[] getOptionalArgs() {
            Arg[] opt = new Arg[2];
            opt[0] = new Arg("numPlayers", POS_INT, "the number of similar players to find (default 10)", "-n",
                    "--number");
            opt[1] = new Arg("printStats", FLAG_ONLY, "prints player stats", "-s", "--stats");
            return opt;
        }

        @Override
        public String getUsageMessage() {
            return "similar-players \"name\" [options]\n"
                    + "Enter a major league baseball position player's name and see which other players he was most similar to!\n"
                    + "Example usage: similarPlayers \"Mike Piazza\"";
        }
    };

    public static void main(String[] args) {
        Args.parse(args, params, SimilarPlayers.class);
        List<Player> playersList = new ArrayList<Player>();
        Map<String, Player> refs = new HashMap<>();
        BufferedReader br = null;
        try {
            br = Input.fromFile(SimilarPlayers.class.getResource("mlb.csv").toString().substring(5));
            br.readLine();
            while (br.ready()) {
                String[] fields = br.readLine().split(",");
                String name = fields[0];
                double avg = Double.parseDouble(fields[1]);
                int hr = Integer.parseInt(fields[2]);
                int r = Integer.parseInt(fields[3]);
                int rbi = Integer.parseInt(fields[4]);
                int sb = Integer.parseInt(fields[5]);
                boolean hof = fields[6].equals("1") ? true : false;
                Player p = new Player(name, avg, hr, r, rbi, sb, hof);
                playersList.add(p);
                refs.put(name, p);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV");
            e.printStackTrace();
            return;
        } finally {
            Input.close(br);
        }
        double[] means = new double[DIM];
        double[] stdevs = new double[DIM];
        IntStream.range(0, DIM).forEach(i -> {
            List<Double> ithCoords = playersList.stream().map(p -> p.getCoord(i)).collect(Collectors.toList());
            means[i] = Stats.mean(ithCoords);
            stdevs[i] = Stats.stdevP(ithCoords);
        });
        DistanceFunction playerDist = new DistanceFunction() {
            @Override
            public double distanceBetween(KDPoint point1, KDPoint point2) {
                double dist = 0;
                for (int i = 0; i < DIM; i++) {
                    double p1Norm = (point1.getCoord(i) - means[i]) / stdevs[i];
                    double p2Norm = (point2.getCoord(i) - means[i]) / stdevs[i];
                    double delta = p1Norm - p2Norm;
                    dist += delta * delta;
                }
                return dist;
            }
        };
        KDTree<Player> allPlayers = new KDTree<>(playersList, DIM, playerDist);
        Player player = refs.get(name);
        if (player == null) {
            System.err.println("No player named " + name + " exists in the database");
            return;
        }
        allPlayers.remove(player);
        if (printStats) {
            System.out.println();
            printWithStats(player);
        }
        System.out.println("\nThe " + numPlayers + " most similar players to " + name + " are: ");
        int i = 0;
        for (Player p : allPlayers.kNN(player, numPlayers)) {
            System.out.format("%-4s", ++i + ". ");
            if (printStats) {
                printWithStats(p);
            } else {
                System.out.println(p + (p.isHallOfFamer() ? "*" : ""));
            }
        }
        System.out.println("\n*Hall of Famer\n");
    }

    private static void printWithStats(Player p) {
        System.out.format("%-20s", p + (p.isHallOfFamer() ? "*" : ""));
        System.out.format("%.3f", p.getCoord(0));
        System.out.println(", " + (int) p.getCoord(1) + " HR, " + (int) p.getCoord(2) + " R, " + (int) p.getCoord(3)
                + " RBI, " + (int) p.getCoord(4) + " SB");
    }
}
