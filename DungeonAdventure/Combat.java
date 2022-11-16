package DungeonAdventure;

import java.util.List;
import java.util.Scanner;

public class Combat {
    static final Scanner SCANNER = new Scanner(System.in);
    private static List<Monster> monsters;
    private static Hero hero;

    /**
     * Constructor, takes all combatants as parameter.
     * @param theMonsters The list of monsters that you fight.
     * @param theHero The hero.
     */
    public Combat(final List<Monster> theMonsters, final Hero theHero) {
        monsters = theMonsters;
        hero = theHero;
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

        int count = 0;
        int heroSpeed = hero.getMyAttackSpeed();

        //combat loop
        while(hero.getMyHealth() > 0 && !monsters.isEmpty()) {
            count++;
            //Hero's turn
            if (count % heroSpeed == 0) {
                heroTurn(monsters, hero);
            }
            //Monsters attack
            for (Monster monster : monsters) {
                if (count % monster.getMyAttackSpeed() == 0) {
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
                    System.out.println(combatStatus());
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

    private void heroTurn(List<Monster> theMonsters, Hero theHero) {
        int option = getHeroOption();
        Monster target;

        if (option == 1) {
            int index = 0;
            //CHOOSE TARGET IF MULTIPLE
            if (theMonsters.size() > 1) {
                boolean valid = false;
                while (!valid) {
                    //TODO: PROMPT USER FOR WHICH MONSTER TO ATTACK (1, 2, 3)
                    //Read input from user
                    System.out.println("Choose target: ");
                    System.out.print("> ");
                    index = SCANNER.nextInt();

                    System.out.println("-----------------");
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
            System.out.println(theHero.getMyName() + " attacks the " + target.getMyName());
            int result = theHero.attack(target);
            //TODO: DISPLAY RESULT IN CLI
            if (result == 0) {
                System.out.println("Missed!");
            } else {
                System.out.println("Dealt " + result + " damage");
            }
            System.out.println();
            System.out.println(combatStatus());

            if (target.getMyHealth() <= 0) {
                theMonsters.remove(index);
                //TODO: DISPLAY MONSTER DEATH
                System.out.println(target.getMyName() + " falls to the dirt");
            }
        } else if (option == 2) {
            //TODO: SPECIAL SKILL
        } else if (option == 3) {
            //TODO: USE ITEM
        }
    }

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
    private String combatStatus() {
        StringBuilder result = new StringBuilder();
        result.append("Your health: " + hero.getMyHealth() + "\n");
        for (Monster monster : monsters) {
            result.append(monster.getMyName() + " health: " + monster.getMyHealth() + "\n");
        }
        return (result.toString());
    }
}
