package DungeonAdventure;

import java.util.List;
import java.util.Set;

public class DefaultModel implements GameModel {
    private Dungeon myDungeon;
    private Combat myCombat;
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
    public List<Item> getRoomItems(Pair p) {
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
        final var location = getHeroLocation();
        for(var otherDirection : myDungeon.getCurrentRoomDoors()) {
            if(theDirection == otherDirection) { // door is open. proceed.
                myDungeon.setMyHeroLocation(
                        switch (theDirection) {
                        case WEST -> new Pair(location.getRow(), location.getColumn()-1);
                        case EAST -> new Pair(location.getRow(), location.getColumn()+1);
                        case NORTH -> new Pair(location.getRow()-1, location.getColumn());
                        case SOUTH -> new Pair(location.getRow()+1, location.getColumn());
                });
                // if the new room has monsters in it, we have a combat encounter on our hands.
                List<Monster> monsters = myDungeon.getRoom(myDungeon.getMyHeroLocation()).getMyMonsters();
                if(monsters.size() > 0) {
                    myCombat = new Combat(monsters, getHero());
                }
                return true;
            }
        }
        // current room does not have an open door/hall in that direction.
        return false;
    }

    @Override
    public boolean checkCombat() {
        return myCombat != null && !myCombat.isOver();
    }

    @Override
    public Room[][] getRooms() {
        return myDungeon.getRooms();
    }

    @Override
    public boolean gameover() {
        // test if character dead or victory condition
        if(getHero().isDead()) return true;
        final var localItems = getRoomItems(getHeroLocation());
        if(localItems == null || localItems.isEmpty()) return false; // have to be on the exit.
        return (
                localItems.get(0) == Item.Exit
                        && getHero().getPillars().size() == 4);
    }
}
