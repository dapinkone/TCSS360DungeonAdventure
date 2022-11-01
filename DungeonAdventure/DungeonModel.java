package DungeonAdventure;

public interface DungeonModel {
    void saveGame();
    void loadGame();
    String getCurrentRoom(); // TODO: shouldn't we avoid string returns?
    String showDungeon();
}
