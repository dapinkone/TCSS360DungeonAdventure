package DungeonAdventure;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class GUIView {

    public static void main(String[] args) {
        JFrame window = new JFrame("Cave Rescue");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.add(new POV());
        window.add(new Map());
        window.add(new Textlog());
        window.add(new Optionlog());
        window.setLayout(new GridLayout(2,2));
        window.setBackground(new Color(20,20,20));
        window.setResizable(false);
        window.setVisible(true);
        window.pack();
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
                gremlin = ImageIO.read(new File("Skitter.png"));
                skeleton = ImageIO.read(new File("Crawler.png"));
                ogre = ImageIO.read(new File("predator.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image bg = null;
            try {
                bg = ImageIO.read(new File("Background.png"));
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
        private static final int WIDTH = 700;
        private static final int HEIGHT = 300;
        private static final JTextArea TEXT_AREA = getTextArea(20,60);;
        private static JLabel HUD;

        private Textlog() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
            add(getHUD(0,0,0,0));
            TEXT_AREA.setEditable(false);
            TEXT_AREA.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
            TEXT_AREA.append("\n");
            add(TEXT_AREA);
            add(getTextField());
        }

        private static JTextArea getTextArea(int rows, int col) {
            JTextArea textArea = new JTextArea(rows, col);
            textArea.setBackground(new Color(20,20,20));
            textArea.setForeground(Color.GREEN);
            textArea.setLineWrap(true);
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
                TEXT_AREA.append(" >" + field.getText() + "\n");
                field.setText("");
            });
            return field;
        }

    }
    private static class Map extends JPanel{
        private static final int WIDTH = 400;
        private static final int HEIGHT = 400;
//        private static final Room[][] myDungeon;
        private static char[][] myMap;

        private Map() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(2, 51, 22));

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

        }
    }
    private static class Optionlog extends JPanel{
        private static final int WIDTH = 400;
        private static final int HEIGHT = 400;

        private Optionlog() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(40,40,40));
        }

    }
}
