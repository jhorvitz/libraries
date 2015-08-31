
import java.util.function.Consumer;

public class Direction {
    private String str;
    private Consumer<Position> mover;

    Direction(String str, Consumer<Position> mover) {
        this.str = str;
        this.mover = mover;
    }

    void move(Position pos) {
        mover.accept(pos);
    }

    @Override
    public String toString() {
        return str;
    }

}
