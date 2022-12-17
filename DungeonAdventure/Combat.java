package DungeonAdventure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class Combat {
    private final List<Monster> myMonsters;
    private final Hero myHero;
    private final List<Integer> myTurnOrder;
    private final List<DungeonCharacter> myCombatants = new ArrayList<>();
    private int myNextTurn;

    public Combat(final List<Monster> theMonsters, final Hero theHero) {
        myMonsters = theMonsters;
        myHero = theHero;
        myCombatants.add(theHero);
        myCombatants.addAll(myMonsters);

        myTurnOrder = turnOrdering();
        progress(); // process monster turns that go before the hero.
    }

    /**
     * Calculates the order in which turns should be taken.
     *
     * @return List of turns in order, represented by their index in
     * the list of combatants.
     */
    private List<Integer> turnOrdering() {
        final class SpeedNode implements Comparable<SpeedNode> {
            final private int myIndex;
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

        final List<SpeedNode> speeds = new LinkedList<>();
        int index = 0;
        for (DungeonCharacter combatant : myCombatants) {
            speeds.add(new SpeedNode(index++, combatant.getMyAttackSpeed()));
        }
        Collections.sort(speeds);
        List<Integer> turnOrderIndexes = new LinkedList<>();
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
    private int getNextTurn() {
        final int result = myTurnOrder.get(myNextTurn);
        myNextTurn++;
        if (myNextTurn >= myTurnOrder.size()) {
            myNextTurn = 0;
        }
        return result;
    }

    private DungeonCharacter getNextCombatant() {
        return myCombatants.get(getNextTurn());
    }

    /**
     * The method for calculating the Hero's turn.
     *
     * @param theOption      Which option the hero chooses on it's turn.
     * @param theTargetIndex The character to be targeted by the hero.
     */
    public void heroTurn(final int theOption, final int theTargetIndex) {
        switch (theOption) {
            case 1 ->  //ATTACK
                    myHero.attack(myMonsters.get(theTargetIndex));
            case 2 ->  //SPECIAL SKILL
                    heroSpecial(theTargetIndex);
            case 3 ->  //USE ITEMS
                    myHero.useHealingPot();
            default -> throw new NoSuchElementException();
        }
        progress();
    }


    /**
     * Method that performs each hero's special move.
     *
     * @param theTargetIndex the target of the special move should there be one
     */
    private void heroSpecial(int theTargetIndex) {
        myHero.specialSkill(myMonsters.get(theTargetIndex));
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
        return myMonsters.stream().allMatch(Monster::isDead);
    }

    public List<Monster> getMonsters() {
        return myMonsters;
    }

    /**
     * Takes monster turns until it's the hero's turn.
     */
    private void progress() {
        for (var monster = getNextCombatant(); monster != myHero;
             monster = getNextCombatant()
        ) {
            // monster's turn to attack.
            monster.attack(myHero);
        }
    }
}
