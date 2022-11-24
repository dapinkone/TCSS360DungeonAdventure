package DungeonAdventure;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Combat {
    private List<Monster> myMonsters;
    private Hero myHero;
    private List<Integer> myTurnOrder;
    private int nextTurn = 0;

    public Combat(List<Monster> theMonsters, Hero theHero) {
        myMonsters = theMonsters;
        myHero = theHero;
        myTurnOrder = turnOrdering();
    }

    /**
     * Calculates the order in which turns should be taken.
     * @return List of the turns in order, represented by their index
     */
    private List<Integer> turnOrdering() {
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
        int heroSpeed = myHero.getMyAttackSpeed();
        List<SpeedNode> speeds = new LinkedList<>();
        speeds.add(new SpeedNode(0, heroSpeed));
        int index = 1;
        for (Monster monster : myMonsters) {
            speeds.add(new SpeedNode(index, monster.getMyAttackSpeed()));
            index++;
        }
        Collections.sort(speeds);
        List<Integer> turnOrderIndexes = new LinkedList<>();
        //combat loop
        int min = speeds.get(speeds.size() - 1).speed;
        while (speeds.get(0).speed > 0) {
            for (SpeedNode sn : speeds) {

                if (sn.speed > 0) {
                    turnOrderIndexes.add(sn.index);
                    sn.speed = sn.speed - min;
                }
            }
            Collections.sort(speeds);
            min = speeds.get(speeds.size() - 1).speed;
        }
        return turnOrderIndexes;
    }

    /**
     * Returns the index of the character whose turn is next that isn't dead.
     * @return The index of the character whose turn is next.
     */
    public int getNextTurn() {
        int result = myTurnOrder.get(nextTurn);
        while (myMonsters.get(result - 1).isDead()) { //Don't need to check hero.
            nextTurn++;
            if (nextTurn >= myTurnOrder.size()) {
                nextTurn = 0;
            }
            result = myTurnOrder.get(nextTurn);
        }
        nextTurn++;
        if (nextTurn >= myTurnOrder.size()) {
            nextTurn = 0;
        }
        return result;
    }

    /**
     * Monster attacks the hero.
     * @param theMonster Monster whose turn it is.
     * @return result of the turn.
     */
    public int monsterTurn(Monster theMonster) {
        return theMonster.attack(myHero);
    }

    /**
     * The method for calculating the Hero's turn.
     * @param theOption Which option the hero chooses on it's turn.
     * @param theTargetIndex The character to be targeted by the hero.
     * @return int representing the result.
     */
    public int heroTurn(int theOption, int theTargetIndex) {
        int result = 0;
        if (theOption == 1) { //ATTACK
            result = myHero.attack(myMonsters.get(theTargetIndex));
        } else if (theOption == 2) { //SPECIAL SKILL
            result = heroSpecial(theTargetIndex);
        } else if (theOption == 3) { //USE ITEMS
            result = myHero.useHealingPot();
        }
        return result;
    }

    /**
     * Method that performs each hero's special move.
     * @param theTargetIndex the target of the special move should there be one
     * @return int containing the result of the move.
     */
    private int heroSpecial(int theTargetIndex) {
        int result = myHero.specialSkill();
        String heroClass = myHero.getMyClass();

        if (heroClass.equals("Bruiser")) {
            Monster target = myMonsters.get(theTargetIndex);
            target.setMyHealth(target.getMyHealth() - result);
        }
        if (heroClass.equals("Scout")) {
            Monster target = myMonsters.get(theTargetIndex);
            if (result == 1) { //normal
                result = myHero.attack(target);
            } if (result == 2) { //crit
                result = myHero.attack(target);
                result += myHero.attack(target);
            }
        }
        return result;
    }

    /**
     * Should be called whenever a monster is hurt. Monster tries to heal, or dies
     * if they are below 0 health.
     * @return int of how much they heal.
     */
    public int monsterHurt(Monster theMonster) {
        if (!theMonster.isDead()) {
            return theMonster.tryToHeal();
        } else return 0;
    }

    /**
     * Checks whether the combat is over.
     * @return boolean of if the combat is over.
     */
    public boolean isOver() {
        if (myHero.isDead()) return true;
        boolean allDead = true;
        for (Monster monster : myMonsters) {
            if (!monster.isDead()) {
                allDead = false;
            }
        }
        return allDead;
    }

    public List<Monster> getMonsters() {
        return myMonsters;
    }
}
