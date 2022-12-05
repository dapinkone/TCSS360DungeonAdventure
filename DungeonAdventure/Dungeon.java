package DungeonAdventure;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Dungeon implements Serializable {
    public static final Random RANDOM = new Random();
    final private  int rows;
    final private int columns;
    final private HashSet<Pair> allCoords = new HashSet<>();
    private static final class ItemRecord{
        private final Double myChance;
        private Integer myMaxOccurrence;
        ItemRecord(Double theChance, Integer theMaxOccurrence) {
            myChance = theChance;
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

        public Double getMyChance() {
            return myChance;
        }
    }


    /***
     * Data structure that holds information about the dungeon, or the "board" on which we play the game
     */
    private Room[][] myRooms;
    private Hero myHero;
    private Pair myHeroLocation = new Pair(0,0);
    public Dungeon(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        makeRooms();
        generateMaze();
        spawnItems();
        spawnMonsters();
    }

    private void spawnMonsters() {
        MonsterFactory mf = MonsterFactory.getInstance();

        // for every room in the maze, maybe add a monster
        for(var coord : allCoords) {
            final var room = getRoom(coord);
            // don't want to start the game in combat.
            if(room.getMyItems().contains(Item.Entrance)) continue;

            if(RANDOM.nextDouble() > 0.8) { // 20% chance to see a monster at all
                final var chance = RANDOM.nextDouble();
                Monster newMonster;
                if(chance > 0.9) {// ver5y unlucky.
                    newMonster = mf.generateMonster("Awoken Horror");
                } else if (chance > 0.7) {
                    newMonster = mf.generateMonster("predator");
                } else if (chance > 0.4) {
                    newMonster =  mf.generateMonster("Crawler");
                } else {
                    newMonster = mf.generateMonster("Skitter");
                }
                System.out.println("adding " + newMonster.getMyName());
                getRoom(coord).addMonster(newMonster);
            }
        }
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Pair getMyHeroLocation() {
        return myHeroLocation;
    }

    public void setMyHeroLocation(Pair theHeroLocation) {
        // if coordinate is valid
        if(allCoords.contains(theHeroLocation)) {
            myHeroLocation = theHeroLocation;
            getRoom(myHeroLocation).setMyVisitedStatus();
        }
    }

    private void makeRooms() {
        // we need to generate the Rooms, coordinates, etc
        myRooms = new Room[rows][];
        for(int row = 0; row < rows; row++) {
            Room[] rowArr = new Room[columns];
            for(int col=0; col < columns; col++) {
                Pair roomLocation = new Pair(row, col);
                allCoords.add(roomLocation);
                Room newRoom = new Room(roomLocation);
                rowArr[col] = newRoom;
            }
            myRooms[row] = rowArr;
        }
    }
    public Room getRoom(Pair theLocation) {
        return myRooms[theLocation.getRow()][theLocation.getColumn()];
    }

    /***
     * spawnItems
     * populates the dungeon Rooms with required Item objects, according
     * to hard-coded required frequencies, with some hard upper limits.
     */
    private void spawnItems() {
        /***
         * myItemRates determines the chance of each item that should spawn in the
         * dungeon, as well as any upper bounds on the # that can spawn.
         */
        final Map<Item, ItemRecord> myItemRates = new HashMap<>();
        // is there a cleaner way to do this?
        // exit, entrance, as well as pillars 100% must spawn,
        // and must spawn exactly once.
        myItemRates.put(Item.Exit, new ItemRecord(1.0,1));
        myItemRates.put(Item.Entrance, new ItemRecord(1.0,1));
        myItemRates.put(Item.PillarAbstraction, new ItemRecord(1.0,1));
        myItemRates.put(Item.PillarInheritance, new ItemRecord(1.0,1));
        myItemRates.put(Item.PillarEncapsulation,new ItemRecord(1.0,1));
        myItemRates.put(Item.PillarPolymorphism, new ItemRecord(1.0,1));
        // Healing potions, vision potions and pits are a bit less common.
        // we'll go 20% chance, with no upper limit.
        myItemRates.put(Item.HealingPotion, new ItemRecord(0.2, Integer.MAX_VALUE));
        myItemRates.put(Item.VisionPotion, new ItemRecord(0.1, Integer.MAX_VALUE));
        myItemRates.put(Item.Pit, new ItemRecord(0.1, Integer.MAX_VALUE));
        //
        //
        final var shuffledCoords = new ArrayList<>(allCoords);
        Collections.shuffle(shuffledCoords);

        Room choice;
        ROOM: for(var coord : shuffledCoords) {
            choice = getRoom(coord);
            for(Item i : Item.values()) {
                final ItemRecord rec = myItemRates.get(i);
                if(rec == null) {
                    throw new NoSuchElementException("Item " + i + "unknown.");
                }
                if(rec.myMaxOccurrence == 1 && rec.myChance == 1) {
                    choice.addToMyItems(i);
                    rec.decrement();
                    // hero is placed at start of dungeon automagically:
                    if(i == Item.Entrance) {
                        setMyHeroLocation(choice.getMyLocation());
                    }
                    // these(entrance/exit/pillars) are only one to a room
                    continue ROOM;
                }
                if(rec.getMyMaxOccurrence() > 0 && RANDOM.nextDouble() <= rec.getMyChance()) {
                    choice.addToMyItems(i);
                    rec.decrement();
                }
            }
        }
        // ensure the required items have been placed.
        for(Item i : Item.values()) {
            final ItemRecord rec = myItemRates.get(i);
            if(rec.getMyChance() == 1 && rec.getMyMaxOccurrence() > 0) {
                throw new IllegalArgumentException(
                        "Maze size not large enough for items.");
            }
        }
    }

    public Room[][] getRooms() {
        return myRooms;
    }

    public Hero getHero() {
        return myHero;
    }

    public void setHero(final Hero theHero) {
        this.myHero = theHero;
    }

    public List<Item> getCurrentRoomItems() {
        return getRoom(myHeroLocation).getMyItems();
    }
    public Set<Direction> getCurrentRoomDoors() {
        return getRoomDoors(myHeroLocation);
    }

    public Set<Direction> getRoomDoors(Pair p) {
        return myRooms[p.getRow()][p.getColumn()].getDoors();
    }

    private ArrayList<ChoicePair> borderCoords(Pair p) {
        int row = p.getRow();
        int col = p.getColumn();
        ArrayList<ChoicePair> choices = new ArrayList<>();
        // TODO: refactor?
        choices.add(new ChoicePair(new Pair(row - 1, col), Direction.NORTH));
        choices.add(new ChoicePair(new Pair(row + 1, col), Direction.SOUTH));
        choices.add(new ChoicePair(new Pair(row, col - 1), Direction.WEST));
        choices.add(new ChoicePair(new Pair(row, col + 1), Direction.EAST));
        // returns choices that are in-bounds. This could be further optimized.
        return (ArrayList<ChoicePair>) choices.stream()
                .filter(c -> allCoords.contains(c.destination))
                .collect(Collectors.toList());
    }

    private void generateMaze() {
        HashSet<Pair> visited = new HashSet<>(); // visited rooms
        Pair current = new Pair(0,0); // starting position
        int row, col;
        Direction lastDoor = null;
        Stack<Pair> walkStack = new Stack<>(); // history of our path

        // start walking through the maze with a modified DFS
        while(visited.size() != allCoords.size()) {
            visited.add(current);
            row = current.getRow();
            col = current.getColumn();

            if(lastDoor != null) {
                // the door we entered the room from
                myRooms[row][col].setDoor(lastDoor.invert());
            }

            // choose a next cell that hasn't been visited
            List<ChoicePair> choices = (
                    borderCoords(current).stream().filter(c -> !visited.contains(c.destination)).toList());
            // if we have no choices from here, we go back through the history stack
            if(choices.isEmpty() && !walkStack.isEmpty()) {
                current = walkStack.pop();
                lastDoor = null; // jumping into a room we've entered before.
                continue;
            }
            if(choices.isEmpty() && walkStack.isEmpty()) {
                break; // no choices and no history. we must be finished.
            }
            walkStack.push(current);
            // random choice from the choices list
            int choiceIndex = RANDOM.nextInt(choices.size());
            ChoicePair nextChoice = choices.get(choiceIndex);
            Direction nextDoor = nextChoice.door;
            Pair destination = nextChoice.destination;
            myRooms[row][col].setDoor(nextDoor);
            lastDoor = nextDoor;
            current = destination;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Dungeon{");
        sb.append("myRooms=").append(Arrays.stream(myRooms)
                .map(Arrays::toString)
                .collect(Collectors.joining("\n")));
        sb.append(", rows=").append(rows);
        sb.append(", columns=").append(columns);
        sb.append(", myHero=").append(myHero);
        sb.append(", myHeroLocation=").append(myHeroLocation);
        sb.append('}');
        return sb.toString();
    }

    private record ChoicePair(Pair destination, Direction door) {
    }
}
