package dungeon.adventure;

import java.io.Serializable;
import java.util.Map;

public enum Direction implements Serializable {
    NORTH, SOUTH, EAST, WEST;
    private static final Map<Direction, Direction> invertMap = Map.of(
            NORTH, SOUTH,
            SOUTH, NORTH,
            EAST, WEST,
            WEST, EAST
    );

    public Direction invert() {
        /*
         returns the inversion of a given direction.
         */
        return invertMap.get(this);
    }
}
