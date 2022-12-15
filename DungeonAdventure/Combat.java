package DungeonAdventure;

import java.util.*;

public class Combat {
    private final List<Monster> myMonsters;
    private final Hero myHero;
    private final List<Integer> myTurnOrder;
    final private List<DungeonCharacter> combatants = new ArrayList<>();
    private int nextTurn = 0;

    public Combat(List<Monster> theMonsters, Hero theHero) {
        myMonsters = theMonsters;
        myHero = theHero;
        combatants.add(theHero);
        combatants.addAll(myMonsters);

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

        List<SpeedNode> speeds = new LinkedList<>();
        int index = 0;
        for (DungeonCharacter theCombatant : combatants) {
            speeds.add(new SpeedNode(index++, theCombatant.getMyAttackSpeed()));
        }
        Collections.sort(speeds);
        List<Integer> turnOrderIndexes = new LinkedList<>();
        int min = speeds.get(speeds.size() - 1).speed;

        //combat loop
        int max = speeds.get(0).speed;
        while (max > 0) {
            for (SpeedNode sn : speeds) {

                if (sn.speed > 0) {
                    turnOrderIndexes.add(sn.index);
                    sn.speed = sn.speed - min;
                }
            }
            Collections.sort(speeds);
            max = speeds.get(0).speed;
        }
        return turnOrderIndexes;
    }

    /**
     * Returns the index of the character whose turn is next.
     *
     * @return The index of the character whose turn is next.
     */
    private int getNextTurn() {
        int result = myTurnOrder.get(nextTurn);
        nextTurn++;
        if (nextTurn >= myTurnOrder.size()) {
            nextTurn = 0;
        }
        return result;
    }

    private DungeonCharacter getNextCombatant() {
        return combatants.get(getNextTurn());
    }

    /**
     * The method for calculating the Hero's turn.
     *
     * @param theOption      Which option the hero chooses on it's turn.
     * @param theTargetIndex The character to be targeted by the hero.
     * @return int representing the result.
     */
    public void heroTurn(int theOption, int theTargetIndex) {
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
     * @return int containing the result of the move.
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
        if (myHero.isDead()) return true;
        return myMonsters.stream().allMatch(Monster::isDead);
    }

    public List<Monster> getMonsters() {
        return myMonsters;
    }

    /**
     * Takes monster turns until it's the hero's turn.
     */
    private void progress() {
        for (var monster = getNextCombatant(); monster != myHero; monster = getNextCombatant()) {
            // monster's turn to attack.
            monster.attack(myHero);
        }
    }
}
