package DungeonAdventure;

import java.util.*;
import java.util.stream.Collectors;

public class Room {
    private List<Item> myItems = new ArrayList<>();
    private final Map<Direction, Boolean> myDoors;
    private final Pair myLocation;
    public Room(Pair theLocation) {
        myLocation = theLocation;
        myDoors = new HashMap<>();
    }

    public Pair getMyLocation() {
        return myLocation;
    }

    public Boolean getDoor(Direction d) {
        // returns true if a door is open/present. otherwise it's a locked door/wall.
        return myDoors.getOrDefault(d, false);
    }
    public Set<Direction> getDoors() {
        return myDoors.keySet();
    }
    public List<Item> getMyItems() {
        return myItems;
    }

    public void addToMyItems(Item theItem) {
        myItems.add(theItem);
    }

    @Override
    public String toString() {
        return "Room{" + "myItems=" + myItems +
                ", myDoors=" + myDoors +
                ", myLocation=" + myLocation +
                '}';
    }

    public void setDoor(Direction d) {
        myDoors.put(d, true);
    }

}
