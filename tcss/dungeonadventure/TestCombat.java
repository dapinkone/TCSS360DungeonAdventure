package tcss.dungeonadventure;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import java.util.Scanner;

public class TestCombat {
    static final Scanner SCANNER = new Scanner(System.in);
    private static List<Monster> monsters;
    private static Hero hero;

    /**
     * Constructor, takes all combatants as parameter.
     * @param theMonsters The list of monsters that you fight.
     * @param theHero The hero.
     */
    public TestCombat(final List<Monster> theMonsters, final Hero theHero) {
        monsters = theMonsters;
        hero = theHero;
    }

    /**
     * SpeedNode object for use in the doCombat method to determine turn orders.
     */
    class SpeedNode implements Comparable<SpeedNode> {
        int index = 0;
        int speed = 0;

        private SpeedNode(final int theIndex, final int theSpeed) {
            index = theIndex;
            speed = theSpeed;
        }

        @Override
        public int compareTo(SpeedNode o) {
            return o.speed - this.speed;
        }
    }

    /**
     * Driver method for combat.
     * @return Boolean of whether the Hero won or not.
     */
    public boolean doCombat() {
        System.out.print("Entering combat with ");
        for (Monster m : monsters) {
            System.out.print(m.getMyName() + " ");
        }
        System.out.println();
        System.out.println(combatStatus());

        int heroSpeed = hero.getMyAttackSpeed();
        List<SpeedNode> turnOrder = new LinkedList<>();
        turnOrder.add(new SpeedNode(0, heroSpeed));
        int index = 1;
        for (Monster monster : monsters) {
            turnOrder.add(new SpeedNode(index, monster.getMyAttackSpeed()));
            index++;
        }
        Collections.sort(turnOrder);
        List<SpeedNode> copy = new LinkedList<>();
        for (SpeedNode sn : turnOrder) {
            copy.add(new SpeedNode(sn.index, sn.speed));
        }
        //combat loop
        int min = turnOrder.get(turnOrder.size() - 1).speed;
        while(hero.getMyHealth() > 0 && !monsters.isEmpty()) {

            for (SpeedNode sn : copy) {

                if (sn.speed > 0) {
                    if (sn.index == 0) {
                        heroTurn();
                    } else {
                        if (monsters.size() >= sn.index) {
                            monsterTurn(monsters.get(sn.index - 1));
                        }
                    }
                    System.out.println(combatStatus());
                    if (hero.getMyHealth() <= 0 || monsters.isEmpty()) {
                        break;
                    }
                    sn.speed = sn.speed - min;
                }
            }
            Collections.sort(copy);
            if (copy.get(0).speed < min) {
                copy.clear();
                for (SpeedNode sn : turnOrder) {
                    copy.add(new SpeedNode(sn.index, sn.speed));
                }
            }
        }
        if (hero.getMyHealth() <= 0) {
            //TODO: DISPLAY GAME OVER
            System.out.println("You lose.");
            return false;
        } else {
            //TODO: DISPLAY VICTORY
            System.out.println("You win!");
            return true;
        }
    }

    /**
     * Perform's monster's attack for their turn.
     * @param monster The monster that is attacking.
     */
    private void monsterTurn(Monster monster) {
        //TODO: DISPLAY MONSTER TURN
        System.out.println(monster.getMyName() + " attacks!");
        int result = monster.attack(hero);

        //TODO: DISPLAY RESULT IN CLI
        if (result == 0) {
            System.out.println("It misses!");
        } else if (result == -1) {
            System.out.println(hero.getMyName() + " dodges!");
        } else {
            System.out.println(monster.getMyName() + " attacks for " + result + " damage.");
        }
        System.out.println();

    }

    /**
     * Should be called everytime a monster takes damage, checks to see if it died and also
     * if it successfully attempts to heal itself.
     * @param targetIndex the index of the monster
     */
    private void monsterHurt(int targetIndex) {
        Monster target = monsters.get(targetIndex);
        if (target.getMyHealth() <= 0) {
            monsters.remove(targetIndex);
            //TODO: DISPLAY MONSTER DEATH
            System.out.println(target.getMyName() + " falls to the dirt");
        } else { //Monster survives and tries to heal
            int result = target.tryToHeal();
            if (result != 0) {
                System.out.println(target.getMyName() + " heals itself for " + result + " health.");
            }
        }
    }

    /**
     * Driver for the player to choose the options for the hero to perform on their turn.
     */
    private void heroTurn() {
        int option = getHeroOption();
        if (option == 1) {
            heroAttack();
        } else if (option == 2) {
            heroSpecial();
        } else if (option == 3) {
            if (hero.getMyHealingPots() > 0) {
                int heal = hero.useHealingPot();
                System.out.println(hero.getMyName() + " drinks a potion and heals " + heal + " health.");
            }
        }
    }

    /**
     * Hero chooses which target to attack if applicable and attacks them.
     */
    private void heroAttack() {
        //CHOOSE TARGET IF MULTIPLE
        int targetIndex = getTargetIndex();
        Monster target = monsters.get(targetIndex);

        System.out.println(hero.getMyName() + " attacks the " + target.getMyName());
        int result = hero.attack(target);
        //TODO: DISPLAY RESULT IN CLI
        if (result == 0) {
            System.out.println("Missed!");
        } else {
            System.out.println("Dealt " + result + " damage");
            monsterHurt(targetIndex);
        }
        System.out.println();
    }

    /**
     * Hero performs their special move, with values given from the hero's specialSkill method.
     */
    private void heroSpecial() {
        int result = hero.specialSkill();
        String heroClass = hero.getMyClass();

        if (heroClass.equals("Bruiser")) {
            int targetIndex = getTargetIndex();
            Monster target = monsters.get(targetIndex);
            System.out.println(hero.getMyName() + " readies their weapon...");
            if (result == 0) {
                System.out.println("They missed!");
            } else {
                System.out.println("Crushes " + target.getMyName() + " for " + result + " damage");
                target.setMyHealth(target.getMyHealth() - result);
                monsterHurt(targetIndex);
            }
            System.out.println();
        }

        if (heroClass.equals("Scout")) {
            System.out.println(hero.getMyName() + " prepares to flank...");
            if (result == 0) {
                System.out.println("They fail!");
            } if (result == 1) {
                heroAttack();
            } if (result == 2) {
                heroAttack();
                System.out.println("One more!");
                heroAttack();
            }
        }

        if (heroClass.equals("Survivalist")) {
            System.out.println(hero.getMyName() + " treats their wounds...");
            System.out.println("They heal for " + result + "health.");
        }
    }

    /**
     * Prompts the user to choose their target.
     * @return The index of the target, returns 0 if only one enemy.
     */
    private int getTargetIndex() {
        int targetIndex = 0;
        if (monsters.size() > 1) {
            boolean valid = false;
            while (!valid) {
                //TODO: PROMPT USER FOR WHICH MONSTER TO ATTACK (1, 2, 3)
                //Read input from user
                System.out.println("Choose target: ");
                System.out.print("> ");
                targetIndex = SCANNER.nextInt();
                System.out.println("-----------------");
                //If in bounds
                if (targetIndex > 0 && targetIndex <= monsters.size()) {
                    valid = true;
                } else {
                    //TODO: PROMPT FOR CORRECT INPUT
                }
            }
            targetIndex--;
        }
        return targetIndex;
    }

    /**
     * Gets what option the hero wants.
     * @return int value of which option to do in combat.
     */
    private int getHeroOption() {
        int option = 0;
        boolean ok = false;
        while (!ok) {
            //TODO: WHAT ACTION
            System.out.println("Choose what to do:");
            System.out.println("1. Attack");
            System.out.println("2. Special Skill");
            System.out.println("3. Use item");
            System.out.print("> ");
            option = SCANNER.nextInt();
            System.out.println("-----------------");
            if (option > 0 && option <= 3) ok = true;
        }
        return option;
    }

    /**
     * Returns the current status of the combat. Should be called in between turns
     * @return String value of the combat status.
     */
    private String combatStatus() {
        StringBuilder result = new StringBuilder();
        result.append("Your health: " + hero.getMyHealth() + "\n");
        for (Monster monster : monsters) {
            result.append(monster.getMyName() + " health: " + monster.getMyHealth() + "\n");
        }
        return (result.toString());
    }
}
