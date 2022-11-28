package tcss.dungeonadventure.test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tcss.dungeonadventure.Direction;
import tcss.dungeonadventure.Dungeon;
import tcss.dungeonadventure.Room;


public class DungeonGenTest {

    /**
     * testRightNumberOfRooms:
     * tests that the Dungeon generation algorithm initializes a 2-d array
     * of Room objects.
     */
    @Test
    public void testRightNumberOfRooms() {
        final int maxWidth = 40;
        final int maxHeight = 40;

        for (int height = 1; height < maxHeight; height++) {
            for (int width = 1; width < maxWidth; width++) {
                final Dungeon d = new Dungeon(height, width);
                Assertions.assertEquals(d.getRooms().length, height);
                Assertions.assertEquals(d.getRooms()[0].length, width);
            }
        }
    }

    /***
     * tests that a 2x2 dungeon generates as we expect.
     */
    @Test
    public void testOpenDoors2By2() {
        Dungeon.RANDOM.setSeed(0L); // tests need to be non-random.
        final Dungeon d = new Dungeon(2, 2);

        final Room[][] roomsGrid = d.getRooms();
        final Room topLeft = roomsGrid[0][0];
        final Room topRight = roomsGrid[0][1];
        final Room botLeft = roomsGrid[1][0];
        final Room botRight = roomsGrid[1][1];

        // check that proper doors are open/closed as expected
        // all these doors should be open:
        assert topLeft.getDoor(Direction.EAST);
        assert topRight.getDoor(Direction.SOUTH);
        assert botRight.getDoor(Direction.WEST);
        assert botLeft.getDoor(Direction.EAST);
    }
}
