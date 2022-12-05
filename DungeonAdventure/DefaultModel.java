package DungeonAdventure;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DefaultModel implements GameModel {
    private Dungeon myDungeon;
    private Combat myCombat;
    private final boolean cheatCanFleeCombat = true;
    public DefaultModel() {

    }

    @Override
    public void newDungeon(int rows, int cols) {
        myDungeon = new Dungeon(rows, cols);
    }

    public void saveGame() throws IOException {
        try (var fileOut = new FileOutputStream("CaveRescue.save")) {
            final var objStreamOut = new ObjectOutputStream(fileOut);
            objStreamOut.writeObject(myDungeon);
            objStreamOut.flush();
        }
    }

    public void loadGame() throws IOException {
        try (var fileIn = new FileInputStream("CaveRescue.save")) {
            final var objStreamIn = new ObjectInputStream(fileIn);
            myDungeon = (Dungeon) objStreamIn.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
        final var cantPickup = List.of(Item.Entrance, Item.Exit, Item.Pit);
        final var roomItems = getRoomItems(getHeroLocation());

        if(cantPickup.contains(theItem) || !roomItems.contains(theItem)) {
            return false;
        }
        // for any item, get the # held, and increase by one in inventory.
        final var inv = getHero().getMyInventory();
        final var count = inv.getOrDefault(theItem, 0);
        inv.put(theItem, count + 1);
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
                List<Monster> monsters = myDungeon.getRoom(getHeroLocation()).getMyMonsters();
                if(monsters != null && monsters.size() > 0) {
                    System.out.println("Combat encountered: ");
                    for(var m : monsters) {
                        System.out.println(m.getStats());
                    }
                    myCombat = new Combat(monsters, getHero());
                }
                // automagically pick up any items?
                final List<Item> localItems = new ArrayList<>(myDungeon.getCurrentRoomItems());
                for(var item : localItems) {
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
        return localItems.get(0) == Item.Exit && getHero().hasAllPillars();
    }
}
