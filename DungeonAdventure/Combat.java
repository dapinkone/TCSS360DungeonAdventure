package DungeonAdventure;

import java.util.List;
import java.util.Scanner;

public class Combat {
    static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Driver method for combat.
     * @param theMonsters The list of monsters involved in combat.
     * @param theHero The hero
     * @return Boolean of whether the Hero won or not.
     */
    public boolean doCombat(final List<Monster> theMonsters, final Hero theHero) {
        int count = 0;
        int heroSpeed = theHero.getMyAttackSpeed();

        //combat loop
        while(theHero.getMyHealth() > 0 && !theMonsters.isEmpty()) {
            count++;
            //Hero Attacks
            if (count % heroSpeed == 0) {
                Monster target;
                int index = 0;
                //CHOOSE TARGET IF MULTIPLE
                if (theMonsters.size() > 1) {
                    boolean valid = false;
                    while (!valid) {
                        //TODO: PROMPT USER FOR WHICH MONSTER TO ATTACK (1, 2, 3)
                        //Read input from user
                        index = SCANNER.nextInt();
                        //If in bounds
                        if (index > 0 && index <= theMonsters.size()) {
                            valid = true;
                        } else {
                            //TODO: PROMPT FOR CORRECT INPUT
                        }
                    }
                    target = theMonsters.get(index - 1);
                } else {
                    target = theMonsters.get(0);
                }
                int result = theHero.attack(target);
                //TODO: DISPLAY RESULT IN CLI

                if (target.getMyHealth() <= 0) {
                    theMonsters.remove(index);
                }
                //TODO: DISPLAY MONSTER DEATH
            }
            //Monsters attack
            for (Monster monster : theMonsters) {
                if (count % monster.getMyAttackSpeed() == 0) {
                    //TODO: DISPLAY MONSTER TURN
                    int result = monster.attack(theHero);
                    //TODO: DISPLAY RESULT IN CLI
                }
            }
        }
        if (theHero.getMyHealth() <= 0) {
            //TODO: DISPLAY GAME OVER
            return false;
        } else {
            //TODO: DISPLAY VICTORY
            return true;
        }
    }
}
