package DungeonAdventure;

public interface GameModel {
    void saveGame();
    void loadGame();
    String getCurrentRoom(); // TODO: shouldn't we avoid string returns?
    String showDungeon();
}
