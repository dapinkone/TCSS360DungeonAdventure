package DungeonAdventure;

import java.util.ArrayList;

public interface GameModel {
    /***
     * encapsulates all the functionality/interactions required to play the game.
     */
    void saveGame();
    void loadGame();

    // data that the view needs access to display/play the game
    // String getCurrentRoom(); // Rooms are mutable. we should try to avoid passing stateful objects around.
    void setHero(Hero theHero); // needed so we can do a hero select screen
    Hero getHero();
    Pair getHeroLocation(); // hero's position in the dungeon maze.
    Item[] getRoomItems(Pair p); // view needs to see items to know how to display
    Direction[] getRoomDoors(Pair p); // view needs to see doors/openings to display, and for player options

    // player actions:
    boolean pickupItem(Item theItem); // pickup an item in the current room, return success/failure.
    boolean useItem(Item theItem); // use an item that's in the hero's inventory.
    boolean move(Direction theDirection); // move the hero in a given direction. returns success/failure.
    boolean checkCombat(); // returns true if we've run into a combat encounter in the current room?
}
