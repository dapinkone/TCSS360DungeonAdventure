package DungeonAdventure;
// main program entrypoint
public class CaveRescue {
    private static GameModel myGameModel;
    private static GameView myGameView;
    public static void main(String[] args) {
        // instantiate a model
        myGameModel = new DefaultModel();
        // instantiate a view
        myGameView = new CLIView(myGameModel);
        myGameView.run();
    }

    private void saveGame() {
    }
    private void loadGame() {

    }

}
