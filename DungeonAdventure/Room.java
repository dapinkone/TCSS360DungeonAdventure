package DungeonAdventure;

public class Room {
    private Item[] myItems;
    private final boolean[] myDoors;
    public Room() {
        myDoors = new boolean[4];
    }
    public Room(boolean[] theDoors) {
        myDoors = theDoors;
    }

    public Item[] getMyItems() {
        return myItems;
    }

    public void setMyItems(Item[] myItems) {
        this.myItems = myItems;
    }
}
