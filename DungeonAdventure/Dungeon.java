package DungeonAdventure;

import java.util.*;
import java.util.stream.Collectors;

public class Dungeon {
    private Room[][] myRooms;
    final private  int rows;
    final private int columns;
    private static final Random RANDOM = new Random();
    final private HashSet<Pair> allCoords = new HashSet<>();
    private Hero myHero;

    public Pair getMyHeroLocation() {
        return myHeroLocation;
    }

    public void setMyHeroLocation(Pair myHeroLocation) {
        // TODO: data validation
        this.myHeroLocation = myHeroLocation;
    }

    private Pair myHeroLocation;

    public Dungeon(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        makeRooms();
        generateMaze();
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

    public Room[][] getRooms() {
        return myRooms;
    }
    public void setHero(final Hero theHero) {
        this.myHero = theHero;
    }
    public Hero getHero() {
        return myHero;
    }
    public Item[] getCurrentRoomItems() {
        return null;
    }
    public String getCurrentRoomDoors() {
        return null;
    }

    private record ChoicePair(Pair destination, Direction door) {
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
}
