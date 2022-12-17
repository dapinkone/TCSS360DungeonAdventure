package dungeon.adventure;

import java.io.Serializable;
import java.util.Random;

public abstract class AbstractDungeonCharacter implements Serializable {
    private static final Random RANDOM = new Random();
    final int myMinDmg;
    final int myMaxDmg;
    private final int myMaxHealth;
    private final int myAttackSpeed;
    private final double myHitChance;
    private String myName;
    private int myHealth;
    private double myDodgeChance = 0;

    public AbstractDungeonCharacter(final String theName,
                                    final int theHealth,
                                    final int theAttackSpeed,
                                    final double theHitChance,
                                    final int theMinDmg,
                                    final int theMaxDmg) {
        setMyName(theName);
        setMyHealth(theHealth);
        myMaxHealth = theHealth;
        myAttackSpeed = theAttackSpeed;
        myHitChance = theHitChance;
        myMinDmg = theMinDmg;
        myMaxDmg = theMaxDmg;
    }


    /**
     * Attack method, rolls a chance to hit, and another chance for that hit to be blocked.
     * Then rolls the damage within the character's damage range.
     *
     * @param theTarget the target to attack
     * @return The damage, or 0 if missed or -1 if dodged by a hero.
     */
    public void attack(final AbstractDungeonCharacter theTarget) {
        if (isDead()) return;
        int amount = 0;
        // if "dodged" / "blocked" or "miss", it's of type miss.
        ActionResultType result = ActionResultType.Miss;
        if (RANDOM.nextDouble() <= myHitChance) {
            if (RANDOM.nextDouble() >= theTarget.getMyDodgeChance()) {
                int damage = RANDOM.nextInt(myMinDmg, myMaxDmg + 1);
                theTarget.takeDamage(damage);
                amount = damage;
                result = ActionResultType.Hit;
            }
        }
        RecordQ.getInstance().add(new HealthChangeRecord(
                this, theTarget, amount, result
        ));
    }

    public boolean isDead() {
        return getMyHealth() <= 0;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(final String theName) {
        this.myName = theName;
    }

    public int getMyHealth() {
        return myHealth;
    }

    public void setMyHealth(final int theHealth) {
        this.myHealth = theHealth;
    }

    public int getMyMaxHealth() {
        return myMaxHealth;
    }

    public int getMyAttackSpeed() {
        return myAttackSpeed;
    }

    public double getMyDodgeChance() {
        return myDodgeChance;
    }

    public void setMyDodgeChance(final double theDodgeChance) {
        this.myDodgeChance = theDodgeChance;
    }

    /**
     * Returns a string containing a dungeon character's stats.
     * @return String
     */
    public String getStats() {
        StringBuilder string = new StringBuilder();
        string.append("Name: " + myName);
        string.append("\nHealth: " + myHealth + "/" + myMaxHealth);
        string.append("\nSpeed: " + myAttackSpeed);
        string.append("\nHit Chance: " + myHitChance * 100 + "%");
        string.append("\nDamage: " + myMinDmg + "-" + myMaxDmg);
        if (myDodgeChance > 0)
            string.append("\nBlock Chance: " + myDodgeChance * 100 + "%");
        return String.valueOf(string);
    }

    /**
     * Heals the character for a given amount, but not more than their max.
     * @param amount The amount healed.
     */
    public void heal(int amount) {
        setMyHealth(Integer.min(getMyHealth() + amount, getMyMaxHealth()));
    }

    /**
     * Damages a character for a given amount, but they can not fall below 0.
     * @param amount The amount taken.
     */
    public void takeDamage(int amount) {
        setMyHealth(Integer.max(0, getMyHealth() - amount));
    }
}