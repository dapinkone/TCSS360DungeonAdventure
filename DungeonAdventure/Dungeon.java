package DungeonAdventure;

import java.util.*;

public class Dungeon {
    private Room[][] myRooms;
    private int rows;
    private int columns;

    private static Random RANDOM = new Random();
    public Dungeon(int rows, int columns, Hero theHero) {
        this.rows = rows;
        this.columns = columns;
        this.myHero = theHero;
    }

    public Hero getMyHero() {
        return myHero;
    }

    public void setMyHero(Hero theHero) {
        this.myHero = theHero;
    }

    private Hero myHero;
    public void setMyHeroLocation(int[] theHeroLocation) {
        // TODO: data validation
        this.myHeroLocation = theHeroLocation;
    }

    private int[] myHeroLocation; // TODO: potentially a Pair() or Coordinates() class here?
    private class Edge {

        public final Pair coordA;
        public final Pair coordB;

        public Edge(Pair coordN, Pair coordM) {
            if(coordN.compareTo(coordM) >= 0) {
                this.coordA = coordN;
                this.coordB = coordM;
            } else {
                this.coordA = coordM;
                this.coordB = coordN;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Edge edge = (Edge) o;
            return coordA.equals(edge.coordA) && coordB.equals(edge.coordB);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coordA, coordB);
        }
    }
    private ArrayList<Edge> getEdges(Pair p) {
        /***
         * returns a List of edges which should exist bordering the given pair p
         */
        // TODO: is this the best place for this? shouldn't pair validation be in Pair?
        ArrayList<Edge> lst = new ArrayList<>();
        if(p.row > 0) {
            lst.add(new Edge(p, new Pair(p.row-1, p.column))); // up
        }
        if(p.column > 0) {
            lst.add(new Edge(p, new Pair(p.row, p.column-1))); // left
        }
        if(p.row < rows - 2) {
            lst.add(new Edge(p, new Pair(p.row+1, p.column))); // down
        }
        if(p.column < columns - 2) {
            lst.add(new Edge(p, new Pair(p.row, p.column+1))); // right
        }
        return lst;
    }
    private void generateMaze() {
        /***
         * populates myRooms[][] using a modified prim's
         * TODO: comments/docs desperately needed.
         */
        HashSet<Pair> visitedCoords = new HashSet<>();
        visitedCoords.add(new Pair(0,0));
        // wallListing is a sort of stack for our edges. 0,0 is an arbitrary entrypoint to the graph.
        ArrayList<Edge> wallListing = new ArrayList<>(getEdges(new Pair(0, 0)));
        HashSet<Edge> passageList = new HashSet(); // listing of open doors between rooms

        while(!wallListing.isEmpty()) {
            // pick a random wall/edge from the stack.
            int pickIndex = RANDOM.nextInt(wallListing.size()-1);
            Edge wall = wallListing.get(pickIndex);
            wallListing.remove(pickIndex);
            boolean visitedA = visitedCoords.contains(wall.coordA);
            boolean visitedB = visitedCoords.contains(wall.coordB);
            if(visitedA ^ visitedB) {
                passageList.add(wall);

                if (visitedA) { // TODO: refactor? not sure how to do this cleaner without ternary.
                    for (Edge e : getEdges(wall.coordB)) {
                        if (!passageList.contains(e)) {
                            wallListing.add(e);
                        }
                    }
                } else {
                    for (Edge e : getEdges(wall.coordA)) {
                        if (!passageList.contains(e)) {
                            wallListing.add(e);
                        }
                    }
                }
            }
        }
        // now we should have a list of edges in passageList that traverses the entire maze.
        // ensuring each cell/room is reachable.
        // next we need to generate the Rooms, doors, etc.

    }

    public int[] getMyHeroLocation() {
        return myHeroLocation;
    }
    public Item[] getCurrentRoomItems() {
        return null;
    }
    public String getCurrentRoomDoors() {
        return null;
    }
}
