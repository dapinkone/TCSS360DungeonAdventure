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


public class GUIView extends JFrame {
    private GameModel myModel;
    private MY_TEXTLOG myTextlog;
    private final POV MY_POV = new POV();
    guiMap guiMap;
    private Optionlog myOptionlog;
    private Map<String, JButton> myButtons;
    private static GUIView instance = null;
    private static final JFileChooser fileChooser = new JFileChooser("./");
    public static GUIView getInstance(GameModel theModel) {
        if(instance  == null) {
            instance = new GUIView(theModel);
        }
        return instance;
    }
    private GUIView(GameModel theModel) {
        myButtons =  new HashMap<>();
        myModel = theModel;
        myTextlog = new MY_TEXTLOG();
        myOptionlog= new Optionlog();
        setTitle("Cave Rescue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(MY_POV);
        add(guiMap = new guiMap(theModel.getRooms().length,
                    theModel.getRooms()[0].length));
        add(myTextlog);
        add(myOptionlog);
        setLayout(new GridLayout(2,2));
        setResizable(false);
        setVisible(true);
        pack();
    }
    private void checkRecords() {
        final var recordQ = RecordQ.getInstance();
        HealthChangeRecord record;
        while( (record = recordQ.poll()) != null) {
            final var src = record.source().getMyName();
            final var tgt = record.target().getMyName();
            final var amt = record.amount();
            final var type = record.actionResultType();
            appendTextLog("*" +
                    switch (type) {
                        case Heal -> src + " healed themselves for "
                                + amt + " health!";
                        case Hit -> src + " hit " + tgt + " for " + amt + " damage!";
                        case CrushingBlow -> src + " dealt " + tgt
                                + " a crushing blow for " + amt + " damage!";
                        case CriticalHit -> src + " got a critical hit! " + amt +
                                " damage to " + tgt;
                        case Miss -> src + " swings to hit " + tgt
                                + " but fumbles and misses.";
                    } + "*");
            if (record.target() == myModel.getHero()) {
                update();
            }
        }
        if(!myModel.checkCombat()) { // won the fight!
            myOptionlog.exitCombat();
            update();
        }
    }
    public static void main(String[] args) {
        DefaultModel model = new DefaultModel();
        model.newDungeon(4,4);
        model.setHero(new Warrior("Karl"));
        GUIView guiView = GUIView.getInstance(model);
    }

    public void appendTextLog(String theText) {
        System.out.println(theText);
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

        guiMap.repaint();
        MY_POV.repaint();
    }

    /***
     * disables/enables buttons so that only valid choices are enabled for
     * navigation
     */
    public void validateDirections() {
        final var validDirections = myModel.getRoomDoors(myModel.getHeroLocation());
        for(Direction d : Direction.values()) {
            myButtons.get(d.name()).setEnabled(
                    validDirections.contains(d)
            );
        }
    }

    private class POV extends JPanel{
        private final int WIDTH = 700;
        private final int HEIGHT = 400;
        private final Point POS = new Point(380,160);
        private final Point OFFSET = new Point(120,20);

        private POV() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
            setBorder(new LineBorder(Color.GREEN, 1));
        }

        private void drawMonsters(Graphics g) {
            Image skitter = null, crawler = null, predator = null;
            Room heroRoom = myModel.getRooms()
                    [myModel.getHeroLocation().getRow()]
                    [myModel.getHeroLocation().getColumn()];
            try {
                skitter = ImageIO.read(new File("sprites/Skitter.png"));
                crawler = ImageIO.read(new File("sprites/Crawler.png"));
                predator = ImageIO.read(new File("sprites/predator.png"));
            } catch (IOException e) {
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
        private void drawItems(Graphics g) {
            Image vpot = null, hpot = null, pillar = null;
            Room heroRoom = myModel.getRooms()
                    [myModel.getHeroLocation().getRow()]
                    [myModel.getHeroLocation().getColumn()];
            try {
                hpot = ImageIO.read(new File("sprites/healpot.png"));
                vpot = ImageIO.read(new File("sprites/visionpot.png"));
                pillar = ImageIO.read(new File("sprites/pillar.png"));
            } catch (IOException e) {
                System.out.println("Missing Sprites");
            }
            int count = 0;
            for (Item item : heroRoom.getMyItems()) {
                if (item.name().contains("Pillar")) {
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
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image bg = null;
            try {
                bg = ImageIO.read(new File("sprites/Background.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawImage(bg, 0,0, this);
            if (myModel.checkCombat()) {
                drawMonsters(g);
            } else {
                drawItems(g);
            }

        }
    }
    protected class MY_TEXTLOG extends JPanel{
        private final JTextArea TEXT_AREA = getTextArea(12,60);;
        private final JLabel HUD = new JLabel();

        private MY_TEXTLOG() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
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
            textArea.setBackground(new Color(20,20,20));
            textArea.setForeground(Color.GREEN);
            textArea.setFont(new Font("Monospaced",Font.PLAIN ,18));
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            return textArea;
        }

        public void updateHUD() {
            Hero hero = myModel.getHero();
            int health = hero.getMyHealth();
            int pillars = hero.getPillars().size();
            int hpots = hero.getHealingPots();
            int vpots = hero.getVisionPots();
            setHUD(health,pillars,hpots,vpots);
        }

        private void setHUD(int health, int pillars, int hpots, int vpots) {
            HUD.setText("Health: " + health + " Pillars: " + pillars +
                    "/4 H.Pots: " + hpots + " V.Pots: " + vpots);
            HUD.setFont(new Font("Monospaced",Font.PLAIN ,24));
            HUD.setForeground(Color.GREEN);
        }

        private JTextField getTextField() {
            JTextField field = new JTextField(60);
            field.addActionListener(e -> {
                appendTextLog(field.getText());
                //field.setText("");
            });
            return field;
        }

    }
    private class guiMap extends JPanel{
        private int myRows;
        private int myCols;
        private final int TILE_SIZE = 100;
        private final Rectangle VERT_DOOR = new Rectangle(20,5);
        private final Rectangle HORI_DOOR = new Rectangle(5,20);
        private final Point NORTH_DOOR = new Point(40,0);
        private final Point SOUTH_DOOR = new Point(40,TILE_SIZE - VERT_DOOR.height);
        private final Point WEST_DOOR = new Point(0,40);
        private final Point EAST_DOOR = new Point(TILE_SIZE - HORI_DOOR.width,40);

        private guiMap(int theRows, int theCol) {
            setBackground(new Color(40, 40, 40));
            setAlignmentX(CENTER_ALIGNMENT);
            myRows = theRows;
            myCols = theCol;
        }

        private void drawDoor(int theRow, int theCol, Direction theDirection, Graphics g) {
            try {
                Image hori_door = ImageIO.read(new File("sprites/hori_door.png"));
                Image vert_door = ImageIO.read(new File("sprites/vert_door.png"));
                int row = theRow * TILE_SIZE;
                int col = theCol * TILE_SIZE;
                switch (theDirection) {
                    case NORTH -> g.drawImage(hori_door, row + NORTH_DOOR.x, col + NORTH_DOOR.y, this);
                    case SOUTH -> g.drawImage(hori_door, row + SOUTH_DOOR.x, col + SOUTH_DOOR.y, this);
                    case EAST -> g.drawImage(vert_door, row + EAST_DOOR.x, col + EAST_DOOR.y, this);
                    case WEST -> g.drawImage(vert_door, row + WEST_DOOR.x, col + WEST_DOOR.y, this);
                }
            } catch (IOException e) {
                System.out.println("Missing sprites");
            }
        }

        private void drawRoom(Room theRoom, Graphics g) {
            int x = theRoom.getMyLocation().getColumn();
            int y = theRoom.getMyLocation().getRow();
            Room heroRoom = myModel.getRooms()
                    [myModel.getHeroLocation().getRow()]
                    [myModel.getHeroLocation().getColumn()];
            Image explored = null, tile = null, player = null;
            try {
                explored = ImageIO.read(new File("sprites/explored.png"));
                tile = ImageIO.read(new File("sprites/tile.png"));
                player = ImageIO.read(new File("sprites/player.png"));

            } catch (IOException e) {
                System.out.println("Missing sprites");
            }

            g.drawImage(tile,x * TILE_SIZE, y * TILE_SIZE, this);

            if (theRoom.getVisible()) {
                g.drawImage(explored,x * TILE_SIZE, y * TILE_SIZE, this);
                for (Direction d: theRoom.getDoors()) {
                    if (theRoom.getDoor(d)) {
                        drawDoor(x, y, d, g);
                    }
                }
                if (heroRoom == theRoom) {
                    g.drawImage(player,x * TILE_SIZE, y * TILE_SIZE, this);
                } else {
                    drawItems(theRoom, g);
                }
            }

        }

        private void drawItems(Room theRoom, Graphics g) {
            Image exit = null, items = null, vpot = null, hpot = null, pillar = null, hazard = null;
            try {
                exit = ImageIO.read(new File("sprites/exit.png"));
                items = ImageIO.read(new File("sprites/items.png"));
                vpot = ImageIO.read(new File("sprites/visionpot.png"));
                hpot = ImageIO.read(new File("sprites/healpot.png"));
                pillar = ImageIO.read(new File("sprites/pillar.png"));
                hazard = ImageIO.read(new File("sprites/warning.png"));
            } catch (IOException e) {
                System.out.println("Missing Sprites");
            }
            int x = theRoom.getMyLocation().getColumn();
            int y = theRoom.getMyLocation().getRow();
            if (theRoom.getMyItems().contains(Item.Exit)) {
                g.drawImage(exit, x * TILE_SIZE, y * TILE_SIZE, this);
            } else {
                if (theRoom.getMyItems().size() > 1) {
                    g.drawImage(items, x * TILE_SIZE, y * TILE_SIZE, this);
                } else {
                    if (theRoom.getMyItems().contains(Item.PillarAbstraction) ||
                            theRoom.getMyItems().contains(Item.PillarEncapsulation) ||
                            theRoom.getMyItems().contains(Item.PillarPolymorphism) ||
                            theRoom.getMyItems().contains(Item.PillarInheritance)) {
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
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                Image explored = ImageIO.read(new File("sprites/explored.png"));
                Image tile = ImageIO.read(new File("sprites/tile.png"));
                Image player = ImageIO.read(new File("sprites/player.png"));
                for (int i = 0; i < myRows; i++) {
                    for (int j = 0; j < myCols; j++) {
                        drawRoom(myModel.getRooms()[i][j], g);
                    }
                }
                checkRecords();
            } catch (IOException e) {
                System.out.println("Missing sprites");
            }
        }
    }
    private class Optionlog extends JPanel {
        private final JPanel MAIN_MENU_PANEL = mainMenuPanel();
        private final JPanel DIRECTION_PANEL = directionPanel();
        private final JPanel TARGETS = targetPanel();
        private final JPanel ITEM_PANEL = itemPanel();
        private final JPanel COMBAT_PANEL = combatPanel();
        private Optionlog() {
            setBackground(new Color(40,40,40));
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setLayout(new CardLayout());
            add(MAIN_MENU_PANEL);
            add(DIRECTION_PANEL);
            add(ITEM_PANEL);
            add(COMBAT_PANEL);
            add(TARGETS);
        }

        public void enterCombat() {
            MAIN_MENU_PANEL.setVisible(false);
            DIRECTION_PANEL.setVisible(false);
            ITEM_PANEL.setVisible(false);
            TARGETS.setVisible(false);
            COMBAT_PANEL.setVisible(true);
        }

        public void exitCombat() {
            if(!myModel.checkCombat() && (COMBAT_PANEL.isVisible() || TARGETS.isVisible())) {
                COMBAT_PANEL.setVisible(false);
                MAIN_MENU_PANEL.setVisible(true);
            }
            TARGETS.setVisible(false);
        }

        /***
         * main menu panel
         * @return JPanel with core function buttons
         */
        private JPanel mainMenuPanel() {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(40,40,40));
            panel.setLayout(new GridLayout(10, 1));

            JButton[] buttons = new JButton[5];
            buttons[0] = makeButton("MOVE");
            buttons[1] = makeButton("USE ITEM");
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
            });
            buttons[3].addActionListener(e -> { // SAVE GAME
                try {
                    var cwd = fileChooser.getCurrentDirectory();
                    var choice = fileChooser.showOpenDialog(this);
                    if (choice != JFileChooser.APPROVE_OPTION) { // option cancelled.
                        fileChooser.setCurrentDirectory(cwd);
                        return;
                    }
                    myModel.saveGame(fileChooser.getSelectedFile());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            buttons[4].addActionListener(e -> {
                try {
                    var cwd = fileChooser.getCurrentDirectory();
                    var choice = fileChooser.showOpenDialog(this);
                    if (choice != JFileChooser.APPROVE_OPTION) { // option cancelled.
                        fileChooser.setCurrentDirectory(cwd);
                        return;
                    }
                    myModel.loadGame(fileChooser.getSelectedFile());
                    myTextlog.TEXT_AREA.setText("");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                GUIView.getInstance(myModel).update();
            });
            return panel;
        }

        /***
         * creates a JPanel containing buttons for guiMap navigation
         * @return JPanel
         */
        private JPanel directionPanel() {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(40,40,40));
            panel.setLayout(new GridLayout(10, 1));

            for(Direction d : Direction.values()) {
                var button = makeButton(d.name());
                button.addActionListener(e -> {
                    myModel.move(d);
                    for(var item : myModel.checkNewItems()) {
                        appendTextLog("Found " + item.name() + "!");
                    }
                    if(myModel.checkCombat()) {
                        enterCombat();
                    }
                    GUIView.getInstance(myModel).update();
                });
                panel.add(button);
            }
            JButton returnButton = makeButton("<- RETURN");
            returnButton.addActionListener(e -> {
                MAIN_MENU_PANEL.setVisible(true);
                DIRECTION_PANEL.setVisible(false);
            });
            panel.add(returnButton);
            return panel;
        }
        private JPanel itemPanel() {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(40,40,40));
            panel.setLayout(new GridLayout(10, 1));
            JButton[] buttons = new JButton[5];

            buttons[0] = makeButton("USE HEALING");
            buttons[1] = makeButton("USE VISION");
            buttons[2] = makeButton("<- RETURN");
            JPanel retPanel = MAIN_MENU_PANEL;
            if(myModel.checkCombat())
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
                //myModel.getHero().useVisionPot( );
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
            JPanel panel = new JPanel();
            panel.setBackground(new Color(40,40,40));
            panel.setLayout(new GridLayout(10, 1));

            JButton[] buttons = new JButton[5];
            for(int i=0; i < 3; i++) { // buttons for target selection (1 - 3)
                buttons[i] = makeButton(Integer.toString(i + 1));
                final int finalI = i;
                buttons[i].addActionListener(e -> {
                    myModel.getMyCombat().heroTurn(attackOrSpecial, finalI);
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
        int attackOrSpecial = 1; // attack == 1; special == 2

        private JPanel combatPanel() {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(40,40,40));
            panel.setLayout(new GridLayout(10, 1));

            JButton[] buttons = new JButton[5];
            buttons[0] = makeButton("ATTACK");
            buttons[1] = makeButton("SPECIAL");
            buttons[2] = makeButton("USE ITEM");
            buttons[3] = makeButton("HELP");
            buttons[4] = makeButton("[TEST] END COMBAT");


            buttons[0].addActionListener(e -> { // ATTACK
                COMBAT_PANEL.setVisible(false);
                TARGETS.setVisible(true);
                attackOrSpecial = 1;
            });
            buttons[1].addActionListener(e -> { // SPECIAL
                COMBAT_PANEL.setVisible(false);
                TARGETS.setVisible(true);
                attackOrSpecial = 2;
            });
            buttons[2].addActionListener(e -> { // USE ITEM
                COMBAT_PANEL.setVisible(false);
                ITEM_PANEL.setVisible(true);
            });
            buttons[4].addActionListener(e -> { // "[TEST] END COMBAT"
                COMBAT_PANEL.setVisible(false);
                MAIN_MENU_PANEL.setVisible(true);
            });
            for (var button : buttons) {
                panel.add(button);
            }
            return panel;
        }

        private JButton makeButton(String theText) {
            JButton button = new JButton(theText);
            button.setBackground(Color.black);
            button.setForeground(Color.green);
            button.setFont(new Font("Monospaced",Font.PLAIN ,24));
            myButtons.put(theText, button);
            return button;
        }
    }
}
