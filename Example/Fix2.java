import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import edu.drexel.cs.jah473.sqlite.QueryManager;
import edu.drexel.cs.jah473.sqlite.QueryResults;
import edu.drexel.cs.jah473.util.Output;


public class Fix2 {

    public static void main(String[] args) throws SQLException, IOException {
        class Res extends QueryResults {
            int id;
            String type;
            int start;
            int end;
        }
        class Way{
            int id;
            String type;
            Way(int id, String type){
                this.id = id;
                this.type = type;
            }
           @Override
           public String toString(){
               return "id: "+id+" type: "+type;
           }
        }
        Map<String,Way> seen = new HashMap<>();
        QueryManager qm = new QueryManager("maps3.sqlite3");
        qm.addQuery("q", "select id, type, start, end from way", Res.class);
        Res res = qm.executeQuery("q");
        PrintWriter out = Output.toFile("remove.sql");
        out.write("BEGIN TRANSACTION;\n");
        while(res.nextRow()){
            String concat = res.start + " " + res.end;
            Way w = seen.get(concat);
            Way w2 = new Way(res.id,res.type);
            if(w == null){
                seen.put(concat, w2);
            }
            else {
                if(w.type.equals("")){
                    out.write("DELETE FROM way WHERE id = "+w.id+";\n");
                }
                else {
                    out.write("DELETE FROM way WHERE id = "+w2.id+";\n");
                }
            }
        }
        out.write("COMMIT;");
        out.close();
        qm.close();
    }
}
