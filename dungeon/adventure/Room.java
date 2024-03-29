package dungeon.adventure;

import java.io.Serializable;
import java.util.*;

public class Room implements Serializable {
    private final List<Item> myItems = new ArrayList<>();
    private final Map<Direction, Boolean> myDoors;
    private final Pair myLocation;
    private final List<Monster> myMonsters = new ArrayList<>();
    private boolean isVisible = false;

    public Room(Pair theLocation) {
        myLocation = theLocation;
        myDoors = new HashMap<>();
    }

    public Pair getMyLocation() {
        return myLocation;
    }

    public Boolean getDoor(Direction theDirection) {
        // returns true if a door is open/present. otherwise it's a locked door/wall.
        return myDoors.getOrDefault(theDirection, false);
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

    public void setVisible() {
        isVisible = true;
    }

    public boolean getVisible() {
        return isVisible;
    }

    public void setDoor(Direction theDirection) {
        myDoors.put(theDirection, true);
    }

    public List<Monster> getMyMonsters() {
        return myMonsters;
    }

    public void addMonster(Monster theMonster) {
        myMonsters.add(theMonster);
    }
}
