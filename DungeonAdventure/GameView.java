package DungeonAdventure;

public interface GameView {

    // start / title / intro page
    void showIntro();

    // character select
    void showHeroSelect();

    // help page
    void showHelp();

    // main dungeon display page
    void showDungeon();

    // hero inventory page
    void showHeroInventory();

    // combat page
    void showCombat();

    // game over
    void showGameOver();

    void showVictory();

    void run();
}
