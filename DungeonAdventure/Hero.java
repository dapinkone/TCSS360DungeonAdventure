package DungeonAdventure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Hero extends DungeonCharacter implements Serializable {

    private final HashMap<Item, Integer> myInventory = new HashMap<>();
//    private int healingPots;
//    private int visionPots;
    private final String myClass;
 //   private List<String> pillars = new LinkedList<>();

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
    public HashMap<Item, Integer> getMyInventory() {
        return myInventory;
    }
    /**
     * Use a "Healing Tonic," restores 40 health but value can be changed.
     * @return
     */
    public int useHealingPot() {
        //if (healingPots == 0) return 0;
        final var quantity = myInventory.getOrDefault(Item.HealingPotion,0);
        if(quantity == 0) return 0;
        int healing = 40;
        if (getMyHealth() + healing > getMyMaxHealth()) {
            setMyHealth(getMyMaxHealth());
        } else {
            setMyHealth(getMyMaxHealth() + healing);
        }
        myInventory.put(Item.HealingPotion, quantity - 1);
        //healingPots--;
        return healing;
    }

    public int useVisionPot() {
        if (getVisionPots() == 0) return 0;
        //How would this be implemented, if not in the driver?
        return 1;
    }

//    /**
//     * Adds a new pillar to the hero's inventory
//     * @param thePillar The pillar to be added.
//     */
//    public void addNewPillar(String thePillar) {
//        pillars.add(thePillar);
//    }

    //public List<String> getPillars() {
//        return pillars;
//    }
    public boolean hasAllPillars() {
        return myInventory.keySet().stream().filter(
                x -> x.name().contains("Pillar")
                        && myInventory.get(x) == 1).count() == 4;
    }
    public int getHealingPots() {
        return myInventory.getOrDefault(Item.HealingPotion,0);
    }

    public void setHealingPots(int theHealingPots) {
        myInventory.put(Item.HealingPotion, theHealingPots);
    }

    public int getVisionPots() {
        return myInventory.getOrDefault(Item.VisionPotion,0);
    }

    public void setVisionPots(int theVisionPots) {
        myInventory.put(Item.VisionPotion, theVisionPots);
    }

    public String getMyClass() {
        return myClass;
    }

    public String toString() {
        final var pillars = myInventory.keySet().stream().filter(
                x-> (myInventory.getOrDefault(x, 0) > 0)
                && (x.name().contains("Pillar"))
        ).map(Enum::toString).toList();

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
