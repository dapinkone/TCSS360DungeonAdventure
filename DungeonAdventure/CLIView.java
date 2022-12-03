package DungeonAdventure;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CLIView implements GameView {
    final GameModel myModel;
    final static Scanner scanner = new Scanner(System.in);
    public CLIView(GameModel theModel) {
        myModel = theModel;
    }

    public void run() {
        // mainloop of game?
        showIntro();
        // menu : [newgame] [loadgame]
        showSizeSelect(); // how large is the dungeon?
        // difficulty? (might adjust spawn rates?)
        showHeroSelect();
        while(!myModel.gameover()) {
            showDungeon();
            myModel.move(movementMenu());
        }
    }

    private Direction movementMenu() {
        final var options = new ArrayList<>(myModel.getRoomDoors(myModel.getHeroLocation()));
        return choiceMenu(options, "What direction would you like to go?");
    }

    private void showSizeSelect() {
        System.out.println("Please enter two integers for the desired game size:");
        int rows = 0, columns = 0;
        while(rows <= 0 || columns <= 0) {
            try {
                rows = scanner.nextInt();
                columns = scanner.nextInt();
            } catch (Exception e) { // TODO: be more specific.
                System.out.println("Invalid size.");
            }
        }
        myModel.newDungeon(rows, columns);
    }

    public void showDungeon() {
        final StringBuilder sb = new StringBuilder();
        // TODO: passing Rooms(mutable) is poor form. further refactor?

        for (Room[] row: myModel.getRooms()) {
            for(int scanline = 0; scanline < 3; scanline++) {
                for (Room room : row) {
                    String s = roomAsASCII(room).split("\n")[scanline];
                    sb.append( s );
                }
                sb.append('\n');
            }
        }
        System.out.println(sb);
    }

    @Override
    public void showHeroInventory() {
        final var hero = myModel.getHero();
        System.out.println("Our hero has these items:"); // this should be generalized. pots aren't special.
        System.out.println("Healing pots" + hero.getHealingPots());
        System.out.println("Healing pots" + hero.getVisionPots());
        System.out.println("Pillars: " + hero.getPillars());
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

    private String roomAsASCII(Room theRoom) {
        final StringBuilder sb = new StringBuilder();
        // top line
        if(theRoom.getDoor(Direction.NORTH)) {
            sb.append("- -\n");
        } else {
            sb.append("---\n");
        }
        // middle line
        sb.append(theRoom.getDoor(Direction.WEST) ? ' ' : '|');

        // TODO: should we be able to see monsters here?
        // append specific item representations / hero to center of room
        if(myModel.getHeroLocation().compareTo(theRoom.getMyLocation()) == 0) {
            sb.append('%');
        } else if(theRoom.getMyItems().size() > 1) {
            sb.append("M");
        } else if(theRoom.getMyItems().size() == 1) {
            var theItem = theRoom.getMyItems().get(0);
            sb.append(switch (theItem) {
                case PillarAbstraction -> "A";
                case PillarEncapsulation -> "E";
                case PillarInheritance -> "I";
                case PillarPolymorphism -> "P";
                case Pit -> "X";
                case Entrance -> "i";
                case Exit -> "O";
                case HealingPotion -> "H";
                case VisionPotion -> "V";
            });
        } else {
            sb.append('.'); // "empty"
        }
        sb.append(theRoom.getDoor(Direction.EAST) ? " \n" : "|\n");

        // bottom line
        if(theRoom.getDoor(Direction.SOUTH)) {
            sb.append("- -\n");
        } else {
            sb.append("---\n");
        }
        return sb.toString();
    }

    @Override
    public void showIntro() {

    }

    @Override
    public void showHeroSelect() { // TODO: should we generalize menus?
        var options = new String[]{"Survivalist", "Bruiser", "Scout"}; // TODO: available from model?
        System.out.println("Select a hero(1-" + options.length +"): ");

        for(int i=0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        // take selection
        int selection = 0;
        if(scanner.hasNextInt()) {
            selection = scanner.nextInt();
        }
        if(selection <= 0 || selection > options.length){ // TODO: fix edge cases such as EOF. test cases?
            System.out.println("Invalid selection detected. Please enter an integer from 1 to " + options.length + ".");
            showHeroSelect();
            return;
        }
        // TODO: use hero factory?
        myModel.setHero(
                switch(selection) {
                    case 1 -> new Priestess(options[selection-1]); // Survivalist
                    case 2 -> new Warrior(options[selection-1]); // Bruiser
                    case 3 -> new Thief(options[selection-1]); // Scout
                    default -> throw new IllegalStateException("Unexpected value: " + selection);
                });
        System.out.println("Hero " + options[selection-1] + " selected.");
    }
    private <T> T choiceMenu(final List<T> options, final String description) throws NoSuchElementException {
        if(options == null || options.isEmpty()) {
            throw new NoSuchElementException();
        }
        int choiceIndex;
        do {
            System.out.println(description + "(1-" + options.size() + ")");
            int i = 1;
            for(T option : options) {
                System.out.println(i++ + ". " + option.toString());
            }
            choiceIndex = scanner.nextInt();
        } while(choiceIndex < 1 || choiceIndex > options.size());
        return options.get(choiceIndex-1);
    }

    @Override
    public void showHelp() {
        // explain gameplay
    }


}
