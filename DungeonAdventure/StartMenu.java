package DungeonAdventure;

import javax.swing.*;
import java.awt.*;

public class StartMenu extends JFrame {
    private final JTextArea TEXT_AREA = textBox();
    private final GameModel myModel = new DefaultModel();
    private int myStage = 0;
    private String myName;
    private Hero myHero = null;

    public StartMenu() {
        setTitle("Cave Rescue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        setPreferredSize(new Dimension(700, 400));

        JPanel panel = new JPanel();
        add(panel);
        JScrollPane scroll = new JScrollPane(TEXT_AREA);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scroll);
        panel.add(textField());
        panel.setLayout(new FlowLayout());
        panel.setBackground(new Color(20, 20, 20));
        pack();
    }

    private static JTextArea textBox() {
        JTextArea textArea = new JTextArea(14, 60);
        textArea.setBackground(new Color(20, 20, 20));
        textArea.setForeground(Color.GREEN);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setText(
                """
                        >Okay, lemme get you the mission brief.
                        >We've got a mining expedition down in the caves
                         that got their comms cut, no response since.
                        >Chances are, they're in some deep s***,
                         and we need you to pull them out.
                        >...
                        >You hearing me?
                         [TYPE ANYTHING TO RESPOND]
                        """);
        return textArea;
    }

    public static void main(String[] args) {
        new StartMenu();

    }

    public void appendText(final String theText) {
        TEXT_AREA.append(theText);
        TEXT_AREA.setCaretPosition(
                TEXT_AREA.getDocument().getLength());
    }

    private JTextField textField() {
        JTextField field = new JTextField(60);
        field.addActionListener(e -> {
            switch (myStage) {
                case 0 -> {
                    appendText(">\"" + field.getText() + "\"\n");
                    appendText("""
                            >Good, now let's get your information down,
                            >What's your name?
                            """);
                    myStage++;
                }
                case 1 -> {
                    appendText(">\"" + field.getText() + "\"\n");
                    myName = field.getText();
                    appendText(">" + myName + ", huh?\n");
                    appendText("""
                            >Okay, what's your specialty then?
                             [1] Bruiser     : Crush foes with a heavy blow.
                             [2] Scout       : Strike with precision and speed.
                             [3] Survivalist : Treat any injury or wound.
                             [TYPE A NUMBER TO SELECT]
                            """);
                    myStage++;
                }
                case 2 -> {
                    try {
                        int choice = Integer.parseInt(field.getText());
                        switch (choice) {
                            case 1 -> myHero = new Warrior(myName);
                            case 2 -> myHero = new Thief(myName);
                            case 3 -> myHero = new Priestess(myName);
                            default -> appendText(" [INVALID CHOICE]\n");
                        }
                        if (myHero != null) {

                            appendText("""
                                    >Alright, I need to fill out your death insurance now-
                                    >Can't be too careful, you know.
                                    >...
                                    >How tough do you think you are?
                                     [1] Easy   : Small cave
                                     [2] Medium : Medium cave
                                     [3] Hard   : Large cave
                                     [TYPE A NUMBER TO SELECT]
                                    """);
                            myStage++;
                        }
                    } catch (NumberFormatException exception) {
                        appendText(" [INVALID CHOICE]\n");
                    }
                }
                case 3 -> {
                    try {
                        boolean proper = false;
                        int choice = Integer.parseInt(field.getText());
                        switch (choice) {
                            case 1 -> {
                                myModel.newDungeon(3, 3);
                                proper = true;
                            }
                            case 2 -> {
                                myModel.newDungeon(4, 4);
                                proper = true;
                            }
                            case 3 -> {
                                myModel.newDungeon(4, 6);
                                proper = true;
                            }
                            default -> appendText(" [INVALID CHOICE]\n");
                        }
                        if (proper) {
                            myModel.setHero(myHero);
                            appendText("""
                                    >There we go, paperworks all filled out,
                                     get your gear and get ready.
                                    >There should be a <Teleporter> down there, that's
                                     where we last saw them.
                                    >Problem is, the <Pillars> required to power it up
                                     were scattered when our crew got attacked.
                                    >Get down there, find the four pillars necessary to
                                     activate the teleporter and get them out.
                                     [TYPE ANYTHING TO BEGIN]
                                    """);
                            myStage++;
                        }
                    } catch (NumberFormatException exception) {
                        appendText(" [INVALID CHOICE]\n");
                    }
                }
                case 4 -> {
                    setVisible(false);
                    GUIView guiView = GUIView.getInstance(myModel);
                }
            }
            field.setText("");
        });
        return field;
    }

    public GameModel getMyModel() {
        if (myStage == 4) {
            return myModel;
        } else return null;
    }

}
