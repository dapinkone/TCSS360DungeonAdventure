package DungeonAdventure;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GUIView extends JFrame {
    private static Dungeon myDungeon;
    private static final Map MY_MAP = new Map(4,4);
    private static final Textlog MY_TEXTLOG = new Textlog();
    private static final POV MY_POV = new POV();
    private static final Optionlog MY_OPTIONLOG = new Optionlog();

    public GUIView(Dungeon theDungeon) {
        myDungeon = theDungeon;
        setTitle("Cave Rescue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(MY_POV);
        add(MY_MAP);
        add(MY_TEXTLOG);
        add(MY_OPTIONLOG);
        setLayout(new GridLayout(2,2));
        setResizable(false);
        setVisible(true);
        pack();
    }

    public static void main(String[] args) {
        GUIView guiView = new GUIView(null);
    }

    public static void appendTextlog(String theText) {
        Textlog.TEXT_AREA.append(" >" + theText + "\n");
        Textlog.TEXT_AREA.setCaretPosition(
                Textlog.TEXT_AREA.getDocument().getLength());
    }

    private static class POV extends JPanel{
        private static final int WIDTH = 700;
        private static final int HEIGHT = 400;
        private static final Point POS1 = new Point(520,200);
        private static final Point POS2 = new Point(400,180);
        private static final Point POS3 = new Point(280,160);

        private POV() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image bg = null;
            Image vpot = null, hpot = null, pillar = null;
            Image gremlin = null, skeleton = null, ogre = null;
            try {
                bg = ImageIO.read(new File("sprites/Background.png"));
                gremlin = ImageIO.read(new File("sprites/Skitter.png"));
                skeleton = ImageIO.read(new File("sprites/Crawler.png"));
                ogre = ImageIO.read(new File("sprites/predator.png"));
                hpot = ImageIO.read(new File("sprites/healpot.png"));
                vpot = ImageIO.read(new File("sprites/visionpot.png"));
                pillar = ImageIO.read(new File("sprites/pillar.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            g.drawImage(bg, 0,0, this);
            g.drawImage(gremlin, POS1.x,POS1.y, this);
            g.drawImage(skeleton, POS2.x,POS2.y, this);
            g.drawImage(ogre, POS3.x,POS3.y, this);

        }
    }
    private static class Textlog extends JPanel{
        private static final JTextArea TEXT_AREA = getTextArea(12,60);;
        private static JLabel HUD;

        private Textlog() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
            add(getHUD(0,0,0,0));
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
        private static JLabel getHUD(int health, int pillars, int hpots, int vpots) {
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
                if (field.getText().equals("2 1")) {
                    MY_MAP.exploreRoom(2,1);
                }
                field.setText("");
            });
            return field;
        }

    }
    private static class Map extends JPanel{

        private static int[][] exploredRooms;
        private static int myRows;
        private static int myCols;
        private static final int TILE_SIZE = 100;
        private static final Rectangle VERT_DOOR = new Rectangle(20,5);
        private static final Rectangle HORI_DOOR = new Rectangle(5,20);
        private static final Point NORTH_DOOR = new Point(40,0);
        private static final Point SOUTH_DOOR = new Point(40,TILE_SIZE - VERT_DOOR.height);
        private static final Point WEST_DOOR = new Point(0,40);
        private static final Point EAST_DOOR = new Point(TILE_SIZE - HORI_DOOR.width,40);

        private Map(int x, int y) {

            setBackground(new Color(40, 40, 40));
            setAlignmentX(CENTER_ALIGNMENT);
            myRows = x;
            myCols = y;
            exploredRooms = new int[x][y];
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    exploredRooms[i][j] = 0;
                }
            }
        }

        public void exploreRoom(int x, int y) {
            exploredRooms[x][y] = 1;
            repaint();
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
            int row = theRoom.getMyLocation().getRow();
            int col = theRoom.getMyLocation().getColumn();
            Room heroRoom = myDungeon.getRooms()
                    [myDungeon.getMyHeroLocation().getRow()]
                    [myDungeon.getMyHeroLocation().getColumn()];
            Image explored = null;
            Image tile = null;
            Image player = null;
            Image exit = null;
            try {
                explored = ImageIO.read(new File("sprites/explored.png"));
                tile = ImageIO.read(new File("sprites/tile.png"));
                player = ImageIO.read(new File("sprites/player.png"));
                exit = ImageIO.read(new File("sprites/exit.png"));
            } catch (IOException e) {
                System.out.println("Missing sprites");
            }

            g.drawImage(tile,row * TILE_SIZE, col * TILE_SIZE, this);

            if (exploredRooms[row][col] != 0) {
                g.drawImage(explored,row * TILE_SIZE, col * TILE_SIZE, this);
                for (Direction d: theRoom.getDoors()) {
                    if (theRoom.getDoor(d)) {
                        drawDoor(row, col, d, g);
                    }
                }
                if (heroRoom == theRoom) {
                    g.drawImage(player,row * TILE_SIZE, col * TILE_SIZE, this);
                } else if (theRoom.getMyItems().contains(Item.Exit)) {
                    g.drawImage(exit,row * TILE_SIZE, col * TILE_SIZE, this);
                } else if (theRoom.getMyItems().contains(Item.HealingPotion)) {

                } else if (theRoom.getMyItems().contains(Item.VisionPotion)) {

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

                        g.drawImage(tile, i * TILE_SIZE, j * TILE_SIZE, this);
//                        if (exploredRooms[i][j] != 0) {
                            g.drawImage(explored,i * TILE_SIZE, j * TILE_SIZE, this);
                            drawDoor(i,j,Direction.NORTH,g);
                        drawDoor(i,j,Direction.SOUTH,g);
                        drawDoor(i,j,Direction.EAST,g);
                        drawDoor(i,j,Direction.WEST,g);
//                            g.drawImage(player,i * TILE_SIZE, j * TILE_SIZE, this);
//                        }

                    }
                }
                g.drawImage(player, 0, 0, this);
            } catch (IOException e) {
                System.out.println("Missing sprites");
            }
        }
    }
    private static class Optionlog extends JPanel {
        private static JButton[] myButtons = new JButton[5];

        private Optionlog() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
            setLayout(new GridLayout(10, 1));
            myButtons[0] = makeButton("ATTACK");
            myButtons[1] = makeButton("MOVE");
            myButtons[2] = makeButton("USE ITEM");
            myButtons[3] = makeButton("HELP");
            myButtons[4] = makeButton("<- RETURN");
            for (JButton button : myButtons) {
                add(button);
            }

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
