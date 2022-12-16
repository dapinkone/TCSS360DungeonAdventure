package DungeonAdventure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public abstract class Hero extends DungeonCharacter implements Serializable {

    private final HashMap<Item, Integer> myInventory = new HashMap<>();
    private final String myClass;

    public Hero(String theClass, String theName, int theHealth, int theAttackSpeed, double theHitChance,
                int theMinDmg, int theMaxDmg,
                double theDodgeChance) {

        super(theName, theHealth, theAttackSpeed, theHitChance, theMinDmg, theMaxDmg);
        myClass = theClass;
        //Sets dodge chance which is specific to heroes.
        setMyDodgeChance(theDodgeChance);
        setHealingPots(1); //freebie
    }

    /**
     * Class's special skill to be initialized in child classes.
     */
    public abstract void specialSkill(DungeonCharacter target);

    public HashMap<Item, Integer> getMyInventory() {
        return myInventory;
    }

    /**
     * Use a "Healing Tonic," restores 40 health but value can be changed.
     */
    public void useHealingPot() {
        final var quantity = myInventory.getOrDefault(Item.HealingPotion, 0);
        if (quantity == 0) return;
        int healing = 60; // TODO: randomize healing
        heal(healing);
        myInventory.put(Item.HealingPotion, quantity - 1);
        //healingPots--;
        RecordQ.getInstance().add(new HealthChangeRecord(
                this, this,
                healing, ActionResultType.Heal));
    }

    /**
     * Checks if the hero has all the pillars to win.
     * @return boolean
     */
    public boolean hasAllPillars() {
        return myInventory.keySet().stream().filter(
                x -> x.name().contains("Pillar")
                        && myInventory.get(x) == 1).count() == 4;
    }

    public int getHealingPots() {
        return myInventory.getOrDefault(Item.HealingPotion, 0);
    }

    public void setHealingPots(int theHealingPots) {
        myInventory.put(Item.HealingPotion, theHealingPots);
    }

    public int getVisionPots() {
        return myInventory.getOrDefault(Item.VisionPotion, 0);
    }

    public void setVisionPots(int theVisionPots) {
        myInventory.put(Item.VisionPotion, theVisionPots);
    }

    public String getMyClass() {
        return myClass;
    }

    public String toString() {
        final var pillars = myInventory.keySet().stream().filter(
                x -> (myInventory.getOrDefault(x, 0) > 0)
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

    /**
     * Gets the list of pillars held by the player.
     * @return List of pillars.
     */
    public List<Item> getPillars() {
        return myInventory.keySet().stream().filter(
                item -> item.name().contains("Pillar")
        ).toList();
    }


}
