package DungeonAdventure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface GameModel {
    /***
     * Returns the instance of RecordQ the game model is using.
     * @return RecordQ
     */
    RecordQ getMyRecordQ();

    /***
     * Encapsulates all the functionality and
     * interactions required to play the game.
     * @param theRows The height of the maze/dungeon.
     * @param theCols The width of the maze/dungeon.
     */
    void newDungeon(int theRows, int theCols);

    /***
     * saveGame() saves game state(dungeon object) to the selected file.
     * @param theSelectedFile file object destination.
     * @throws IOException Some permissions/IO issue happened?
     */
    void saveGame(File theSelectedFile) throws IOException;

    /***
     * loadGame() loads a serialized Dungeon object from a given File object.
     * @param theSelectedFile file object source.
     * @throws IOException Throws if deserialization or read fails.
     */
    void loadGame(File theSelectedFile) throws IOException;

    /***
     * Builds a new game state.
     * @param theRows The height of the dungeon/maze.
     * @param theCols The width of the dungeon/maze.
     */
    void newGame(int theRows, int theCols);

    // data that the view needs access to display/play the game:

    /***
     * Uses a vision pot to show adjacent rooms.
     */
    void useVisionPot();

    /***
     * Our intrepid hero.
     * @return Hero
     */
    Hero getHero();

    /***
     * Sets the Hero in the model, so we can do a hero selection screen.
     * @param theHero Our intrepid hero.
     */
    void setHero(Hero theHero); // needed so we can do a hero select screen

    /***
     * The hero's current location in the dungeon.
     * @return Pair location.
     */
    Pair getHeroLocation(); // hero's position in the dungeon maze.

    /***
     * Returns the items in a given room location so the view can render.
     * @param theLocation Pair of row, column
     * @return List of items found at theLocation.
     */
    List<Item> getRoomItems(Pair theLocation);

    /***
     * View needs to see doors/openings to display, and for player navitation.
     * @param theLocation The location to be queried.
     * @return Set of Direction objects which have open/passable doors.
     */
    Set<Direction> getRoomDoors(Pair theLocation);

    /***
     * Picks up theItem in the current room.
     * @param theItem Desired item.
     */
    // player actions:
    void pickupItem(Item theItem);

    /***
     * Use a given inventory item, and return success/failure.
     * @param theItem Item to be used.
     * @return success or failure of attempt.
     */
    boolean useItem(Item theItem);

    /***
     * Move the hero in a given Direction within the dungeon. May fail if
     * hero is in combat, or desired Direction is not free of obstruction.
     * @param theDirection direction of movement.
     * @return success or failure of movement.
     */
    boolean move(Direction theDirection);

    /***
     * Is the hero currently in combat?
     * @return true for yes. false for no.
     */
    boolean checkCombat();

    /***
     * Combat object encapsulates combat/turn ordering.
     * @return Combat object.
     */
    Combat getMyCombat();

    /***
     * Used to get more detail about specific rooms in the dungeon structure.
     * @return Room[][] dungeon rooms.
     */
    Room[][] getRooms();

    /***
     * Gets a specific room by location.
     * @param theCoord location pair.
     * @return Room object from the dungeon.
     */
    Room getRoom(Pair theCoord);


    //void spawnBossFight();

    /***
     * Checks if the game is over(hero is dead, or victorious)
     * @return true/false for game condition.
     */
    boolean gameOver();

    /***
     * Checks victory condition.
     * @return true if Hero is not dead, has all pillars, and stands alone,
     * outside combat, at the exit.
     */
    boolean victoryCondition();

    /***
     * Used to get the next record from the RecordQ for rendering code.
     * @return HealthChangeRecord
     */
    HealthChangeRecord nextGameRecord();

    /***
     * Records and returns the items which have been auto-picked-up upon the
     * hero's entry into the room.
     * @return List of Items
     */
    ArrayList<Item> checkNewItems();
}
