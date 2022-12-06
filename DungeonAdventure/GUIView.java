package DungeonAdventure;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GUIView extends JFrame {
    private static GameModel myModel;
//    private static final Textlog MY_TEXTLOG = ;
    private static final POV MY_POV = new POV();
    static Map map;
    private static final Optionlog MY_OPTIONLOG = new Optionlog();

    public GUIView(GameModel theModel) {
        myModel = theModel;
        setTitle("Cave Rescue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(MY_POV);
        add(map = new Map(theModel.getRooms().length,
                    theModel.getRooms()[0].length));
        add(new Textlog());
        add(MY_OPTIONLOG);
        setLayout(new GridLayout(2,2));
        setResizable(false);
        setVisible(true);
        pack();
    }

    public static void main(String[] args) {
        DefaultModel model = new DefaultModel();
        model.newDungeon(3,3);
        model.setHero(new Warrior("Karl"));
        GUIView guiView = new GUIView(model);
    }

    public static void appendTextlog(String theText) {
        Textlog.TEXT_AREA.append(" >" + theText + "\n");
        Textlog.TEXT_AREA.setCaretPosition(
                Textlog.TEXT_AREA.getDocument().getLength());
    }

    public static void update() {
        Textlog.updateHUD();
        map.repaint();
    }

    private static class POV extends JPanel{
        private static final int WIDTH = 700;
        private static final int HEIGHT = 400;
        private static final Point POS = new Point(520,200);
        private static final Point OFFSET = new Point(120,20);

        private POV() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
        }

        private void drawMonsters(Graphics g) {
            Image gremlin = null, skeleton = null, ogre = null;
            Room heroRoom = myModel.getRooms()
                    [myModel.getHeroLocation().getRow()]
                    [myModel.getHeroLocation().getColumn()];
            try {
                gremlin = ImageIO.read(new File("sprites/Skitter.png"));
                skeleton = ImageIO.read(new File("sprites/Crawler.png"));
                ogre = ImageIO.read(new File("sprites/predator.png"));
            } catch (IOException e) {
                System.out.println("Missing Sprites");
            }
            int count = 0;
            for (Monster monster : heroRoom.getMyMonsters()) {
                String name = monster.getMyName();
                switch (name) {
                    case "Skitter" -> g.drawImage(gremlin,
                            POS.x + OFFSET.x * count,
                            POS.y + OFFSET.y * count,
                            this);
                    case "Crawler" -> g.drawImage(skeleton,
                            POS.x + OFFSET.x * count,
                            POS.y + OFFSET.y * count,
                            this);
                    case "Predator" -> g.drawImage(ogre,
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
            Image vpot = null, hpot = null, pillar = null;
            try {
                bg = ImageIO.read(new File("sprites/Background.png"));
                hpot = ImageIO.read(new File("sprites/healpot.png"));
                vpot = ImageIO.read(new File("sprites/visionpot.png"));
                pillar = ImageIO.read(new File("sprites/pillar.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawImage(bg, 0,0, this);
//            drawMonsters(g);

        }
    }
    private static class Textlog extends JPanel{
        private static final JTextArea TEXT_AREA = getTextArea(12,60);;
        private static JLabel HUD;

        private Textlog() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
            HUD = setHUD(myModel.getHero().getMyHealth(), 0, 0, 0);
            add(HUD);
            TEXT_AREA.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
            TEXT_AREA.append("\n");
            JScrollPane scroll = new JScrollPane(TEXT_AREA);
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            add(scroll);
            add(getTextField());
        }

        private static JTextArea getTextArea(int rows, int col) {
            JTextArea textArea = new JTextArea(rows, col);
            textArea.setBackground(new Color(20,20,20));
            textArea.setForeground(Color.GREEN);
            textArea.setFont(new Font("Monospaced",Font.PLAIN ,18));
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            return textArea;
        }

        public static void updateHUD() {
            Hero hero = myModel.getHero();
            int health = hero.getMyHealth();
            int pillars = hero.getPillars().size();
            int hpots = hero.getHealingPots();
            int vpots = hero.getVisionPots();
            HUD = setHUD(health,pillars,hpots,vpots);
        }

        private static JLabel setHUD(int health, int pillars, int hpots, int vpots) {
            JLabel label = new JLabel("Health: " + health + " Pillars: " + pillars +
                    "/4 H.Pots: " + hpots + " V.Pots: " + vpots);
            label.setFont(new Font("Monospaced",Font.PLAIN ,24));
            label.setForeground(Color.GREEN);
            return label;
        }

        private static JTextField getTextField() {
            JTextField field = new JTextField(60);
            field.addActionListener(e -> {
                appendTextlog(field.getText());
                field.setText("");
            });
            return field;
        }

    }
    private static class Map extends JPanel{

        private static int myRows;
        private static int myCols;
        private static final int TILE_SIZE = 100;
        private static final Rectangle VERT_DOOR = new Rectangle(20,5);
        private static final Rectangle HORI_DOOR = new Rectangle(5,20);
        private static final Point NORTH_DOOR = new Point(40,0);
        private static final Point SOUTH_DOOR = new Point(40,TILE_SIZE - VERT_DOOR.height);
        private static final Point WEST_DOOR = new Point(0,40);
        private static final Point EAST_DOOR = new Point(TILE_SIZE - HORI_DOOR.width,40);

        private Map(int theRows, int theCol) {

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

            if (theRoom.getMyVisitedStatus()) {
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
            Image exit = null, items = null, vpot = null, hpot = null, pillar = null;
            try {
                exit = ImageIO.read(new File("sprites/exit.png"));
                items = ImageIO.read(new File("sprites/items.png"));
                vpot = ImageIO.read(new File("sprites/visionpot.png"));
                hpot = ImageIO.read(new File("sprites/healpot.png"));
                pillar = ImageIO.read(new File("sprites/pillar.png"));
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
//
//                        g.drawImage(tile, i * TILE_SIZE, j * TILE_SIZE, this);
//                        g.drawImage(explored,i * TILE_SIZE, j * TILE_SIZE, this);
//                        drawDoor(i,j,Direction.NORTH,g);
//                        drawDoor(i,j,Direction.SOUTH,g);
//                        drawDoor(i,j,Direction.EAST,g);
//                        drawDoor(i,j,Direction.WEST,g);
                    }
                }
            } catch (IOException e) {
                System.out.println("Missing sprites");
            }
        }
    }
    private static class Optionlog extends JPanel {
        private static JButton[] myButtons = new JButton[5];
        private static final JPanel MOVE = movementPanel();
        private static final JPanel DIRECTION = directionPanel();
//        private static JPanel targetsPanel = ;
        private static final JPanel ITEMS = itemPanel();

        private Optionlog() {
            setBackground(new Color(40,40,40));
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setLayout(new CardLayout());
            add(MOVE);
            add(DIRECTION);
            add(ITEMS);
        }
        private static JPanel movementPanel() {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(40,40,40));
            panel.setLayout(new GridLayout(10, 1));
            myButtons[0] = makeButton("MOVE");
            myButtons[1] = makeButton("USE ITEM");
            myButtons[2] = makeButton("HELP");
            for (int i = 0; i < 3; i++) {
                panel.add(myButtons[i]);
            }
            myButtons[0].addActionListener(e -> {
                MOVE.setVisible(false);
                DIRECTION.setVisible(true);
            });
            myButtons[1].addActionListener(e -> {
                MOVE.setVisible(false);
                ITEMS.setVisible(true);
            });
            return panel;
        }
        private static JPanel directionPanel() {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(40,40,40));
            panel.setLayout(new GridLayout(10, 1));
            myButtons[0] = makeButton("NORTH");
            myButtons[1] = makeButton("WEST");
            myButtons[2] = makeButton("EAST");
            myButtons[3] = makeButton("SOUTH");
            myButtons[4] = makeButton("<- RETURN");
            for (int i = 0; i < 5; i++) {
                panel.add(myButtons[i]);
            }
            myButtons[0].addActionListener(e -> {
                myModel.move(Direction.NORTH);
                GUIView.update();
            });
            myButtons[1].addActionListener(e -> {
                myModel.move(Direction.WEST);
                GUIView.update();
            });
            myButtons[2].addActionListener(e -> {
                myModel.move(Direction.EAST);
                GUIView.update();
            });
            myButtons[3].addActionListener(e -> {
                myModel.move(Direction.SOUTH);
                GUIView.update();
            });
            myButtons[4].addActionListener(e -> {
                MOVE.setVisible(true);
                DIRECTION.setVisible(false);
            });
            return panel;
        }
        private static JPanel itemPanel() {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(40,40,40));
            panel.setLayout(new GridLayout(10, 1));
            myButtons[0] = makeButton("USE HEALING");
            myButtons[1] = makeButton("USE VISION");
            myButtons[2] = makeButton("<- RETURN");
            for (int i = 0; i < 3; i++) {
                panel.add(myButtons[i]);
            }
            myButtons[2].addActionListener(e -> {
                ITEMS.setVisible(false);
                MOVE.setVisible(true);
            });
            return panel;
        }

        private static JButton makeButton(String theText) {
            JButton button = new JButton(theText);
            button.setBackground(Color.black);
            button.setForeground(Color.green);
            button.setFont(new Font("Monospaced",Font.PLAIN ,24));
            return button;
        }
    }
}
