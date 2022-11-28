package tcss.dungeonadventure;

import java.util.Set;

public interface GameModel {
    /***
     * encapsulates all the functionality/interactions required to play the game.
     * @param theRows : the width of the dungeon generated
     * @param theColumns : the height of the dungeon generated
     */
    void newDungeon(int theRows, int theColumns);
    void saveGame();
    void loadGame();

    // data that the view needs access to display/play the game

    void setHero(Hero theHero); // needed so we can do a hero select screen
    Hero getHero();
    Pair getHeroLocation(); // hero's position in the dungeon maze.
    Item[] getRoomItems(Pair theRoomLocation); // view needs to see items to know how to display
    Set<Direction> getRoomDoors(Pair theRoomLocation); // view needs to see doors/openings to display, and for player options

    // player actions:
    boolean pickupItem(Item theItem); // pickup an item in the current room, return success/failure.
    boolean useItem(Item theItem); // use an item that's in the hero's inventory.
    boolean move(Direction theDirection); // move the hero in a given direction. returns success/failure.
    boolean checkCombat(); // returns true if we've run into a combat encounter in the current room?

    Room[][] getRooms();
}
