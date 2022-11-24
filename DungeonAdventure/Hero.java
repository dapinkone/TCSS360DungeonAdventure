package DungeonAdventure;

import java.util.LinkedList;
import java.util.List;

public abstract class Hero extends DungeonCharacter {
    private int healingPots;
    private int visionPots;
    private final String myClass;
    private List<String> pillars = new LinkedList<>();

    public Hero(String theClass, String theName, int theHealth, int theAttackSpeed, double theHitChance,
                int theMinDmg, int theMaxDmg,
                double theDodgeChance) {

        super(theName, theHealth, theAttackSpeed, theHitChance, theMinDmg, theMaxDmg);
        myClass = theClass;
        //Sets dodge chance which is specific to heroes.
        setMyDodgeChance(theDodgeChance);
    }

    /**
     * Class's special skill to be initialized in child classes.
     * @return int indicating the success.
     */
    public abstract int specialSkill();

    /**
     * Use a "Healing Tonic," restores 40 health but value can be changed.
     * @return
     */
    public int useHealingPot() {
        if (healingPots == 0) return 0;
        int healing = 40;
        if (getMyHealth() + healing > getMyMaxHealth()) {
            setMyHealth(getMyMaxHealth());
        } else {
            setMyHealth(getMyMaxHealth() + healing);
        }
        healingPots--;
        return healing;
    }

    public int useVisionPot() {
        if (getVisionPots() == 0) return 0;
        //How would this be implemented, if not in the driver?
        return 1;
    }

    /**
     * Adds a new pillar to the hero's inventory
     * @param thePillar The pillar to be added.
     */
    public void addNewPillar(String thePillar) {
        pillars.add(thePillar);
    }

    public List<String> getPillars() {
        return pillars;
    }

    public int getHealingPots() {
        return healingPots;
    }

    public void setHealingPots(int healingPots) {
        this.healingPots = healingPots;
    }

    public int getVisionPots() {
        return visionPots;
    }

    public void setVisionPots(int visionPots) {
        this.visionPots = visionPots;
    }

    public String getMyClass() {
        return myClass;
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("Name: " + getMyName() + " the " + getMyClass());
        string.append("\nHealth: " + getMyHealth() + "/" + getMyMaxHealth());
        string.append("\nHealing Tonics: " + getHealingPots());
        string.append("\nSonar Devices: " + getVisionPots());
        string.append("\nParts found: ");
        for (String s : pillars) {
            string.append(s + " ");
        }
        return String.valueOf(string);
    }
}
