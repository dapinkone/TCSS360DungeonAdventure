package dungeon.adventure;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GUIView extends JFrame { //implements GameView {
    private static final JFileChooser FILE_CHOOSER = new JFileChooser("./");
    private static GUIView instance = null;
    private final GameModel myModel;
    private final TextLog myTextLog;
    private final POV myPov;
    private final OptionLog myOptionLog;
    private final GuiMap myGuiMap;
    private final Map<String, JButton> myButtons;
    private boolean myGameOverFlag = false;
    private GUIView(final GameModel theModel) {
        myButtons = new HashMap<>();
        myModel = theModel;
        myPov = new POV();
        myGuiMap = new GuiMap(theModel.getRooms().length,
                theModel.getRooms()[0].length);
        myTextLog = new TextLog();
        myOptionLog = new OptionLog();
        setTitle("Cave Rescue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(myPov);
        add(myGuiMap);
        add(myTextLog);
        add(myOptionLog);
        setLayout(new GridLayout(2, 2));
        setResizable(false);
        setVisible(true);
        pack();
    }

    public static GUIView getInstance(final GameModel theModel) {
        if (instance == null) {
            instance = new GUIView(theModel);
        }
        return instance;
    }

    public static void main(final String[] theArgs) {
        final DefaultModel model = new DefaultModel();
        model.newDungeon(4, 4);
        model.setHero(new Warrior("Karl"));
        GUIView.getInstance(model);
    }

    /**
     * Iterates through the model's record queue to read out the system messages necessary. Also dictates combat
     * victory conditions for the view and the necessary messages for that.
     */
    private void checkRecords() {
        final var recordQ = RecordQ.getInstance();
        HealthChangeRecord record;
        while ((record = recordQ.poll()) != null) {
            final var src = record.source().getMyName();
            final var tgt = record.target().getMyName();
            final var amt = record.amount();
            final var type = record.actionResultType();
            appendTextLog(
//                    "*" +
                            switch (type) {
                        case Heal -> src + " healed themselves for "
                                + amt + " health!";
                        case Hit ->
                                src + " hit " + tgt + " for " + amt + " damage!";
                        case CrushingBlow -> src + " dealt " + tgt
                                + " a crushing blow for " + amt + " damage!";
                        case CriticalHit ->
                                src + " got a critical hit! " + amt +
                                        " damage to " + tgt;
                        case Miss -> src + " attacks " + tgt
                                + " but misses!";
                    }
//                    + "*"
            );
            if (record.target() == myModel.getHero()) {
                update();
            }
        }
        if (!myModel.checkCombat()) { // won the fight!
            checkGameOver();
            myOptionLog.exitCombat();
        }
        update();
    }

    /**
     * Checks if the player has reached a game over state (victory or death) and provides the appropriate messages,
     * disables the proper buttons and reveals the map to the user.
     */
    private void checkGameOver() {
        if (myModel.getHero().isDead() && ! myGameOverFlag) {
            myGameOverFlag = true;
            appendTextLog("You have joined your ancestors below...");
            appendTextLog("Load a game, or restart to try again.");
            for(var buttonKey : myButtons.keySet()) {
                myButtons.get(buttonKey).setEnabled(false);
            }
            myButtons.get("LOAD GAME").setEnabled(true);
        } else if (myModel.victoryCondition() && !myGameOverFlag) {
            myGameOverFlag = true;
            appendTextLog("You successfully powered up the teleporter!");
            appendTextLog("Everyone escapes with their lives.");
            appendTextLog("\"Rock and Stone, " + myModel.getHero().getMyName() + "!\"");
            appendTextLog("Load a game, or restart to try again.");
            for(var buttonKey : myButtons.keySet()) {
                myButtons.get(buttonKey).setEnabled(false);
            }
            myButtons.get("LOAD GAME").setEnabled(true);
        }
        if (myGameOverFlag) {
            for (Room[] RoomRows : myModel.getRooms()) {
                for (Room room : RoomRows) {
                    room.setVisible();
                }
            }
        }
    }

    /**
     * Adds text to the game's text log.
     * @param theText The text to be added.
     */
    public void appendTextLog(final String theText) {
        //System.out.println(theText);
        myTextLog.TEXT_AREA.append(" >" + theText + "\n");
        myTextLog.TEXT_AREA.setCaretPosition(
                myTextLog.TEXT_AREA.getDocument().getLength());
    }

    /**
     * Updates the game's Graphics such as the map, character view, and HUD.
     */
    public void update() {
        myTextLog.updateHUD();
        // set invalid directions to inactive, and valid to active.
        validateDirections();
        myButtons.get("USE HEALING").setEnabled(
                myModel.getHero().getHealingPots() >= 1);
        myButtons.get("USE VISION").setEnabled(
                myModel.getHero().getVisionPots() >= 1);

        myGuiMap.repaint();
        myPov.repaint();
    }

    /***
     * disables/enables buttons so that only valid choices are enabled for
     * navigation
     */
    private void validateDirections() {
        final var validDirections = myModel.getRoomDoors(myModel.getHeroLocation());
        for (Direction d : Direction.values()) {
            myButtons.get(d.name()).setEnabled(
                    validDirections.contains(d)
            );
        }
    }

    /**
     * disables/enables buttons so that only valid choices are enabled for targeting.
     */
    private void validateTargets() {
        final var monsters = myModel.getMyCombat().getMonsters();
        for(int i=0; i < 3; i++) {
            final var button = myButtons.get(Integer.toString(i+1));
            if(monsters.size() > i) {
                button.setEnabled(
                        !monsters.get(i).isDead() && !myModel.getHero().isDead());
            } else {
                myButtons.get(Integer.toString(i+1)).setEnabled(false);
            }
        }
    }
    private final class POV extends JPanel {
        private static final int WIDTH = 700;
        private static final int HEIGHT = 400;
        private static final Point POSITION = new Point(300, 160);
        private static final Point OFFSET = new Point(120, 20);
        private Image myBackground;

        private POV() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40, 40, 40));
            setBorder(new LineBorder(Color.GREEN, 1));
        }

        /**
         * Takes all the monsters in the current room and draws them onto the character view.
         * @param theGraphics The graphics element.
         */
        private void drawMonsters(final Graphics theGraphics) {
            Image skitter = null;
            Image crawler = null;
            Image predator = null;
            final Room heroRoom = myModel.getRoom(myModel.getHeroLocation());

            try {
                skitter = ImageIO.read(new File("sprites/Skitter.png"));
                crawler = ImageIO.read(new File("sprites/Crawler.png"));
                predator = ImageIO.read(new File("sprites/predator.png"));
            } catch (final IOException e) {
                System.out.println("Missing Sprites");
            }
            int count = 0;
            final var spriteLookup = Map.of(
                    "Skitter", skitter,
                    "Crawler", crawler,
                    "Predator", predator);
            for (Monster monster : heroRoom.getMyMonsters()) {
                if (!monster.isDead()) {
                    theGraphics.drawImage(spriteLookup.get(monster.getMyName()),
                            POSITION.x + OFFSET.x * count,
                            POSITION.y + OFFSET.y * count,
                            this);
                }
                count++;
            }
        }

        /**
         * Draws all the items in the current room for the player to see.
         * @param theGraphics The graphics element.
         */
        private void drawItems(final Graphics theGraphics) {
            Image vpot = null;
            Image hpot = null;
            Image pillar = null;
            Image exit = null;
            Room heroRoom = myModel.getRooms()
                    [myModel.getHeroLocation().row()]
                    [myModel.getHeroLocation().column()];
            try {
                hpot = ImageIO.read(new File("sprites/healpot.png"));
                vpot = ImageIO.read(new File("sprites/visionpot.png"));
                pillar = ImageIO.read(new File("sprites/pillar.png"));
                exit = ImageIO.read(new File("sprites/exit.png"));

            } catch (final IOException e) {
                System.out.println("Missing Sprites");
            }
            int count = 0;
            for (Item item : heroRoom.getMyItems()) {
                if (item == Item.Exit) {
                    theGraphics.drawImage(exit,
                            POSITION.x + OFFSET.x * count,
                            POSITION.y + OFFSET.y * count,
                            this);
                } else if (item.name().contains("Pillar")) {
                    theGraphics.drawImage(pillar,
                            POSITION.x + OFFSET.x * count,
                            POSITION.y + OFFSET.y * count,
                            this);
                } else if (item == (Item.HealingPotion)) {
                    theGraphics.drawImage(hpot,
                            POSITION.x + OFFSET.x * count,
                            POSITION.y + OFFSET.y * count,
                            this);
                } else if (item == (Item.VisionPotion)) {
                    theGraphics.drawImage(vpot,
                            POSITION.x + OFFSET.x * count,
                            POSITION.y + OFFSET.y * count,
                            this);
                }
                count++;
            }
        }

        @Override
        protected void paintComponent(final Graphics theGraphics) {
            super.paintComponent(theGraphics);
            try {
                if (myGameOverFlag && myModel.getHero().isDead()) {
                    myBackground = ImageIO.read(new File("sprites/gameover.png"));
                } else if (myGameOverFlag && myModel.victoryCondition()) {
                    myBackground = ImageIO.read(new File("sprites/gamewin.png"));
                } else {
                    myBackground = ImageIO.read(new File("sprites/Background.png"));
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            theGraphics.drawImage(myBackground, 0, 0, this);
            if (!myGameOverFlag) {
                if (myModel.checkCombat()) {
                    drawMonsters(theGraphics);
                } else {
                    drawItems(theGraphics);
                }
            }
        }
    }

    private final class TextLog extends JPanel {
        private final JTextArea TEXT_AREA = getTextArea();
        private final JLabel HUD = new JLabel();

        private TextLog() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40, 40, 40));
            setHUD(myModel.getHero().getMyHealth(), 0, 0, 0);
            add(HUD);
            JScrollPane scroll = new JScrollPane(TEXT_AREA);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            add(scroll);
//            add(getTextField());
        }

        /**
         * Helper method to create the main text area.
         * @return The JTextArea
         */
        private JTextArea getTextArea() {
            JTextArea textArea = new JTextArea(12, 60);
            textArea.setBackground(new Color(20, 20, 20));
            textArea.setForeground(Color.GREEN);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            return textArea;
        }

        /**
         * Updates the values on the player HUD.
         */
        public void updateHUD() {
            final Hero hero = myModel.getHero();
            final int health = hero.getMyHealth();
            final int pillars = hero.getPillars().size();
            final  int hpots = hero.getHealingPots();
            final int vpots = hero.getVisionPots();
            setHUD(health, pillars, hpots, vpots);
        }

        /**
         * Sets the values of the player HUD
         * @param theHealth Player health.
         * @param thePillars How many pillars the player owns.
         * @param theHealthPots The total health pots in inventory.
         * @param theVisionPots The total vision pots in inventory.
         */
        private void setHUD(final int theHealth,
                            final  int thePillars,
                            final  int theHealthPots,
                            final  int theVisionPots) {
            HUD.setText("Health: " + theHealth + " Pillars: " + thePillars
                    + "/4 H.Pots: " + theHealthPots + " V.Pots: " + theVisionPots);
            HUD.setFont(new Font("Monospaced", Font.PLAIN, 24));
            HUD.setForeground(Color.GREEN);
        }

        /**
         * Helper method to make a text field beneath the text box.
         * @return the JTextField
         */
        private JTextField getTextField() {
            final JTextField field = new JTextField(60);
            field.addActionListener(e -> {
                appendTextLog(field.getText());
                field.setText("");
            });
            return field;
        }

    }

    private final class GuiMap extends JPanel {
        private final int myRows;
        private final int myCols;
        private static final int TILE_SIZE = 100;
        private static final Rectangle VERT_DOOR = new Rectangle(20, 5);
        private static final Rectangle HORI_DOOR = new Rectangle(5, 20);
        private static final Point NORTH_DOOR = new Point(40, 0);
        private static final Point SOUTH_DOOR = new Point(40, TILE_SIZE - VERT_DOOR.height);
        private static final Point WEST_DOOR = new Point(0, 40);
        private static final Point EAST_DOOR = new Point(TILE_SIZE - HORI_DOOR.width, 40);

        private GuiMap(final int theRows, final int theCol) {
            setBackground(new Color(40, 40, 40));
            setAlignmentX(CENTER_ALIGNMENT);
            myRows = theRows;
            myCols = theCol;
        }

        /**
         * Draws a door onto the room on the map.
         * @param theRow the row of the room
         * @param theCol the column of the room
         * @param theDirection the direction to draw the door onto
         * @param theGraphics the graphics element
         */
        private void drawDoor(final int theRow, final int theCol,
                              final Direction theDirection, final Graphics theGraphics) {
            try {
                final Image hori_door = ImageIO.read(new File("sprites/hori_door.png"));
                final Image vert_door = ImageIO.read(new File("sprites/vert_door.png"));
                int row = theRow * TILE_SIZE;
                int col = theCol * TILE_SIZE;
                switch (theDirection) {
                    case NORTH ->
                            theGraphics.drawImage(hori_door, row + NORTH_DOOR.x, col + NORTH_DOOR.y, this);
                    case SOUTH ->
                            theGraphics.drawImage(hori_door, row + SOUTH_DOOR.x, col + SOUTH_DOOR.y, this);
                    case EAST ->
                            theGraphics.drawImage(vert_door, row + EAST_DOOR.x, col + EAST_DOOR.y, this);
                    case WEST ->
                            theGraphics.drawImage(vert_door, row + WEST_DOOR.x, col + WEST_DOOR.y, this);
                }
            } catch (final IOException e) {
                System.out.println("Missing sprites");
            }
        }

        /**
         * Draws the elements of a given room onto the map in the proper spot.
         * @param theRoom The given room to draw
         * @param theGraphics the graphics element
         */
        private void drawRoom(final Room theRoom, final Graphics theGraphics) {
            final int x = theRoom.getMyLocation().column();
            final int y = theRoom.getMyLocation().row();
            final Room heroRoom = myModel.getRoom(myModel.getHeroLocation());
            Image explored = null;
            Image tile = null;
            Image player = null;
            try {
                explored = ImageIO.read(new File("sprites/explored.png"));
                tile = ImageIO.read(new File("sprites/tile.png"));
                player = ImageIO.read(new File("sprites/player.png"));

            } catch (final IOException e) {
                System.out.println("Missing sprites");
            }

            theGraphics.drawImage(tile, x * TILE_SIZE, y * TILE_SIZE, this);

            if (theRoom.getVisible()) {
                theGraphics.drawImage(explored, x * TILE_SIZE, y * TILE_SIZE, this);
                for (Direction d : theRoom.getDoors()) {
                    if (theRoom.getDoor(d)) {
                        drawDoor(x, y, d, theGraphics);
                    }
                }
                if (heroRoom == theRoom) {
                    theGraphics.drawImage(player, x * TILE_SIZE, y * TILE_SIZE, this);
                } else {
                    drawItems(theRoom, theGraphics);
                }
            }

        }

        /**
         * Takes the stock of a room's items and draws the appropriate sprites onto it.
         * @param theRoom The room to draw.
         * @param theGraphics the graphics element
         */
        private void drawItems(final Room theRoom, final Graphics theGraphics) {
            Image exit = null;
            Image items = null;
            Image vpot = null;
            Image hpot = null;
            Image pillar = null;
            Image hazard = null;
            try {
                exit = ImageIO.read(new File("sprites/exit.png"));
                items = ImageIO.read(new File("sprites/items.png"));
                vpot = ImageIO.read(new File("sprites/visionpot.png"));
                hpot = ImageIO.read(new File("sprites/healpot.png"));
                pillar = ImageIO.read(new File("sprites/pillar.png"));
                hazard = ImageIO.read(new File("sprites/warning.png"));
            } catch (final IOException e) {
                System.out.println("Missing Sprites");
            }
            final int x = theRoom.getMyLocation().column();
            final int y = theRoom.getMyLocation().row();
            if (theRoom.getMyItems().contains(Item.Exit)) {
                theGraphics.drawImage(exit, x * TILE_SIZE, y * TILE_SIZE, this);
            } else {
                if (theRoom.getMyItems().size() > 1) {
                    theGraphics.drawImage(items, x * TILE_SIZE, y * TILE_SIZE, this);
                } else {
                    if (theRoom.getMyItems().stream().anyMatch(
                            item -> item.name().contains("Pillar"))) {
                        theGraphics.drawImage(pillar, x * TILE_SIZE, y * TILE_SIZE, this);
                    } else if (theRoom.getMyItems().contains(Item.HealingPotion)) {
                        theGraphics.drawImage(hpot, x * TILE_SIZE, y * TILE_SIZE, this);
                    } else if (theRoom.getMyItems().contains(Item.VisionPotion)) {
                        theGraphics.drawImage(vpot, x * TILE_SIZE, y * TILE_SIZE, this);
                    } else if (theRoom.getMyItems().contains(Item.Pit)) {
                        theGraphics.drawImage(hazard, x * TILE_SIZE, y * TILE_SIZE, this);
                    }
                }
            }
        }

        @Override
        protected void paintComponent(final Graphics theGraphics) {
            super.paintComponent(theGraphics);
            for (int i = 0; i < myRows; i++) {
                for (int j = 0; j < myCols; j++) {
                    drawRoom(myModel.getRooms()[i][j], theGraphics);
                }
            }
            checkRecords();
        }
    }

    private final class OptionLog extends JPanel {
        private int myAttackOrSpecial = 1; // attack == 1; special == 2
        private final JPanel MAIN_MENU_PANEL = mainMenuPanel();
        private final JPanel DIRECTION_PANEL = directionPanel();
        private final JPanel ITEM_PANEL = itemPanel();
        private final JPanel COMBAT_PANEL = combatPanel();
        private final JPanel COMBAT_ITEMS = combatItemPanel();
        private final JPanel TARGETS = targetPanel();

        private OptionLog() {
            setBackground(new Color(40, 40, 40));
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setLayout(new CardLayout());
            add(MAIN_MENU_PANEL);
            add(DIRECTION_PANEL);
            add(ITEM_PANEL);
            add(COMBAT_PANEL);
            add(COMBAT_ITEMS);
            add(TARGETS);
        }

        /**
         * Disables all active button panels and replaces it with the combat panel
         */
        public void enterCombat() {
            MAIN_MENU_PANEL.setVisible(false);
            DIRECTION_PANEL.setVisible(false);
            ITEM_PANEL.setVisible(false);
            TARGETS.setVisible(false);
            COMBAT_ITEMS.setVisible(false);
            COMBAT_PANEL.setVisible(true);
        }

        /**
         * Disables the combat panels and returns to the main menu panel
         */
        public void exitCombat() {
            // if this condition is true, we were in combat(showing combat panel)
            // but we are no longer in combat; we must have won the fight.
            if (!myModel.checkCombat() && (COMBAT_PANEL.isVisible() || TARGETS.isVisible())) {
                COMBAT_PANEL.setVisible(false);
                MAIN_MENU_PANEL.setVisible(true);
//                appendTextLog("You won the fight!");
            }
            TARGETS.setVisible(false);
        }
        /***
         * main menu panel
         * @return JPanel with core function buttons
         */
        private JPanel mainMenuPanel() {
            final JPanel panel = new JPanel();
            panel.setBackground(new Color(40, 40, 40));
            panel.setLayout(new GridLayout(10, 1));

            final JButton[] buttons = new JButton[6];
            buttons[0] = makeButton("MOVE");
            buttons[1] = makeButton("USE ITEM ");
            buttons[2] = makeButton("HELP");
            buttons[3] = makeButton("SAVE GAME");
            buttons[4] = makeButton("LOAD GAME");
            for (int i = 0; i < 4; i++) {
                panel.add(buttons[i]);
            }
            panel.add(buttons[4]);
            buttons[0].addActionListener(e -> {
                MAIN_MENU_PANEL.setVisible(false);
                DIRECTION_PANEL.setVisible(true);
                validateDirections();
            });
            buttons[1].addActionListener(e -> {
                MAIN_MENU_PANEL.setVisible(false);
                ITEM_PANEL.setVisible(true);
            });
            buttons[2].addActionListener(e -> {
                appendTextLog("\"Call for help?\"");
                appendTextLog("\"Well too bad, because you're alone down there.\"");
                appendTextLog("\"Get the pillars, get to the teleporter and get out.\"");
                appendTextLog("*beep");
            });
            buttons[3].addActionListener(e -> { // SAVE GAME
                try {
                    final var cwd = FILE_CHOOSER.getCurrentDirectory();
                    final var choice = FILE_CHOOSER.showOpenDialog(this);
                    if (choice != JFileChooser.APPROVE_OPTION) { // option cancelled.
                        FILE_CHOOSER.setCurrentDirectory(cwd);
                        return;
                    }
                    myModel.saveGame(FILE_CHOOSER.getSelectedFile());
                } catch (final IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            buttons[4].addActionListener(e -> {
                try {
                    final var cwd = FILE_CHOOSER.getCurrentDirectory();
                    final var choice = FILE_CHOOSER.showOpenDialog(this);
                    if (choice != JFileChooser.APPROVE_OPTION) { // option cancelled.
                        FILE_CHOOSER.setCurrentDirectory(cwd);
                        return;
                    }
                    myGameOverFlag = false;
                    for(var key : myButtons.keySet()) {
                        myButtons.get(key).setEnabled(true);
                    }
                    myModel.loadGame(FILE_CHOOSER.getSelectedFile());
                    myTextLog.TEXT_AREA.setText("");
                } catch (final IOException ex) {
                    throw new RuntimeException(ex);
                }

                GUIView.getInstance(myModel).update();
            });
            return panel;
        }

        /***
         * creates a JPanel containing buttons for GuiMap navigation
         * @return JPanel
         */
        private JPanel directionPanel() {
            final JPanel panel = new JPanel();
            panel.setBackground(new Color(40, 40, 40));
            panel.setLayout(new GridLayout(10, 1));

            for (Direction d : Direction.values()) {
                final var button = makeButton(d.name());
                button.addActionListener(e -> {
                    myModel.move(d);
                    for (var item : myModel.checkNewItems()) {
                        appendTextLog("Item Discovered: " + item.name());
                        if(myModel.getHero().hasAllPillars() && item.name().contains("Pillar")) {
                            appendTextLog("You have enough pillars to power the teleporter!");
                        }
                    }
                    final var roomItems = myModel.getRoomItems(
                            myModel.getHeroLocation());
                    for(var item : roomItems) {
                        if(item == Item.Exit) {
                            appendTextLog("You see the escape teleporter and the survivors.");
                            if(!myModel.getHero().hasAllPillars()) {
                                appendTextLog("...But you don't have enough pillars to power it.");
                            } else {
                                appendTextLog("You try to power up the teleporter.");
                                appendTextLog("...But the sound attracts some bugs!");
                            }
                        } else if (item == Item.Pit) {
                            appendTextLog("The hazardous terrain wounds you.");
                            myModel.getHero().takeDamage(10);
                            appendTextLog("You are scraped for 10 damage.");
                            checkRecords();
                        } else {
                            appendTextLog("You see a " + item.name()
                                    + " in the room...");
                        }
                    }
                    if (myModel.checkCombat()) {
                        enterCombat();
                    }
                    GUIView.getInstance(myModel).update();
                });
                panel.add(button);
            }
            final JButton returnButton = makeButton("<- RETURN");
            returnButton.addActionListener(e -> {
                MAIN_MENU_PANEL.setVisible(true);
                DIRECTION_PANEL.setVisible(false);
            });
            panel.add(returnButton);
            return panel;
        }

        /**
         * Creates a panel for selection of items.
         * @return JPanel
         */
        private JPanel itemPanel() {
            final JPanel panel = new JPanel();
            panel.setBackground(new Color(40, 40, 40));
            panel.setLayout(new GridLayout(10, 1));
            final JButton[] buttons = new JButton[5];

            buttons[0] = makeButton("USE HEALING");
            buttons[1] = makeButton("USE VISION");
            buttons[2] = makeButton("<- RETURN");
            var returnToMain = MAIN_MENU_PANEL;
            if (myModel.checkCombat())
                returnToMain = COMBAT_PANEL;
            final var returnPanel = returnToMain;
            for (int i = 0; i < 3; i++) {
                panel.add(buttons[i]);
            }
            buttons[0].addActionListener(e -> {
                myModel.getHero().useHealingPot();
                checkRecords();
                ITEM_PANEL.setVisible(false);
                Objects.requireNonNull(returnPanel).setVisible(true);
            });
            buttons[1].addActionListener(e -> {
                myModel.useVisionPot();
                ITEM_PANEL.setVisible(false);
                Objects.requireNonNull(returnPanel).setVisible(true);
            });
            buttons[2].addActionListener(e -> {
                ITEM_PANEL.setVisible(false);
                Objects.requireNonNull(returnPanel).setVisible(true);
            });
            return panel;
        }

        /**
         * Creates a panel for selection of items exclusive to combat.
         * @return JPanel
         */
        private JPanel combatItemPanel() {
            final JPanel panel = new JPanel();
            panel.setBackground(new Color(40, 40, 40));
            panel.setLayout(new GridLayout(10, 1));
            final JButton[] buttons = new JButton[5];

            buttons[0] = makeButton("USE HEALING");
            buttons[1] = makeButton("<- RETURN");
            for (int i = 0; i < 2; i++) {
                panel.add(buttons[i]);
            }
            buttons[0].addActionListener(e -> {
                myModel.getHero().useHealingPot();
                checkRecords();
                COMBAT_ITEMS.setVisible(false);
                COMBAT_PANEL.setVisible(true);
            });
            buttons[1].addActionListener(e -> {
                COMBAT_ITEMS.setVisible(false);
                COMBAT_PANEL.setVisible(true);
            });
            return panel;
        }

        /**
         * Creates panel for selection of attack target in combat.
         * @return JPanel
         */
        private JPanel targetPanel() {
            final JPanel panel = new JPanel();
            panel.setBackground(new Color(40, 40, 40));
            panel.setLayout(new GridLayout(10, 1));

            final JButton[] buttons = new JButton[5];
            for (int i = 0; i < 3; i++) { // buttons for target selection (1 - 3)
                buttons[i] = makeButton(Integer.toString(i + 1));
                final int finalI = i;
                buttons[i].addActionListener(e -> {
                    myModel.getMyCombat().heroTurn(myAttackOrSpecial, finalI);
                    checkRecords();
                    TARGETS.setVisible(false);
                    COMBAT_PANEL.setVisible(true);
                });
                panel.add(buttons[i]);
            }

            buttons[3] = makeButton("<- RETURN");
            buttons[3].addActionListener(e -> {
                TARGETS.setVisible(false);
                COMBAT_PANEL.setVisible(true);
            });

            panel.add(buttons[3]);
            return panel;
        }

        /**
         * Creates a panel for selecting what to do in combat.
         * @return JPanel
         */
        private JPanel combatPanel() {
            final JPanel panel = new JPanel();
            panel.setBackground(new Color(40, 40, 40));
            panel.setLayout(new GridLayout(10, 1));

            final JButton[] buttons = new JButton[4];
            buttons[0] = makeButton("ATTACK");
            buttons[1] = makeButton("SPECIAL");
            buttons[2] = makeButton("USE ITEM");
            buttons[3] = makeButton("INFO");

            buttons[0].addActionListener(e -> { // ATTACK
                COMBAT_PANEL.setVisible(false);
                validateTargets();
                TARGETS.setVisible(true);
                myAttackOrSpecial = 1;
            });
            buttons[1].addActionListener(e -> { // SPECIAL
                if (myModel.getHero().getMyClass().equals("Survivalist")) {
                    myModel.getMyCombat().heroTurn(2, 0);
                    checkRecords();
                } else {
                    COMBAT_PANEL.setVisible(false);
                    validateTargets();
                    TARGETS.setVisible(true);
                    myAttackOrSpecial = 2;
                }
            });
            buttons[2].addActionListener(e -> { // USE ITEM
                COMBAT_PANEL.setVisible(false);
                COMBAT_ITEMS.setVisible(true);
            });
            buttons[3].addActionListener(e -> { // INFO
                int i = 0;
                appendTextLog("Initiating bio-scanner...");
                final var monsters =  myModel.getMyCombat().getMonsters();
                for (var monster : monsters) {
                    i++;
                    if (!monster.isDead()) {
                        final String name = monster.getMyName();
                        final int health = monster.getMyHealth();
                        appendTextLog("[" + i + "] " + name + " | Health: " + health);
                    }
                }
            });
            for (var button : buttons) {
                panel.add(button);
            }
            return panel;
        }

        /**
         * Helper method to make a uniform button.
         * @param theText The label on the button
         * @return JButton
         */
        private JButton makeButton(final String theText) {
            final JButton button = new JButton(theText);
            button.setBackground(Color.black);
            button.setForeground(Color.green);
            button.setFont(new Font("Monospaced", Font.PLAIN, 24));
            myButtons.put(theText, button);
            return button;
        }
    }
}
