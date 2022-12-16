package DungeonAdventure;

// main program entrypoint
public class DungeonAdventure {
    /***
     * runs the CLIView for CaveRescue game.
     * @param theArgs  command line arguments
     */
    public static void main(final String[] theArgs) {
        // instantiate a model, feed to a view, and run the view.
        // instantiate a view
        new StartMenu();
//        new CLIView(new DefaultModel()).run();

    }

}
