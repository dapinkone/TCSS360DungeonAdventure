package tcss.dungeonadventure;

import java.util.NoSuchElementException;
import java.util.Scanner;

public final class CLIView implements GameView {
    /**
     * Scanner for taking user inputs.
     */
    static final Scanner SCANNER = new Scanner(System.in);
    private final GameModel myModel;
    public CLIView(final GameModel theModel) {
        myModel = theModel;
    }
    public void run() {
        // mainloop of game?
        showIntro();
        // menu : [newgame] [loadgame]
        showSizeSelect(); // how large is the dungeon?
        // difficulty? (might adjust spawn rates?)
        showHeroSelect();
        showDungeon();

    }

    private void showSizeSelect() {
        System.out.println("Please enter two integers for the game size:");
        int rows = 0;
        int columns = 0;
        while (rows <= 0 || columns <= 0) {
            try {
                rows = SCANNER.nextInt();
                columns = SCANNER.nextInt();
            } catch (final NoSuchElementException e) {
                System.out.println("Invalid size selection.");
            }
        }
        myModel.newDungeon(rows, columns);
    }

    public void showDungeon() {
        final StringBuilder sb = new StringBuilder();

        final int maxScanLines = 3;
        for (Room[] row : myModel.getRooms()) {
            for (int scanline = 0; scanline < maxScanLines; scanline++) {
                for (Room room : row) {
                    final String s = roomAsASCII(room).split("\n")[scanline];
                    sb.append(s);
                }
                sb.append('\n');
            }
        }
        System.out.println(sb);
    }

    @Override
    public void showHeroInventory() {
        // TODO: need an inventory listing from hero/model
    }

    @Override
    public void showCombat() {
        // TODO: need a way to get current combat status from model
        // eg: monsters involved, their health, actions, etc.
    }

    @Override
    public void showGameOver() {
        System.out.println("\n\t\tYou have died. Thanks for playing!\n");
    }

    @Override
    public void showVictory() {
        System.out.println("\n\t\tVictory!\n");
    }

    private String asciiWallNorthSouth(final boolean theDoor) {
        if (theDoor) {
            return "- -\n";
        }
        return "---\n";
    }

    private String asciiWallEastWest(final boolean theDoor) {
        if (theDoor) {
            return " ";
        }
        return "|";
    }

    private String roomAsASCII(final Room theRoom) {
        final StringBuilder sb = new StringBuilder();
        // top line
        sb.append(asciiWallNorthSouth(theRoom.getDoor(Direction.NORTH)));

        // middle line
        sb.append(asciiWallEastWest(theRoom.getDoor(Direction.WEST)));

        // append specific item representations / hero to center of room.
        if (myModel.getHeroLocation().compareTo(theRoom.getMyLocation()) == 0) {
            sb.append('%');
        } else if (theRoom.getMyItems().size() > 1) {
            sb.append("M");
        } else if (theRoom.getMyItems().size() == 1) {
            final var item = theRoom.getMyItems().get(0);
            sb.append(item.asAscii());
        } else {
            sb.append('.'); // "empty"
        }
        sb.append(asciiWallEastWest(theRoom.getDoor(Direction.EAST)));
        sb.append("\n");

        // bottom line
        sb.append(asciiWallNorthSouth(theRoom.getDoor(Direction.SOUTH)));

        return sb.toString();
    }

    @Override
    public void showIntro() {

    }

    @Override
    public void showHeroSelect() {
        final var options = new String[]{"Survivalist", "Bruiser", "Scout"};
        System.out.println("Select a hero(1-" + options.length + "): ");

        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        // take selection
        int selection = 0;

        if (SCANNER.hasNextInt()) {
            selection = SCANNER.nextInt();
        }
        if (selection <= 0 || selection > options.length) {
            System.out.println("Invalid selection detected. Please enter an integer from 1 to " + options.length);
            showHeroSelect();
            return;
        }
        myModel.setHero(
            switch (selection) {
                case 1 -> new Priestess(options[selection - 1]); // Survivalist
                case 2 -> new Warrior(options[selection - 1]); // Bruiser
                case 3 -> new Thief(options[selection - 1]); // Scout
                default -> throw new IllegalStateException("Unexpected value: " + selection);
            });
        System.out.println("Hero " + options[selection - 1] + " selected.");
    }

    @Override
    public void showHelp() {
        // explain gameplay
    }


}
