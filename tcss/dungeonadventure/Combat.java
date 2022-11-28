package tcss.dungeonadventure;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class Combat {
    private final List<Monster> myMonsters;
    private final Hero myHero;
    private final List<Integer> myTurnOrder;
    private int myNextTurn;

    public Combat(final List<Monster> theMonsters, final Hero theHero) {
        myMonsters = theMonsters;
        myHero = theHero;
        myTurnOrder = turnOrdering();
    }

    /**
     * Calculates the order in which turns should be taken.
     *
     * @return List of the turns in order, represented by their index
     */
    private List<Integer> turnOrdering() {
        final class SpeedNode implements Comparable<SpeedNode> {
            private final int myIndex;
            private int mySpeed;

            private SpeedNode(final int theIndex, final int theSpeed) {
                myIndex = theIndex;
                mySpeed = theSpeed;
            }

            @Override
            public int compareTo(final SpeedNode theOther) {
                return theOther.mySpeed - this.mySpeed;
            }
        }
        final int heroSpeed = myHero.getMyAttackSpeed();
        final List<SpeedNode> speeds = new LinkedList<>();
        speeds.add(new SpeedNode(0, heroSpeed));
        int index = 1;
        for (Monster monster : myMonsters) {
            speeds.add(new SpeedNode(index, monster.getMyAttackSpeed()));
            index++;
        }
        Collections.sort(speeds);
        final List<Integer> turnOrderIndexes = new LinkedList<>();
        final int min = speeds.get(speeds.size() - 1).mySpeed;

        //combat loop
        int max = speeds.get(0).mySpeed;
        while (max > 0) {
            for (SpeedNode sn : speeds) {

                if (sn.mySpeed > 0) {
                    turnOrderIndexes.add(sn.myIndex);
                    sn.mySpeed = sn.mySpeed - min;
                }
            }
            Collections.sort(speeds);
            max = speeds.get(0).mySpeed;
        }
        return turnOrderIndexes;
    }

    /**
     * Returns the index of the character whose turn is next.
     *
     * @return The index of the character whose turn is next.
     */
    public int getMyNextTurn() {
        final int result = myTurnOrder.get(myNextTurn);
        myNextTurn++;
        if (myNextTurn >= myTurnOrder.size()) {
            myNextTurn = 0;
        }
        return result;
    }

    /**
     * Monster attacks the hero.
     *
     * @param theMonster Monster whose turn it is.
     * @return result of the turn.
     */
    public int monsterTurn(final Monster theMonster) {
        return theMonster.attack(myHero);
    }

    /**
     * The method for calculating the Hero's turn.
     *
     * @param theOption      Which option the hero chooses on it's turn.
     * @param theTargetIndex The character to be targeted by the hero.
     * @return int representing the result.
     */
    public int heroTurn(final int theOption, final int theTargetIndex) {
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
     *
     * @param theTargetIndex the target of the special move should there be one
     * @return int containing the result of the move.
     */
    private int heroSpecial(final int theTargetIndex) {
        int result = myHero.specialSkill();
        final String heroClass = myHero.getMyClass();

        if ("Bruiser".equals(heroClass)) {
            final Monster target = myMonsters.get(theTargetIndex);
            target.setMyHealth(target.getMyHealth() - result);
        }
        if ("Scout".equals(heroClass)) {
            final Monster target = myMonsters.get(theTargetIndex);
            if (result == 1) { //normal
                result = myHero.attack(target);
            }
            if (result == 2) { //crit
                result = myHero.attack(target);
                result += myHero.attack(target);
            }
        }
        return result;
    }

    /**
     * Should be called whenever a monster is hurt.
     * The monster tries to heal, or dies.
     *
     * @param theMonster monster being targeted
     *                   if they are below 0 health.
     * @return int of how much they heal.
     */
    public int monsterHurt(final Monster theMonster) {
        if (!theMonster.isDead()) {
            return theMonster.tryToHeal();
        } else {
            return 0;
        }
    }

    /**
     * Checks whether the combat is over.
     *
     * @return boolean of if the combat is over.
     */
    public boolean isOver() {
        if (myHero.isDead()) {
            return true;
        }
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
