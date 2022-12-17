package dungeon.adventure;

import java.io.Serializable;
import java.util.Map;

public enum Direction implements Serializable {
    NORTH, SOUTH, EAST, WEST;
    private static final Map<Direction, Direction> INVERT_MAP = Map.of(
            NORTH, SOUTH,
            SOUTH, NORTH,
            EAST, WEST,
            WEST, EAST
    );
    /***
     @return the inversion of a given direction.
     */
    public Direction invert() {
        return INVERT_MAP.get(this);
    }
}
