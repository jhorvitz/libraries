
import edu.drexel.cs.jah473.distance.KDPoint;

@SuppressWarnings("serial")
public class Player extends KDPoint {

    private String name;
    private boolean hof;

    public Player(String name, double avg, int hr, int r, int rbi, int sb, boolean hof) {
        super(avg, hr, r, rbi, sb);
        this.name = name;
        this.hof = hof;
    }

    boolean isHallOfFamer() {
        return hof;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (hof != other.hof)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
