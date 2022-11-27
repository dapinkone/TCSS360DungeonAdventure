package DungeonAdventure;

import java.util.Set;

public class DefaultModel implements GameModel {
    private Dungeon myDungeon;
    public DefaultModel() {

    }

    @Override
    public void newDungeon(int rows, int cols) {
        myDungeon = new Dungeon(rows, cols);
    }

    public void saveGame() {

    }

    public void loadGame() {

    }

    @Override
    public void setHero(Hero theHero) {
        myDungeon.setHero(theHero);
    }

    @Override
    public Hero getHero() {
        return myDungeon.getHero();
    }

    @Override
    public Pair getHeroLocation() {
        return myDungeon.getMyHeroLocation();
    }

    @Override
    public Item[] getRoomItems(Pair p) {
        return myDungeon.getCurrentRoomItems();
    }

    @Override
    public Set<Direction> getRoomDoors(Pair p) {
        return myDungeon.getRoomDoors(p);
    }

    @Override
    public boolean pickupItem(Item theItem) {
        return false;
    }

    @Override
    public boolean useItem(Item theItem) {
        return false;
    }

    @Override
    public boolean move(Direction theDirection) {
        if(checkCombat()) {
           return false; // currently in combat. can't move.
        }
        for(var otherDirection : myDungeon.getCurrentRoomDoors()) {
            if(theDirection == otherDirection) {
                return true;
            }
        }
        // current room does not have an open door/hall in that direction.
        return false;
    }

    @Override
    public boolean checkCombat() {
        return false;
    }

    @Override
    public Room[][] getRooms() {
        return myDungeon.getRooms();
    }
}
