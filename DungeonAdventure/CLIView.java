package DungeonAdventure;

import java.io.IOException;
import java.util.*;

public final class CLIView implements GameView {
    static final Scanner SCANNER = new Scanner(System.in);
    final GameModel myModel;

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
        while (!myModel.gameover()) {
            showDungeon();
            myModel.move(movementMenu());
            // check for combat
            if (myModel.checkCombat()) { // combat mode
                final var combat = myModel.getMyCombat();
                final var monsters = combat.getMonsters();
                System.out.println("*Combat initiated!*");
                System.out.println("You have encountered:");

                final var recordQ = myModel.getMyRecordQ();
                while (myModel.checkCombat()) {
                    // process all records
                    HealthChangeRecord record;
                    while ((record = recordQ.poll()) != null) {
                        handleRecord(record);
                    }
                    for (var monster : monsters) {
                        System.out.println(monster.getStats() + "\n");
                    }
                    System.out.println("Your stats:\n" + myModel.getHero() + "\n");
                    // show fight options (attack / special skill / use health pot)
                    final var fightOptions = List.of("Attack", "Special skill", "Use Health Tonic");
                    final var choice = choiceMenu(fightOptions,
                            "How do you fight?");
                    combat.heroTurn(fightOptions.indexOf(choice) + 1,
                            0); // TODO: selection of multiple targets?
                }
                HealthChangeRecord record;
                while ((record = recordQ.poll()) != null) {
                    handleRecord(record);
                }
                System.out.println("You won the fight!");
            }
            // check for new items
            final var newItems = myModel.checkNewItems();
            if (!newItems.isEmpty()) {
                System.out.println("New items found:" + newItems);
            }
            final var roomItems = myModel.getRoomItems(myModel.getHeroLocation());

            if (!roomItems.isEmpty()) {
                System.out.println("You see something in the room....:");
                for (Item item : roomItems) {
                    System.out.println("<" + item.name() + ">");
                }
            }

        }
    }

    private void handleRecord(final HealthChangeRecord theRecord) {
        final var src = theRecord.source().getMyName();
        final var tgt = theRecord.target().getMyName();
        final var amt = theRecord.amount();
        final var type = theRecord.actionResultType();
        System.out.println(
            switch (type) {
                case Heal -> "*" + src
                        + " healed themselves for "
                        + amt + " health!*";
                case Hit -> "*"
                        + src + " hit " + tgt + " for " + amt + " damage!*";
                case CrushingBlow -> "*" + src + " dealt " + tgt
                        + " a crushing blow for " + amt + " damage!*";
                case CriticalHit ->
                        "*" + src + " got a critical hit! " + amt
                                + " damage to " + tgt + "*";
                case Miss -> "*" + src + " swings to hit " + tgt
                        + " but fumbles and misses.*";
            });
    }

    private Direction movementMenu() {
        final var options = new ArrayList<>(myModel.getRoomDoors(myModel.getHeroLocation()));
        return choiceMenu(options, "What direction would you like to go?");
    }

    private void showSizeSelect() {
        System.out.println("Please enter two integers for the desired game size:");
        int rows = 0, columns = 0;
        while (rows <= 0 || columns <= 0) {
            try {
                rows = SCANNER.nextInt();
                columns = SCANNER.nextInt();
            } catch (final InputMismatchException e) { // TODO: be more specific.
                System.out.println("Invalid size.");
            }
        }
        myModel.newDungeon(rows, columns);
    }

    public void showDungeon() {
        final StringBuilder sb = new StringBuilder();
        // TODO: passing Rooms(mutable) is poor form. further refactor?

        for (Room[] row : myModel.getRooms()) {
            for (int scanline = 0; scanline < 3; scanline++) {
                for (Room room : row) {
                    String s = roomAsASCII(room).split("\n")[scanline];
                    sb.append(s);
                }
                sb.append('\n');
            }
        }
        System.out.println(sb);
    }

    @Override
    public void showHeroInventory() {
        final var hero = myModel.getHero();
        System.out.println("Our hero has these items:");
        final var inv = hero.getMyInventory();
        for (Item item : inv.keySet()) {
            System.out.println(item + " " + inv.get(item));
        }
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

    private String roomAsASCII(final Room theRoom) {
        final StringBuilder sb = new StringBuilder();
        // top line
        if (theRoom.getDoor(Direction.NORTH)) {
            sb.append("- -\n");
        } else {
            sb.append("---\n");
        }
        // middle line
        sb.append(theRoom.getDoor(Direction.WEST) ? ' ' : '|');

        // TODO: should we be able to see monsters here?
        // append specific item representations / hero to center of room
        if (myModel.getHeroLocation().compareTo(theRoom.getMyLocation()) == 0) {
            sb.append('%'); // hero representation
        } else if (!theRoom.getMyMonsters().isEmpty()) {
            sb.append("*"); // monsters in CLIView are displayed as *
        } else if (theRoom.getMyItems().size() > 1) {
            sb.append("M"); // stack of multiple items is displayed as M
        } else if (theRoom.getMyItems().size() == 1) {
            final var item = theRoom.getMyItems().get(0);
            sb.append(
                switch (item) {
                    case PillarAbstraction -> "A";
                    case PillarEncapsulation -> "E";
                    case PillarInheritance -> "I";
                    case PillarPolymorphism -> "P";
                    case Pit -> "X";
                    case Entrance -> "i";
                    case Exit -> "O";
                    case HealingPotion -> "H";
                    case VisionPotion -> "V";
                }
            );
        } else {
            sb.append('.'); // "empty"
        }
        sb.append(theRoom.getDoor(Direction.EAST) ? " \n" : "|\n");

        // bottom line
        if (theRoom.getDoor(Direction.SOUTH)) {
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
        final var options = new String[]{"Survivalist", "Bruiser", "Scout"}; // TODO: available from model?
        System.out.println("Select a hero(1-" + options.length + "): ");

        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        // take selection
        int selection = 0;
        if (SCANNER.hasNextInt()) {
            selection = SCANNER.nextInt();
        }
        if (selection <= 0 || selection > options.length) { // TODO: fix edge cases such as EOF. test cases?
            System.out.println("Invalid selection detected. Please enter an integer from 1 to " + options.length + ".");
            showHeroSelect();
            return;
        }
        // TODO: use hero factory?
        myModel.setHero(
                switch (selection) {
                    case 1 ->
                            new Priestess(options[selection - 1]); // Survivalist
                    case 2 -> new Warrior(options[selection - 1]); // Bruiser
                    case 3 -> new Thief(options[selection - 1]); // Scout
                    default ->
                            throw new IllegalStateException("Unexpected value: " + selection);
                });
        System.out.println("Hero " + options[selection - 1] + " selected.");
    }

    private <T> T choiceMenu(final List<T> options, final String description) throws NoSuchElementException {
        if (options == null || options.isEmpty()) {
            throw new NoSuchElementException();
        }
        int choiceIndex;
        do {
            System.out.println(description + "(1-" + options.size() + ")");
            int i = 1;
            for (T option : options) {
                System.out.println(i++ + ". " + option.toString());
            }
            choiceIndex = SCANNER.nextInt();
            if (choiceIndex == 31337) { // cheat code
                final var hero = myModel.getHero();
                hero.setHealingPots(31337);
                hero.setMyHealth(10000);
            }
        } while (choiceIndex < 1 || choiceIndex > options.size());
        return options.get(choiceIndex - 1);
    }

    @Override
    public void showHelp() {
        // explain gameplay
    }


}
