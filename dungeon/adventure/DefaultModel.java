package dungeon.adventure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DefaultModel implements GameModel {
    /***
     * Queue of health change records for communication of events between model
     * and view.
     */
    private final RecordQ myRecordQ = RecordQ.getInstance();
    /***
     * Stores recently acquired items.
     */
    private final ArrayList<Item> myNewItems = new ArrayList<>();
    /***
     * myDungeon stores primary game state.
     */
    private Dungeon myDungeon;
    /***
     * Stores information about current combat encounter.
     */
    private Combat myCombat;

    /***
     * builds a default GameModel, awaits data given by setters.
     */
    public DefaultModel() {

    }

    /***
     * Returns the instance of RecordQ the game model is using.
     * @return RecordQ
     */
    @Override
    public RecordQ getMyRecordQ() {
        return myRecordQ;
    }
    /***
     * Encapsulates all the functionality and
     * interactions required to play the game.
     * @param theRows The height of the maze/dungeon.
     * @param theCols The width of the maze/dungeon.
     */
    @Override
    public void newDungeon(final int theRows, final  int theCols) {
        myDungeon = new Dungeon(theRows, theCols);
    }
    /***
     * saveGame() saves game state(dungeon object) to the selected file.
     * @param theSelectedFile file object destination.
     * @throws IOException Some permissions/IO issue happened?
     */
    @Override
    public void saveGame(final File theSelectedFile) throws IOException {
        try (var fileOut = new FileOutputStream(theSelectedFile)) {
            final var objStreamOut = new ObjectOutputStream(fileOut);
            objStreamOut.writeObject(myDungeon);
            objStreamOut.flush();
        }
    }
    /***
     * loadGame() loads a serialized Dungeon object from a given File object.
     * @param theSelectedFile file object source.
     * @throws IOException Throws if deserialization or read fails.
     */
    @Override
    public void loadGame(final File theSelectedFile) throws IOException {
        try (var fileIn = new FileInputStream(theSelectedFile)) {
            final var objStreamIn = new ObjectInputStream(fileIn);
            myDungeon = (Dungeon) objStreamIn.readObject();
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /***
     * Builds a new game state.
     * @param theRows The height of the dungeon/maze.
     * @param theCols The width of the dungeon/maze.
     */
    @Override
    public void newGame(final int theRows, final int theCols) {
        // clear and rebuild game state.
        myCombat = null;
        myRecordQ.clear();
        myNewItems.clear();
        newDungeon(theRows, theCols);
    }

    /***
     * Our intrepid hero.
     * @return Hero
     */
    @Override
    public Hero getHero() {
        return myDungeon.getHero();
    }
    /***
     * Sets the Hero in the model, so we can do a hero selection screen.
     * @param theHero Our intrepid hero.
     */
    @Override
    public void setHero(final Hero theHero) {
        myDungeon.setHero(theHero);
    }
    /***
     * The hero's current location in the dungeon.
     * @return Pair location.
     */
    @Override
    public Pair getHeroLocation() {
        return myDungeon.getMyHeroLocation();
    }
    /***
     * Returns the items in a given room location so the view can render.
     * @param theLocation Pair of row, column
     * @return List of items found at theLocation.
     */
    @Override
    public List<Item> getRoomItems(final Pair theLocation) {
        return myDungeon.getCurrentRoomItems();
    }
    /***
     * View needs to see doors/openings to display, and for player navigation.
     * @param theLocation The location to be queried.
     * @return Set of Direction objects which have open/passable doors.
     */
    @Override
    public Set<Direction> getRoomDoors(final Pair theLocation) {
        return myDungeon.getRoomDoors(theLocation);
    }
    /***
     * Picks up theItem in the current room.
     * @param theItem Desired item.
     */
    @Override
    public void pickupItem(final Item theItem) {
        final var roomItems = getRoomItems(getHeroLocation());

        if (!theItem.canBePickedUp() || !roomItems.contains(theItem)) {
            return;
        }
        // for any item, get the # held, and increase by one in inventory.
        final var inv = getHero().getMyInventory();
        final var count = inv.getOrDefault(theItem, 0);
        inv.put(theItem, count + 1);
        myNewItems.add(theItem);
        roomItems.remove(theItem);
    }
    /***
     * Use a given inventory item, and return success/failure.
     * @param theItem Item to be used.
     * @return success or failure of attempt.
     */
    @Override // not yet implemented
    public boolean useItem(final Item theItem) {
        return false;
    }
    /***
     * Move the hero in a given Direction within the dungeon. May fail if
     * hero is in combat, or desired Direction is not free of obstruction.
     * @param theDirection direction of movement.
     * @return success or failure of movement.
     */
    @Override
    public boolean move(final Direction theDirection) {
        /*
         * If true, permits player to run from combat.
         */
        boolean cheatCanFleeCombat = false;
        if (checkCombat() && !cheatCanFleeCombat) {
            return false; // currently in combat. can't move.
        }
        final var location = getHeroLocation();
        for (var otherDirection : myDungeon.getCurrentRoomDoors()) {
            if (theDirection == otherDirection) { // door is open. proceed.
                myDungeon.setMyHeroLocation(
                        newLocationFrom(theDirection, location));
                // if the new room has monsters in it,
                // we have a combat encounter on our hands.
                final var room = myDungeon.getRoom(getHeroLocation());

                // if we have all the pillars, and this room is the exit,
                // we spawn boss.
                if (getHero().hasAllPillars()
                        && room.getMyItems().contains(Item.Exit)) {
                    spawnBossFight();
                }
                final List<Monster> monsters = room.getMyMonsters();
                if (monsters != null && monsters.size() > 0) {
                    myCombat = new Combat(monsters, getHero());
                }
                // automagically pick up any items?
                final List<Item> localItems = new ArrayList<>(
                        myDungeon.getCurrentRoomItems());

                for (var item : localItems) {
                    if (item.canBePickedUp()) {
                        pickupItem(item);
                    }
                }
                return true;
            }
        }
        // current room does not have an open door/hall in that direction.
        return false;
    }

    /***
     * Gives a new location coordinate relative to the given position.
     * @param theDirection the given relative direction.
     * @param theLoc the given location.
     * @return new location Pair
     */
    private Pair newLocationFrom(final Direction theDirection,
                                 final Pair theLoc) {
        return switch (theDirection) {
            case WEST ->
                    new Pair(theLoc.row(), theLoc.column() - 1);
            case EAST ->
                    new Pair(theLoc.row(), theLoc.column() + 1);
            case NORTH ->
                    new Pair(theLoc.row() - 1, theLoc.column());
            case SOUTH ->
                    new Pair(theLoc.row() + 1, theLoc.column());
        };
    }
    /***
     * Is the hero currently in combat?
     * @return true for yes. false for no.
     */
    @Override
    public boolean checkCombat() {
        if (myCombat == null) {
            return false;
        }
        if (myCombat.isOver()) {
            myCombat = null;
            return false;
        }
        return true;
    }
    /***
     * Combat object encapsulates combat/turn ordering.
     * @return Combat object.
     */
    @Override
    public Combat getMyCombat() {
        return myCombat;
    }
    /***
     * Used to get more detail about specific rooms in the dungeon structure.
     * @return Room[][] dungeon rooms.
     */
    @Override
    public Room[][] getRooms() {
        return myDungeon.getRooms();
    }
    /***
     * Gets a specific room by location.
     * @param theCoord location pair.
     * @return Room object from the dungeon.
     */
    @Override
    public Room getRoom(final Pair theCoord) {
        return myDungeon.getRoom(theCoord);
    }

    /***
     * When the hero has collected all the pillars, and arrives at the exit,
     * the model spawns a boss fight.
     */
    private void spawnBossFight() {
        myDungeon.spawnBossFight();
    }
    /***
     * Checks if the game is over(hero is dead, or victorious).
     * @return true/false for game condition.
     */
    @Override
    public boolean gameOver() {
        // test if character dead or victory condition
        if (getHero().isDead()) {
            return true;
        }
        return victoryCondition();
    }

    /***
     * Checks victory condition.
     * @return true if Hero is not dead, has all pillars, and stands alone,
     * outside combat, at the exit.
     */
    @Override
    public boolean victoryCondition() {
        final var hero = getHero();
        final var atExit = myDungeon.getCurrentRoomItems().contains(Item.Exit);
        return !hero.isDead()
                && hero.hasAllPillars()
                && atExit
                && !checkCombat();
    }

    /***
     * Used to get the next record from the RecordQ for rendering code.
     * @return HealthChangeRecord
     */
    @Override
    public HealthChangeRecord nextGameRecord() {
        return myRecordQ.poll();
    }
    /***
     * Records and returns the items which have been auto-picked-up upon the
     * hero's entry into the room.
     * @return List of Items
     */
    @Override
    public ArrayList<Item> checkNewItems() {
        final ArrayList<Item> ret = new ArrayList<>(myNewItems);
        myNewItems.clear();
        return ret;
    }
    /***
     * Uses a vision pot to show adjacent rooms.
     */
    @Override
    public void useVisionPot() {
        myDungeon.useVisionPot();
    }
}
