package dungeon.adventure.Test;

import dungeon.adventure.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DungeonTest {
    @Test
    public void testRightNumberOfRooms() {
        // verifying that the constructor for Dungeon creates the requested # of rooms.
        final int maxWidth = 20;
        final int maxHeight = 20;

        for (int height = 3; height < maxHeight; height++) {
            for (int width = 3; width < maxWidth; width++) {
                final Dungeon d = new Dungeon(height, width);
                Assertions.assertEquals(d.getRooms().length, height);
                Assertions.assertEquals(d.getRooms()[0].length, width);
            }
        }
    }
}
