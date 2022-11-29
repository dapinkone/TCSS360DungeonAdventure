package tcss.dungeonadventure;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
/***
 * @author Peter Iriarte, Brian Nguyen
 * @version 0.0.1
 * Dungeon is the Data structure that holds information about the dungeon,
 * or the "board" on which we play the game.
 */

public final class Dungeon implements Serializable {
    public static final Random RANDOM = new Random();
    private final int myRows;
    private final int myColumns;
    private final Set<Pair> myCoords = new HashSet<>();
    private Room[][] myRooms;
    private Hero myHero;
    private Pair myHeroLocation = new Pair(0, 0);

    public Dungeon(final int theRows, final int theColumns) {
        this.myRows = theRows;
        this.myColumns = theColumns;
        makeRooms();
        generateMaze();
    }

    public int getMyColumns() {
        return myColumns;
    }

    public int getMyRows() {
        return myRows;
    }

    public Pair getMyHeroLocation() {
        return myHeroLocation;
    }

    public void setMyHeroLocation(final Pair theHeroLocation) {
        // if coordinate is valid
        if (myCoords.contains(theHeroLocation)) {
            myHeroLocation = theHeroLocation;
        }
    }

    private void makeRooms() {
        // we need to generate the Rooms, coordinates, etc
        myRooms = new Room[myRows][];
        for (int row = 0; row < myRows; row++) {
            final Room[] rowArr = new Room[myColumns];
            for (int col = 0; col < myColumns; col++) {
                final Pair roomLocation = new Pair(row, col);
                myCoords.add(roomLocation);
                final Room newRoom = new Room(roomLocation);
                rowArr[col] = newRoom;
            }
            myRooms[row] = rowArr;
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

    public Item[] getCurrentRoomItems() {
        return null;
    }

    public Set<Direction> getCurrentRoomDoors() {
        return getRoomDoors(myHeroLocation);
    }

    public Set<Direction> getRoomDoors(final Pair theLocation) {
        return myRooms[theLocation.getRow()][theLocation.getColumn()].getDoors();
    }

    private ArrayList<ChoicePair> borderCoords(final Pair theLocation) {
        final int row = theLocation.getRow();
        final int col = theLocation.getColumn();
        final ArrayList<ChoicePair> choices = new ArrayList<>();

        choices.add(new ChoicePair(new Pair(row - 1, col), Direction.NORTH));
        choices.add(new ChoicePair(new Pair(row + 1, col), Direction.SOUTH));
        choices.add(new ChoicePair(new Pair(row, col - 1), Direction.WEST));
        choices.add(new ChoicePair(new Pair(row, col + 1), Direction.EAST));
        // returns choices that are in-bounds. This could be further optimized.
        return (ArrayList<ChoicePair>) choices.stream().filter(c ->
                myCoords.contains(c.destination)).collect(Collectors.toList());
    }

    private void generateMaze() {
        final Set<Pair> visited = new HashSet<>(); // visited rooms
        Pair current = new Pair(0, 0); // starting position
        int row;
        int col;
        Direction lastDoor = null;
        final Stack<Pair> walkStack = new Stack<>(); // history of our path

        // start walking through the maze with a modified DFS
        while (visited.size() != myCoords.size()) {
            visited.add(current);
            row = current.getRow();
            col = current.getColumn();

            if (lastDoor != null) {
                // the door we entered the room from
                myRooms[row][col].setDoor(lastDoor.invert());
            }

            // choose a next cell that hasn't been visited
            final List<ChoicePair> choices = borderCoords(current).stream().filter(c -> !visited.contains(c.destination)).toList();
            // if we have no choices from here, we go back through the history stack.
            if (choices.isEmpty() && !walkStack.isEmpty()) {
                current = walkStack.pop();
                lastDoor = null; // jumping into a room we've entered before.
                continue;
            }
            if (choices.isEmpty()) {
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
        return "Dungeon{" + "myRooms="
                + Arrays.stream(myRooms).map(Arrays::toString).collect(
                        Collectors.joining("\n"))
                + ", rows=" + myRows + ", columns=" + myColumns
                + ", myHero=" + myHero + ", myHeroLocation="
                + myHeroLocation + '}';
    }

    private record ChoicePair(Pair destination, Direction door) {
    }
}
