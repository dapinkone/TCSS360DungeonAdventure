package DungeonAdventure;

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
    private final MY_TEXTLOG myTextlog;
    private final POV myPov = new POV();
    private final Optionlog myOptionlog;
    private final Map<String, JButton> myButtons;
    private final GuiMap myGuiMap;
    private boolean myGameOverFlag = false;
    private GUIView(final GameModel theModel) {
        myButtons = new HashMap<>();
        myModel = theModel;
        myTextlog = new MY_TEXTLOG();
        myOptionlog = new Optionlog();
        setTitle("Cave Rescue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(myPov);
        add(myGuiMap = new GuiMap(theModel.getRooms().length,
                theModel.getRooms()[0].length));
        add(myTextlog);
        add(myOptionlog);
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

    private void checkRecords() {
        final var recordQ = RecordQ.getInstance();
        HealthChangeRecord record;
        while ((record = recordQ.poll()) != null) {
            final var src = record.source().getMyName();
            final var tgt = record.target().getMyName();
            final var amt = record.amount();
            final var type = record.actionResultType();
            appendTextLog(
                    "*" + switch (type) {
                        case Heal -> src + " healed themselves for "
                                + amt + " health!";
                        case Hit ->
                                src + " hit " + tgt + " for " + amt + " damage!";
                        case CrushingBlow -> src + " dealt " + tgt
                                + " a crushing blow for " + amt + " damage!";
                        case CriticalHit ->
                                src + " got a critical hit! " + amt +
                                        " damage to " + tgt;
                        case Miss -> src + " swings to hit " + tgt
                                + " but fumbles and misses.";
                    } + "*");
            if (record.target() == myModel.getHero()) {
                update();
            }
        }
        if (!myModel.checkCombat()) { // won the fight!
            Image newScreen = null;
            if (myModel.getHero().isDead() && ! myGameOverFlag) {
                myGameOverFlag = true;
                appendTextLog("Game Over! You have died.");
                try {
                    newScreen = ImageIO.read(new File("sprites/gameover.png"));
                } catch (final IOException e) {
                    appendTextLog("Game Over! You have died.");
                }
                for(var buttonKey : myButtons.keySet()) {
                    myButtons.get(buttonKey).setEnabled(false);
                }
                myButtons.get("LOAD GAME").setEnabled(true);
            } else if (myModel.victoryCondition() && !myGameOverFlag) {
                myGameOverFlag = true;
                appendTextLog("Victory! You have escaped!");
                try {
                    newScreen = ImageIO.read(new File("sprites/gamewin.png"));
                } catch (final IOException e) {
                    appendTextLog("Victory! You have escaped!");
                }
                for(var buttonKey : myButtons.keySet()) {
                    myButtons.get(buttonKey).setEnabled(false);
                }
                myButtons.get("LOAD GAME").setEnabled(true);
            }
            if(newScreen != null) {
                myPov.drawItems(newScreen.getGraphics());
                myPov.repaint();
            }
            myOptionlog.exitCombat();
            update();
        }
    }

    public void appendTextLog(final String theText) {
        //System.out.println(theText);
        myTextlog.TEXT_AREA.append(" >" + theText + "\n");
        myTextlog.TEXT_AREA.setCaretPosition(
                myTextlog.TEXT_AREA.getDocument().getLength());
    }

    public void update() {
        myTextlog.updateHUD();
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
    public void validateDirections() {
        final var validDirections = myModel.getRoomDoors(myModel.getHeroLocation());
        for (Direction d : Direction.values()) {
            myButtons.get(d.name()).setEnabled(
                    validDirections.contains(d)
            );
        }
    }

    private class POV extends JPanel {
        private final int WIDTH = 700;
        private final int HEIGHT = 400;
        private final Point POS = new Point(300, 160);
        private final Point OFFSET = new Point(120, 20);

        private POV() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40, 40, 40));
            setBorder(new LineBorder(Color.GREEN, 1));
        }

        private void drawMonsters(final Graphics g) {
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
                    g.drawImage(spriteLookup.get(monster.getMyName()),
                            POS.x + OFFSET.x * count,
                            POS.y + OFFSET.y * count,
                            this);
                }
                count++;
            }
        }

        private void drawItems(final Graphics g) {
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
                    g.drawImage(exit,
                            POS.x + OFFSET.x * count,
                            POS.y + OFFSET.y * count,
                            this);
                } else if (item.name().contains("Pillar")) {
                    g.drawImage(pillar,
                            POS.x + OFFSET.x * count,
                            POS.y + OFFSET.y * count,
                            this);
                } else if (item == (Item.HealingPotion)) {
                    g.drawImage(hpot,
                            POS.x + OFFSET.x * count,
                            POS.y + OFFSET.y * count,
                            this);
                } else if (item == (Item.VisionPotion)) {
                    g.drawImage(vpot,
                            POS.x + OFFSET.x * count,
                            POS.y + OFFSET.y * count,
                            this);
                }
                count++;
            }
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            Image bg = null;
            try {
                bg = ImageIO.read(new File("sprites/Background.png"));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            g.drawImage(bg, 0, 0, this);
            if (myModel.checkCombat()) {
                drawMonsters(g);
            } else {
                drawItems(g);
            }

        }
    }

    protected final class MY_TEXTLOG extends JPanel {
        private final JTextArea TEXT_AREA = getTextArea(12, 60);
        private final JLabel HUD = new JLabel();

        private MY_TEXTLOG() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40, 40, 40));
            setHUD(myModel.getHero().getMyHealth(), 0, 0, 0);
            add(HUD);
            //TEXT_AREA.setText("....");
            //appendTextLog("\n");
            JScrollPane scroll = new JScrollPane(TEXT_AREA);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            add(scroll);
            add(getTextField());
        }

        private JTextArea getTextArea(final int rows, final int col) {
            JTextArea textArea = new JTextArea(rows, col);
            textArea.setBackground(new Color(20, 20, 20));
            textArea.setForeground(Color.GREEN);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            return textArea;
        }

        public void updateHUD() {
            final Hero hero = myModel.getHero();
            final int health = hero.getMyHealth();
            final int pillars = hero.getPillars().size();
            final  int hpots = hero.getHealingPots();
            final int vpots = hero.getVisionPots();
            setHUD(health, pillars, hpots, vpots);
        }

        private void setHUD(final int theHealth,
                            final  int thePillars,
                            final  int theHealthPots,
                            final  int theVisionPots) {
            HUD.setText("Health: " + theHealth + " Pillars: " + thePillars
                    + "/4 H.Pots: " + theHealthPots + " V.Pots: " + theVisionPots);
            HUD.setFont(new Font("Monospaced", Font.PLAIN, 24));
            HUD.setForeground(Color.GREEN);
        }

        private JTextField getTextField() {
            final JTextField field = new JTextField(60);
            field.addActionListener(e -> {
                appendTextLog(field.getText());
                //field.setText("");
            });
            return field;
        }

    }

    private final class GuiMap extends JPanel {
        private final int myRows;
        private final int myCols;
        private final int TILE_SIZE = 100;
        private final Rectangle VERT_DOOR = new Rectangle(20, 5);
        private final Rectangle HORI_DOOR = new Rectangle(5, 20);
        private final Point NORTH_DOOR = new Point(40, 0);
        private final Point SOUTH_DOOR = new Point(40, TILE_SIZE - VERT_DOOR.height);
        private final Point WEST_DOOR = new Point(0, 40);
        private final Point EAST_DOOR = new Point(TILE_SIZE - HORI_DOOR.width, 40);

        private GuiMap(final int theRows, final int theCol) {
            setBackground(new Color(40, 40, 40));
            setAlignmentX(CENTER_ALIGNMENT);
            myRows = theRows;
            myCols = theCol;
        }

        private void drawDoor(final int theRow, final int theCol, final Direction theDirection, final Graphics g) {
            try {
                final Image hori_door = ImageIO.read(new File("sprites/hori_door.png"));
                final Image vert_door = ImageIO.read(new File("sprites/vert_door.png"));
                int row = theRow * TILE_SIZE;
                int col = theCol * TILE_SIZE;
                switch (theDirection) {
                    case NORTH ->
                            g.drawImage(hori_door, row + NORTH_DOOR.x, col + NORTH_DOOR.y, this);
                    case SOUTH ->
                            g.drawImage(hori_door, row + SOUTH_DOOR.x, col + SOUTH_DOOR.y, this);
                    case EAST ->
                            g.drawImage(vert_door, row + EAST_DOOR.x, col + EAST_DOOR.y, this);
                    case WEST ->
                            g.drawImage(vert_door, row + WEST_DOOR.x, col + WEST_DOOR.y, this);
                }
            } catch (final IOException e) {
                System.out.println("Missing sprites");
            }
        }

        private void drawRoom(final Room theRoom, final Graphics g) {
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

            g.drawImage(tile, x * TILE_SIZE, y * TILE_SIZE, this);

            if (theRoom.getVisible()) {
                g.drawImage(explored, x * TILE_SIZE, y * TILE_SIZE, this);
                for (Direction d : theRoom.getDoors()) {
                    if (theRoom.getDoor(d)) {
                        drawDoor(x, y, d, g);
                    }
                }
                if (heroRoom == theRoom) {
                    g.drawImage(player, x * TILE_SIZE, y * TILE_SIZE, this);
                } else {
                    drawItems(theRoom, g);
                }
            }

        }

        private void drawItems(final Room theRoom, final Graphics g) {
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
                g.drawImage(exit, x * TILE_SIZE, y * TILE_SIZE, this);
            } else {
                if (theRoom.getMyItems().size() > 1) {
                    g.drawImage(items, x * TILE_SIZE, y * TILE_SIZE, this);
                } else {
                    if (theRoom.getMyItems().stream().anyMatch(
                            item -> item.name().contains("Pillar"))) {
                        g.drawImage(pillar, x * TILE_SIZE, y * TILE_SIZE, this);
                    } else if (theRoom.getMyItems().contains(Item.HealingPotion)) {
                        g.drawImage(hpot, x * TILE_SIZE, y * TILE_SIZE, this);
                    } else if (theRoom.getMyItems().contains(Item.VisionPotion)) {
                        g.drawImage(vpot, x * TILE_SIZE, y * TILE_SIZE, this);
                    } else if (theRoom.getMyItems().contains(Item.Pit)) {
                        g.drawImage(hazard, x * TILE_SIZE, y * TILE_SIZE, this);
                    }
                }
            }
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            //try {
//              Image explored = ImageIO.read(new File("sprites/explored.png"));
//                Image tile = ImageIO.read(new File("sprites/tile.png"));
//                Image player = ImageIO.read(new File("sprites/player.png"));
            for (int i = 0; i < myRows; i++) {
                for (int j = 0; j < myCols; j++) {
                    drawRoom(myModel.getRooms()[i][j], g);
                }
            }
            checkRecords();
//            } catch (final IOException e) {
  //              System.out.println("Missing sprites");
    //        }
        }
    }

    private final class Optionlog extends JPanel {
        private int myAttackOrSpecial = 1; // attack == 1; special == 2
        private final JPanel MAIN_MENU_PANEL = mainMenuPanel();
        private Optionlog() {
            setBackground(new Color(40, 40, 40));
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setLayout(new CardLayout());
            add(MAIN_MENU_PANEL);
            add(DIRECTION_PANEL);
            add(ITEM_PANEL);
            add(COMBAT_PANEL);
            add(TARGETS);
        }
        private final JPanel DIRECTION_PANEL = directionPanel();

        public void enterCombat() {
            MAIN_MENU_PANEL.setVisible(false);
            DIRECTION_PANEL.setVisible(false);
            ITEM_PANEL.setVisible(false);
            TARGETS.setVisible(false);
            COMBAT_PANEL.setVisible(true);
        }
        private final JPanel TARGETS = targetPanel();

        public void exitCombat() {
            // if this condition is true, we were in combat(showing combat panel)
            // but we are no longer in combat; we must have won the fight.
            if (!myModel.checkCombat() && (COMBAT_PANEL.isVisible() || TARGETS.isVisible())) {
                COMBAT_PANEL.setVisible(false);
                MAIN_MENU_PANEL.setVisible(true);
                //appendTextLog("You won the fight!");
            }
            TARGETS.setVisible(false);
        }        private final JPanel ITEM_PANEL = itemPanel();

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
            //buttons[4] = makeButton("[TEST] START COMBAT");
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
                appendTextLog("Call for help?");
                appendTextLog("Well too bad, because you're alone down there.");
                appendTextLog("Get the pillars, get to the teleporter and get out.");
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
                    myTextlog.TEXT_AREA.setText("");
                } catch (final IOException ex) {
                    throw new RuntimeException(ex);
                }

                GUIView.getInstance(myModel).update();
            });
            return panel;
        }        private final JPanel COMBAT_PANEL = combatPanel();

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
                        appendTextLog("Found " + item.name() + "!");
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

        private JPanel itemPanel() {
            final JPanel panel = new JPanel();
            panel.setBackground(new Color(40, 40, 40));
            panel.setLayout(new GridLayout(10, 1));
            final JButton[] buttons = new JButton[5];

            buttons[0] = makeButton("USE HEALING");
            buttons[1] = makeButton("USE VISION");
            buttons[2] = makeButton("<- RETURN");
            JPanel retPanel = MAIN_MENU_PANEL;
            if (myModel.checkCombat())
                retPanel = COMBAT_PANEL;
            final var returnPanel = retPanel;
            for (int i = 0; i < 3; i++) {
                panel.add(buttons[i]);
            }
            buttons[0].addActionListener(e -> {
                myModel.getHero().useHealingPot();
                checkRecords();
                ITEM_PANEL.setVisible(false);
                Objects.requireNonNull(returnPanel).setVisible(true);
            });
            buttons[1].addActionListener(e -> { // todo: implement vision pots
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

        private JPanel targetPanel() {
            // TODO: heroTurn() requires different arguments for special vs attack
            // TODO: transfer special vs attack selection state to here?
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
                TARGETS.setVisible(true);
                myAttackOrSpecial = 1;
            });
            buttons[1].addActionListener(e -> { // SPECIAL
                COMBAT_PANEL.setVisible(false);
                TARGETS.setVisible(true);
                myAttackOrSpecial = 2;
            });
            buttons[2].addActionListener(e -> { // USE ITEM
                COMBAT_PANEL.setVisible(false);
                ITEM_PANEL.setVisible(true);
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
                for(i=0; i < 3; i++) {
                    final var button = myButtons.get(Integer.toString(i));
                    if(monsters.size() >= i) {
                        button.setEnabled(
                                !monsters.get(i).isDead() && !myModel.getHero().isDead());
                    } else {
                        myButtons.get(Integer.toString(i)).setEnabled(false);
                    }
                }
            });
            for (var button : buttons) {
                panel.add(button);
            }
            return panel;
        }

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
