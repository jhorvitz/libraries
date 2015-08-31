
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import edu.drexel.cs.jah473.sqlite.QueryManager;
import edu.drexel.cs.jah473.sqlite.QueryResults;
import edu.drexel.cs.jah473.util.Output;

public class Fix {
    public static class LL {
        double lat;
        double lon;

        LL(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        QueryManager qm = null;
        qm = new QueryManager("maps.sqlite3");
        class Res extends QueryResults {
            String id;
            double lat;
            double lon;
        }
        class IntRes extends QueryResults {
            int id;
        }
        class Res2 extends QueryResults {
            String id;
            String name;
            String type;
            String start;
            String end;
        }
        qm.addQuery("q1", "select * from node;", Res.class);
        qm.addQuery("q2", "select * from way", Res2.class);
        Res res = qm.executeQuery("q1");
        Map<String, LL> nodes = new HashMap<>();
        while (res.nextRow()) {
            LL ll = new LL(res.lat, res.lon);
            nodes.put(res.id, ll);
        }
        PrintWriter out;
        QueryManager qm2 = new QueryManager("maps2.sqlite3");
        qm2.addQuery("qm2", "SELECT id from node where latitude=? and longitude=?;", IntRes.class);
        Res2 res2 = qm.executeQuery("q2");
        out = Output.toFile("out2.sql");
        out.write("BEGIN TRANSACTION;\n");
        Set<String> seen = new HashSet<>();
        int i = 0;
        while (res2.nextRow()) {
            String concat = res2.start + " " + res2.end;
            if (seen.contains(concat)) {
                continue;
            }
            seen.add(concat);
            LL startll = nodes.get(res2.start);
            LL endll = nodes.get(res2.end);
            IntRes from = qm2.executeQuery("qm2", startll.lat, startll.lon);
            from.nextRow();
            IntRes to = qm2.executeQuery("qm2", endll.lat, endll.lon);
            to.nextRow();
            if (res2.type.equals("")) {
                res2.name = "";
            }
            out.write("INSERT INTO way VALUES (NULL,\"" + res2.name + "\",\"" + res2.type + "\"," + from.id + "," + to.id
                    + ");\n");
           
        }
        out.write("COMMIT;");
        out.close();
        qm.close();
        qm2.close();
    }
}
