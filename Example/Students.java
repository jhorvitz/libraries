
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.drexel.cs.jah473.datastructures.TallyMap;
import edu.drexel.cs.jah473.util.Input;
import edu.drexel.cs.jah473.util.Strings;


public class Students {

    public static void main(String[] args) throws IOException {
        BufferedReader br = Input.fromFile("students.csv");
        TallyMap<String> tm = new TallyMap<>();
        while(br.ready()){
            String line = Strings.removePunctuation(br.readLine());
            tm.increment(line);
            br.readLine();
        }
        tm.entrySet().stream().sorted((e1,e2)->e2.getValue().compareTo(e1.getValue())).forEachOrdered(System.out::println);
        System.out.println(tm.getMaxKeys());
        System.out.println(tm.size());
        br.close();
    }

}
