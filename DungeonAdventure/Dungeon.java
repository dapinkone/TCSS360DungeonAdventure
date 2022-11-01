package DungeonAdventure;

public class Dungeon {
    private Room[][] myRooms;

    public Hero getMyHero() {
        return myHero;
    }

    public void setMyHero(Hero theHero) {
        this.myHero = theHero;
    }

    private Hero myHero;
    public void setMyHeroLocation(int[] theHeroLocation) {
        // TODO: data validation
        this.myHeroLocation = theHeroLocation;
    }

    private int[] myHeroLocation; // TODO: potentially a Pair() or Coordinates() class here?
    private void generateMaze() {

    }

    public int[] getMyHeroLocation() {
        return myHeroLocation;
    }
    public Item[] getCurrentRoomItems() {
        return null;
    }
    public String getCurrentRoomDoors() {
        return null;
    }
}
