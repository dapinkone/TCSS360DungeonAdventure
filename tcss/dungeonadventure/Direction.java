package tcss.dungeonadventure;
import java.io.Serializable;
import java.util.Map;

public enum Direction implements Serializable {
    /*** enum constant ordinal 0. */
    NORTH,
    /*** enum constant ordinal 1. */
    SOUTH,
    /*** enum constant ordinal 2. */
    EAST,
    /*** enum constant ordinal 4. */
    WEST;
    /***
     * maps each cardinal direction to it's opposite.
     */
    private static final Map<Direction, Direction> INVERT_MAP = Map.of(
            NORTH, SOUTH,
            SOUTH, NORTH,
            EAST, WEST,
            WEST, EAST
    );

    /***
     * @return the inversion/opposite of the current direction.
     */
    public Direction invert() {
        /*
         returns the inversion of a given direction.
         */
        return INVERT_MAP.get(this);
    }
}
