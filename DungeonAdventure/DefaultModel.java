package DungeonAdventure;

import java.io.*;
import java.util.*;

public class DefaultModel implements GameModel {
    private Dungeon myDungeon;
    private Combat myCombat;
    private final boolean cheatCanFleeCombat = false;
    private final RecordQ myRecordQ = RecordQ.getInstance();
    private final ArrayList<Item> newItems = new ArrayList<>();
    public DefaultModel() {

    }
    @Override
    public RecordQ getMyRecordQ() {
        return myRecordQ;
    }
    @Override
    public void newDungeon(int rows, int cols) {
        myDungeon = new Dungeon(rows, cols);
    }

    @Override
    public void saveGame(File selectedFile) throws IOException {
        try (var fileOut = new FileOutputStream(selectedFile)) {
            final var objStreamOut = new ObjectOutputStream(fileOut);
            objStreamOut.writeObject(myDungeon);
            objStreamOut.flush();
        }
    }
    @Override
    public void loadGame(File selectedFile) throws IOException {
        try (var fileIn = new FileInputStream(selectedFile)) {
            final var objStreamIn = new ObjectInputStream(fileIn);
            myDungeon = (Dungeon) objStreamIn.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void newGame(final int theRows, final int theCols) {
        // clear and rebuild game state.
        myCombat = null;
        myRecordQ.clear();
        newItems.clear();
        newDungeon(theRows, theCols);
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
        final var roomItems = getRoomItems(getHeroLocation());

        if(!theItem.canBePickedUp() || !roomItems.contains(theItem)) {
            return false;
        }
        // for any item, get the # held, and increase by one in inventory.
        final var inv = getHero().getMyInventory();
        final var count = inv.getOrDefault(theItem, 0);
        inv.put(theItem, count + 1);
        newItems.add(theItem);
        //System.out.println("Picked up: " + theItem); // TODO: remove.
        roomItems.remove(theItem);
        return true;
    }

    @Override
    public boolean useItem(Item theItem) {
        return false;
    }

    @Override
    public boolean move(Direction theDirection) {
        if(checkCombat() && !cheatCanFleeCombat) {
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
                final var theRoom =  myDungeon.getRoom(getHeroLocation());

                // if we have all the pillars, and this room is the exit, we spawn boss
                if(getHero().hasAllPillars() && theRoom.getMyItems().contains(Item.Exit)) {
                    spawnBossFight();
                }
                List<Monster> monsters = theRoom.getMyMonsters();
                if(monsters != null && monsters.size() > 0) {
                    myCombat = new Combat(monsters, getHero());
                }
                // automagically pick up any items?
                final List<Item> localItems = new ArrayList<>(myDungeon.getCurrentRoomItems());

                for(var item : localItems) {
                    if(item.canBePickedUp())
                        pickupItem(item);
                }
                return true;
            }
        }
        // current room does not have an open door/hall in that direction.
        return false;
    }

    @Override
    public boolean checkCombat() {
        if(myCombat == null) return false;
        if(myCombat.isOver()) {
            myCombat = null;
            return false;
        }
        return true;
    }
    @Override
    public Combat getMyCombat() {
        return myCombat;
    }
    @Override
    public Room[][] getRooms() {
        return myDungeon.getRooms();
    }
    @Override
    public Room getRoom(Pair theCoord) {
        return myDungeon.getRoom(theCoord);
    }


    @Override
    public void spawnBossFight() {
        myDungeon.spawnBossFight();
    }

    @Override
    public boolean gameover() {
        // test if character dead or victory condition
        if(getHero().isDead()) return true;
        final var localItems = getRoomItems(getHeroLocation());
        if(localItems == null || localItems.isEmpty()) return false; // have to be on the exit.
        return localItems.get(0) == Item.Exit && getHero().hasAllPillars();
    }

    /***
     * retrieves and returns the head of the gameEventsQueue;
     * returns null if the queue is empty.
     * @return gameEvent : the next event which is to be processed.
     */
    @Override
    public HealthChangeRecord nextGameRecord() {
        return myRecordQ.poll();
    }

    @Override
    public ArrayList<Item> checkNewItems() {
        final ArrayList<Item> ret = new ArrayList<>(newItems);
        newItems.clear();
        return ret;
    }
    @Override
    public void useVisionPot() {
        myDungeon.useVisionPot();
    }
}
