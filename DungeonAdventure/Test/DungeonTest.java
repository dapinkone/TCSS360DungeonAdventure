package DungeonAdventure.Test;

import DungeonAdventure.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class DungeonTest {
    @Test
    public void testRightNumberOfRooms() {
        // verifying that the constructor for Dungeon creates the requested # of rooms.
        final int maxWidth = 40, maxHeight = 40;

        for(int height=1; height < maxHeight; height++) {
            for (int width = 1; width < maxWidth; width++) {
                Dungeon d = new Dungeon(height, width);
                Assertions.assertEquals(d.getRooms().length, height);
                Assertions.assertEquals(d.getRooms()[0].length, width);
            }
        }
    }
    @Test
    public void testOpenDoors2By2() {
        Dungeon.RANDOM.setSeed(0L); // tests need to be non-random.
        Dungeon d = new Dungeon(2,2);

        final Room[][] theRooms = d.getRooms();
        final Room topLeft = theRooms[0][0];
        final Room topRight = theRooms[0][1];
        final Room botLeft = theRooms[1][0];
        final Room botRight = theRooms[1][1];

        // check that proper doors are open/closed as expected
        // all these doors should be open:
        assert topLeft.getDoor(Direction.EAST);
        assert topRight.getDoor(Direction.SOUTH);
        assert botRight.getDoor(Direction.WEST);
        assert botLeft.getDoor(Direction.EAST);
    }
}
