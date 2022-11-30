package DungeonAdventure;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GUIView {
    private static final Map MY_MAP = new Map(4,4);
    private static final Textlog MY_TEXTLOG = new Textlog();
    private static final POV MY_POV = new POV();
    private static final Optionlog MY_OPTIONLOG = new Optionlog();

    public static void main(String[] args) {
        JFrame window = new JFrame("Cave Rescue");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.add(MY_POV);
        window.add(MY_MAP);
        window.add(MY_TEXTLOG);
        window.add(MY_OPTIONLOG);
        window.setLayout(new GridLayout(2,2));
        window.setResizable(false);
        window.setVisible(true);
        window.pack();
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
        private static Image skeleton;
        private static Image gremlin;
        private static Image ogre;


        private POV() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
            try {
                gremlin = ImageIO.read(new File("sprites/Skitter.png"));
                skeleton = ImageIO.read(new File("sprites/Crawler.png"));
                ogre = ImageIO.read(new File("sprites/predator.png"));
            } catch (IOException e) {
                e.printStackTrace();
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
            g.drawImage(gremlin, POS1.x,POS1.y, this);
            g.drawImage(skeleton, POS2.x,POS2.y, this);
            g.drawImage(ogre, POS3.x,POS3.y, this);

        }
    }
    private static class Textlog extends JPanel{
        private static final JTextArea TEXT_AREA = getTextArea(20,60);;
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
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            return textArea;
        }
        private static JLabel getHUD(int health, int pillars, int hpots, int vpots) {
            JLabel label = new JLabel("Health: " + health + " Pillars: " + pillars +
                    " H.Pots: " + hpots + " V.Pots: " + vpots);
            label.setFont(Font.getFont(Font.MONOSPACED));
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
        //        private static final Room[][] myDungeon;
        private static int[][] exploredRooms;
        private static int myRows;
        private static int myCols;
        private static final int TILE_SIZE = 100;
        private static final Rectangle VERT_DOOR = new Rectangle(20,5);
        private static final Rectangle HORI_DOOR = new Rectangle(5,20);
        private static final Point UP = new Point(40,0);
        private static final Point DOWN = new Point(40,TILE_SIZE - VERT_DOOR.height);
        private static final Point LEFT = new Point(0,40);
        private static final Point RIGHT = new Point(TILE_SIZE - HORI_DOOR.width,40);

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

        private void drawDoor(int theRow, int theCol, char direction, Graphics g) {
            try {
                Image hori_door = ImageIO.read(new File("sprites/hori_door.png"));
                Image vert_door = ImageIO.read(new File("sprites/vert_door.png"));
                int row = theRow * TILE_SIZE;
                int col = theCol * TILE_SIZE;
                switch (direction) {
                    case 'N' -> g.drawImage(hori_door, row + UP.x, col + UP.y, this);
                    case 'S' -> g.drawImage(hori_door, row + DOWN.x, col + DOWN.y, this);
                    case 'E' -> g.drawImage(vert_door, row + RIGHT.x, col + RIGHT.y, this);
                    case 'W' -> g.drawImage(vert_door, row + LEFT.x, col + LEFT.y, this);
                }
            } catch (IOException e) {
                System.out.println("Missing sprites");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                Image explored = ImageIO.read(new File("sprites/explored.png"));
                Image tile = ImageIO.read(new File("sprites/tile.png"));
                for (int i = 0; i < myRows; i++) {
                    for (int j = 0; j < myCols; j++) {
                        g.drawImage(tile, i * TILE_SIZE, j * TILE_SIZE, this);
                        if (exploredRooms[i][j] != 0) {
                            g.drawImage(explored,i * TILE_SIZE, j * TILE_SIZE, this);
                            drawDoor(i,j,'N',g);
                            drawDoor(i,j,'W',g);
                            drawDoor(i,j,'E',g);
                            drawDoor(i,j,'S',g);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Missing sprites");
            }
        }
    }
    private static class Optionlog extends JPanel{

        private Optionlog() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
        }
    }
}
