package dungeon.adventure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public final class Dungeon implements Serializable {
    /***
     * Random object used for item & room generation.
     */
    public static final Random RANDOM = new Random();
    /***
     * The height of the dungeon.
     */
    private final int myRows;
    /***
     * The width of the dungeon.
     */
    private final int myColumns;
    /***
     * Listing of all valid coordinate pairs in the dungeon's range.
     * Simplify bounds checking at the cost of some RAM.
     */
    private final Set<Pair> myAllCoords = new HashSet<>();
    /***
     * Data structure that holds information about the dungeon rooms,
     * or the "board" on which we play the game.
     */
    private Room[][] myRooms;
    /***
     * Our intrepid hero, as given by setHero().
     */
    private Hero myHero;
    /***
     * Hero's current location in the dungeon.
     */
    private Pair myHeroLocation = new Pair(0, 0);

    /***
     * Main worker of Dungeon.java, constructs rooms, generates the maze doors,
     * monsters, items, etc.
     * @param theRows Dungeon Height.
     * @param theColumns Dungeon width.
     */
    public Dungeon(final int theRows, final int theColumns) {
        this.myRows = theRows;
        this.myColumns = theColumns;
        makeRooms();
        generateMaze();
        spawnItems();
        spawnMonsters();
    }

    /**
     * Spawns a random monster at the given theLocation.
     * @param theLocation Pair theLocation in the dungeon.
     */
    private void spawnMonster(final Pair theLocation) {
        final var chance = RANDOM.nextDouble();
        final var mf = MonsterFactory.getInstance();
        final Monster monster;
        if (chance > 0.7) {
            monster = mf.generateMonster("predator");
        } else if (chance > 0.4) {
            monster = mf.generateMonster("Crawler");
        } else {
            monster = mf.generateMonster("Skitter");
        }
        getRoom(theLocation).addMonster(monster);
    }

    /**
     * Populates the dungeon with monsters at a given rate.
     */
    private void spawnMonsters() {
        // for every room in the maze, maybe add a monster
        for (var coord : myAllCoords) {
            final var room = getRoom(coord);
            // don't want to start the game in combat.
            if (room.getMyItems().contains(Item.Entrance)) {
                continue;
            }

            if (RANDOM.nextDouble() > 0.8) { // 20% chance to see a monster at all
                spawnMonster(coord);
            }
            if (getRoom(coord).getMyItems().stream().anyMatch(
                    // double monsters in pillar or exit rooms.
                    e -> e.name().contains("Pillar"))) {
                spawnMonster(coord);
            }
        }
    }

    /**
     * Spawns the boss fight for the final event.
     */
    public void spawnBossFight() {
        for (var coord : myAllCoords) { // find exit
            if (getRoom(coord).getMyItems().contains(Item.Exit)) {
                spawnMonster(coord);
                spawnMonster(coord);
                spawnMonster(coord);
            }
        }
    }

    /***
     * @return myColumns, the number of columns in the dungeon.
     */
    public int getColumns() {
        return myColumns;
    }

    /***
     * @return myRows, the number of rows in the dungeon.
     */
    public int getRows() {
        return myRows;
    }

    /***
     * The hero's current location in the dungeon.
     * @return Pair of (row, column)
     */
    public Pair getMyHeroLocation() {
        return myHeroLocation;
    }

    /***
     * Sets the hero's location, used to move the hero.
     * @param theHeroLocation new location.
     */
    public void setMyHeroLocation(final Pair theHeroLocation) {
        // if coordinate is valid
        if (myAllCoords.contains(theHeroLocation)) {
            myHeroLocation = theHeroLocation;
            getRoom(myHeroLocation).setVisible();
        }
    }

    /**
     * Populates the room arraw with empty rooms.
     */
    private void makeRooms() {
        // we need to generate the Rooms, coordinates, etc
        myRooms = new Room[myRows][];
        for (int row = 0; row < myRows; row++) {
            final Room[] rowArr = new Room[myColumns];
            for (int col = 0; col < myColumns; col++) {
                final Pair roomLocation = new Pair(row, col);
                myAllCoords.add(roomLocation);
                final Room newRoom = new Room(roomLocation);
                rowArr[col] = newRoom;
            }
            myRooms[row] = rowArr;
        }
    }
    /***
     * Gets a specific room by location.
     * @param theLocation location pair.
     * @return Room object from the dungeon.
     */
    public Room getRoom(final Pair theLocation) {
        return myRooms[theLocation.row()][theLocation.column()];
    }

    /***
     * spawnItems
     * populates the dungeon Rooms with required Item objects, according
     * to hard-coded required frequencies, with some hard upper limits.
     */
    private void spawnItems() {
        /*
         * itemRates determines the chance of each item that should spawn in the
         * dungeon, as well as any upper bounds on the # that can spawn.
         */
        final Map<Item, ItemRecord> itemRates = new HashMap<>();
        // is there a cleaner way to do this?
        // exit, entrance, as well as pillars 100% must spawn,
        // and must spawn exactly once.
        itemRates.put(Item.Exit,
                new ItemRecord(1.0, 1));
        itemRates.put(Item.Entrance,
                new ItemRecord(1.0, 1));
        itemRates.put(Item.PillarAbstraction,
                new ItemRecord(1.0, 1));
        itemRates.put(Item.PillarInheritance,
                new ItemRecord(1.0, 1));
        itemRates.put(Item.PillarEncapsulation,
                new ItemRecord(1.0, 1));
        itemRates.put(Item.PillarPolymorphism,
                new ItemRecord(1.0, 1));
        // Healing potions, vision potions and pits are a bit less common.
        // we'll go 20% chance, with no upper limit.
        itemRates.put(Item.HealingPotion,
                new ItemRecord(0.2, Integer.MAX_VALUE));
        itemRates.put(Item.VisionPotion,
                new ItemRecord(0.1, Integer.MAX_VALUE));
        itemRates.put(Item.Pit,
                new ItemRecord(0.1, Integer.MAX_VALUE));
        //
        //
        final var shuffledCoords = new ArrayList<>(myAllCoords);
        Collections.shuffle(shuffledCoords);

        Room choice;
        ROOM:
        for (var coord : shuffledCoords) {
            choice = getRoom(coord);
            for (Item i : Item.values()) {
                final ItemRecord rec = itemRates.get(i);
                if (rec == null) {
                    throw new NoSuchElementException("Item " + i + "unknown.");
                }
                if (rec.myMaxOccurrence == 1 && rec.myDropChance == 1) {
                    choice.addToMyItems(i);
                    rec.decrement();
                    // hero is placed at start of dungeon automagically:
                    if (i == Item.Entrance) {
                        setMyHeroLocation(choice.getMyLocation());
                    }
                    // these(entrance/exit/pillars) are only one to a room
                    continue ROOM;
                }
                if (rec.getMyMaxOccurrence() > 0
                        && RANDOM.nextDouble() <= rec.getMyDropChance()) {
                    choice.addToMyItems(i);
                    rec.decrement();
                }
            }
        }
        // ensure the required items have been placed.
        for (Item i : Item.values()) {
            final ItemRecord rec = itemRates.get(i);
            if (rec.getMyDropChance() == 1 && rec.getMyMaxOccurrence() > 0) {
                throw new IllegalArgumentException(
                        "Maze size not large enough for items.");
            }
        }
    }
    /***
     * Used to get more detail about specific rooms in the dungeon structure.
     * @return Room[][] dungeon rooms.
     */
    public Room[][] getRooms() {
        return myRooms;
    }
    /***
     * Our intrepid hero.
     * @return Hero
     */
    public Hero getHero() {
        return myHero;
    }
    /***
     * Sets the Hero in the model, so we can do a hero selection screen.
     * @param theHero Our intrepid hero.
     */
    public void setHero(final Hero theHero) {
        this.myHero = theHero;
    }

    /***
     * @return List of Items which are left in the current room.
     */
    public List<Item> getCurrentRoomItems() {
        return getRoom(myHeroLocation).getMyItems();
    }

    /***
     * Returns set of doors /walls in the current room which are passable.
     * @return Set of Directions
     */
    public Set<Direction> getCurrentRoomDoors() {
        return getRoomDoors(myHeroLocation);
    }

    /***
     * returns the doors for a given room.
     * @param theLocation the room's location
     * @return Set of doors.
     */
    public Set<Direction> getRoomDoors(final Pair theLocation) {
        return myRooms[theLocation.row()][theLocation.column()].getDoors();
    }

    /**
     * Creates a list of ChoicePairs from the given room coordinates.
     * @param theLocation Pair with the coordinates.
     * @return ArrayList of Choice Pairs
     */
    private ArrayList<ChoicePair> borderCoords(final Pair theLocation) {
        final int row = theLocation.row();
        final int col = theLocation.column();
        final ArrayList<ChoicePair> choices = new ArrayList<>();
        choices.add(new ChoicePair(new Pair(row - 1, col), Direction.NORTH));
        choices.add(new ChoicePair(new Pair(row + 1, col), Direction.SOUTH));
        choices.add(new ChoicePair(new Pair(row, col - 1), Direction.WEST));
        choices.add(new ChoicePair(new Pair(row, col + 1), Direction.EAST));
        // returns choices that are in-bounds. This could be further optimized.
        return (ArrayList<ChoicePair>) choices.stream().filter(
                c -> myAllCoords.contains(c.destination)).collect(
                        Collectors.toList());
    }

    /**
     * Consumes a vision potion to reveal adjacent rooms to the hero.
     */
    public void useVisionPot() {
        final var vpots = myHero.getVisionPots();

        if (vpots <= 0) {
            return;
        }
        for (var choice : borderCoords(myHeroLocation)) {
            getRoom(choice.destination).setVisible();
        }
        myHero.setVisionPots(vpots - 1);
    }

    /**
     * Creates the dungeon maze utilizing depth first search.
     */
    private void generateMaze() {
        final Set<Pair> visited = new HashSet<>(); // visited rooms
        Pair current = new Pair(0, 0); // starting position
        int row;
        int col;
        Direction lastDoor = null;
        final Stack<Pair> walkStack = new Stack<>(); // history of our path

        // start walking through the maze with a modified DFS
        while (visited.size() != myAllCoords.size()) {
            visited.add(current);
            row = current.row();
            col = current.column();

            if (lastDoor != null) {
                // the door we entered the room from
                myRooms[row][col].setDoor(lastDoor.invert());
            }

            // choose a next cell that hasn't been visited
            final List<ChoicePair> choices = borderCoords(
                    current).stream().filter(
                            c -> !visited.contains(c.destination)).toList();
            // if we have no choices from here,
            // we go back through the history stack
            if (choices.isEmpty() && !walkStack.isEmpty()) {
                current = walkStack.pop();
                lastDoor = null; // jumping into a room we've entered before.
                continue;
            }
            if (choices.isEmpty() /*&& walkStack.isEmpty()*/) {
                break; // no choices and no history. we must be finished.
            }
            walkStack.push(current);
            // random choice from the choices list
            final int choiceIndex = RANDOM.nextInt(choices.size());
            final ChoicePair nextChoice = choices.get(choiceIndex);
            final Direction nextDoor = nextChoice.door;
            final Pair destination = nextChoice.destination;
            myRooms[row][col].setDoor(nextDoor);
            lastDoor = nextDoor;
            current = destination;
        }
    }

    @Override
    public String toString() {
        return "Dungeon{" + "myRooms=" + Arrays.stream(myRooms).map(
                Arrays::toString).collect(
                        Collectors.joining("\n"))
                + ", rows=" + myRows
                + ", columns=" + myColumns
                + ", myHero=" + myHero
                + ", myHeroLocation=" + myHeroLocation
                + '}';
    }

    private static final class ItemRecord {
        /***
         * Percentage chance to see the item in any one dungeon space.
         */
        private final Double myDropChance;
        /***
         * The number of the item which is admissible is in the dungeon.
         */
        private Integer myMaxOccurrence;

        ItemRecord(final Double theChance, final Integer theMaxOccurrence) {
            myDropChance = theChance;
            myMaxOccurrence = theMaxOccurrence;
        }

        /***
         * decrements myMaxOccurrence for keeping track of how many more
         * occurrences of this item we can have.
         */
        public void decrement() {
            myMaxOccurrence--;
        }

        public int getMyMaxOccurrence() {
            return myMaxOccurrence;
        }

        public Double getMyDropChance() {
            return myDropChance;
        }
    }

    private record ChoicePair(Pair destination, Direction door) {
    }
}
