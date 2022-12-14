package DungeonAdventure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface GameModel {
    RecordQ getMyRecordQ();

    /***
     * encapsulates all the functionality/interactions required to play the game.
     */
    void newDungeon(int rows, int cols);
    void saveGame(File selectedFile) throws IOException;
    void loadGame(File selectedFile) throws IOException;

    // data that the view needs access to display/play the game

    void newGame(int theRows, int theCols);
    void useVisionPot();
    void setHero(Hero theHero); // needed so we can do a hero select screen
    Hero getHero();
    Pair getHeroLocation(); // hero's position in the dungeon maze.
    List<Item> getRoomItems(Pair p); // view needs to see items to know how to display
    Set<Direction> getRoomDoors(Pair p); // view needs to see doors/openings to display, and for player options

    // player actions:
    boolean pickupItem(Item theItem); // pickup an item in the current room, return success/failure.
    boolean useItem(Item theItem); // use an item that's in the hero's inventory.
    boolean move(Direction theDirection); // move the hero in a given direction. returns success/failure.
    boolean checkCombat(); //returns true if we've run into a combat encounter in the current room?

    Combat getMyCombat();

    /***
     * returns reference to monsters in the current combat encounter.
     * @return list of monsters
     */
    //List<Monster> combatStats();

    Room[][] getRooms();

    Room getRoom(Pair theCoord);

    void spawnBossFight();
    boolean gameover();

    HealthChangeRecord nextGameRecord();
    ArrayList<Item> checkNewItems();
}
