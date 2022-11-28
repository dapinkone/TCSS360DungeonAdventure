package tcss.dungeonadventure;

import java.util.Set;

public final class DefaultModel implements GameModel {
    private Dungeon myDungeon;
    public DefaultModel() {

    }

    @Override
    public void newDungeon(final int theRows, final int theColumns) {
        myDungeon = new Dungeon(theRows, theColumns);
    }

    public void saveGame() {

    }

    public void loadGame() {

    }

    @Override
    public void setHero(final Hero theHero) {
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
    public Item[] getRoomItems(final Pair theRoomLocation) {
        //return myDungeon.getCurrentRoomItems();
        return null;
    }

    @Override
    public Set<Direction> getRoomDoors(final Pair theRoomLocation) {
        return myDungeon.getRoomDoors(theRoomLocation);
    }

    @Override
    public boolean pickupItem(final Item theItem) {
        return false;
    }

    @Override
    public boolean useItem(final Item theItem) {
        return false;
    }

    @Override
    public boolean move(final Direction theDirection) {
        if (checkCombat()) {
            return false; // currently in combat. can't move.
        }
        for (var otherDirection : myDungeon.getCurrentRoomDoors()) {
            if (theDirection == otherDirection) {
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
