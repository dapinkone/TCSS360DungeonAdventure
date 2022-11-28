package tcss.dungeonadventure;

// main program entrypoint
public class CaveRescue {

    public static void main(final String[] theArgs) {
        // instantiate a model
        final GameModel gameModel = new DefaultModel();
        // instantiate a view
        final GameView gameView = new CLIView(gameModel);
        gameView.run();
    }

    private void saveGame() {
    }

    private void loadGame() {

    }

}
