package tcss.dungeonadventure;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public abstract class Hero extends AbstractDungeonCharacter implements Serializable {
    private final String myClass;
    private int myHealingPots;
    private int myVisionPots;
    private final List<String> myPillars = new LinkedList<>();

    public Hero(
            final String theClass,
            final String theName,
            final int theHealth,
            final int theAttackSpeed,
            final double theHitChance,
            final int theMinDmg,
            final int theMaxDmg,
            final double theDodgeChance
    ) {

        super(theName, theHealth, theAttackSpeed, theHitChance, theMinDmg, theMaxDmg);
        myClass = theClass;
        //Sets dodge chance which is specific to heroes.
        setMyDodgeChance(theDodgeChance);
    }

    /**
     * Class's special skill to be initialized in child classes.
     *
     * @return int indicating the success.
     */
    public abstract int specialSkill();

    /**
     * Use a "Healing Tonic," restores 40 health but value can be changed.
     *
     * @return returns the quantity of healing done
     */
    public int useHealingPot() {
        if (myHealingPots == 0) { return 0; }
        final int healing = 40;
        if (getMyHealth() + healing > getMyMaxHealth()) {
            setMyHealth(getMyMaxHealth());
        } else {
            setMyHealth(getMyMaxHealth() + healing);
        }
        myHealingPots--;
        return healing;
    }

    public int useVisionPot() {
        if (getMyVisionPots() == 0) {
            return 0;
        }
        //How would this be implemented, if not in the driver?
        return 1;
    }

    /**
     * Adds a new pillar to the hero's inventory
     *
     * @param thePillar The pillar to be added.
     */
    public void addNewPillar(final String thePillar) {
        myPillars.add(thePillar);
    }

    public List<String> getMyPillars() {
        return myPillars;
    }

    public int getMyHealingPots() {
        return myHealingPots;
    }

    public void setMyHealingPots(final int theHealingPots) {
        myHealingPots = theHealingPots;
    }

    public int getMyVisionPots() {
        return myVisionPots;
    }

    public void setMyVisionPots(final int theVisionPots) {
        this.myVisionPots = theVisionPots;
    }

    public String getMyClass() {
        return myClass;
    }

    public String toString() {
        final StringBuilder string = new StringBuilder();
        string.append("Name: " + getMyName() + " the " + getMyClass());
        string.append("\nHealth: " + getMyHealth() + "/" + getMyMaxHealth());
        string.append("\nHealing Tonics: " + getMyHealingPots());
        string.append("\nSonar Devices: " + getMyVisionPots());
        string.append("\nParts found: ");
        for (String s : myPillars) {
            string.append(s + " ");
        }
        return String.valueOf(string);
    }
}
