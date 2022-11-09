package DungeonAdventure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Room {
    private Item[] myItems;
    private final Map<Direction, Boolean> myDoors;
    private final Pair myLocation;
    public Room(Pair theLocation) {
        myLocation = theLocation;
        myDoors = new HashMap<>();
    }
    public Boolean getDoor(Direction d) {
        return myDoors.getOrDefault(d, false);
    }
    public Item[] getMyItems() {
        return myItems;
    }

    public void setMyItems(Item[] myItems) {
        this.myItems = myItems;
    }

    @Override
    public String toString() {
        return "Room{" + "myItems=" + Arrays.toString(myItems) +
                ", myDoors=" + myDoors +
                ", myLocation=" + myLocation +
                '}';
    }

    public void setDoor(Direction d) {
        myDoors.put(d, true);
    }

}
