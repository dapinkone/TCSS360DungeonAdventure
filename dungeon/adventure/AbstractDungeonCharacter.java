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
    private double myDodgeChance;

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
     * Attack method, rolls a chance to hit, and another chance for
     * that hit to be blocked, then rolls the damage within the character's
     * damage range.
     *
     * @param theTarget the target to attack
     */
    public void attack(final AbstractDungeonCharacter theTarget) {
        if (isDead()) {
            return;
        }
        int amount = 0;
        // if "dodged" / "blocked" or "miss", it's of type miss.
        ActionResultType result = ActionResultType.Miss;
        if (RANDOM.nextDouble() <= myHitChance) {
            if (RANDOM.nextDouble() >= theTarget.getMyDodgeChance()) {
                final int damage = RANDOM.nextInt(
                        myMinDmg, myMaxDmg + 1);
                theTarget.takeDamage(damage);
                amount = damage;
                result = ActionResultType.Hit;
            }
        }
        RecordQ.getInstance().add(new HealthChangeRecord(
                this, theTarget, amount, result
        ));
    }

    /***
     * @return true if character is dead, false otherwise.
     */
    public boolean isDead() {
        return getMyHealth() <= 0;
    }

    /***
     * Each character has a given name.
     * @return the character's given from the private myName field.
     */
    public String getMyName() {
        return myName;
    }

    /***
     * sets myName field.
     * @param theName : String name as provided.
     */
    public void setMyName(final String theName) {
        this.myName = theName;
    }

    /***
     * character's current health.
     * @return integer value of character's current health.
     */
    public int getMyHealth() {
        return myHealth;
    }

    /***
     * sets the current health to some value.
     * @param theHealth integer health value
     */
    public void setMyHealth(final int theHealth) {
        this.myHealth = theHealth;
    }

    /***
     * returns the max health of the character(set at instantiation).
     * @return integer of max health
     */
    public int getMyMaxHealth() {
        return myMaxHealth;
    }

    /***
     * @return integer attack speed as set at instantiation time.
     * value is used in calculations for combat.
     */
    public int getMyAttackSpeed() {
        return myAttackSpeed;
    }

    /***
     * @return double of dodge chance. chance to dodge attacks.
     */
    public double getMyDodgeChance() {
        return myDodgeChance;
    }

    /***
     * @param theDodgeChance sets chance to dodge attacks.
     */
    public void setMyDodgeChance(final double theDodgeChance) {
        this.myDodgeChance = theDodgeChance;
    }

    /**
     * Returns a string containing a dungeon character's stats.
     * @return String
     */
    public String getStats() {
        final StringBuilder string = new StringBuilder();
        string.append("Name: ").append(myName);
        string.append("\nHealth: ").append(
                myHealth).append("/").append(myMaxHealth);
        string.append("\nSpeed: ").append(myAttackSpeed);
        string.append("\nHit Chance: ").append(myHitChance * 100).append("%");
        string.append("\nDamage: ").append(myMinDmg).append("-").append(
                myMaxDmg);
        if (myDodgeChance > 0) {
            string.append("\nBlock Chance: ").append(
                    myDodgeChance * 100).append("%");
        }
        return String.valueOf(string);
    }

    /**
     * Heals the character for a given theAmount, but not more than their max.
     * @param theAmount The theAmount healed.
     */
    public void heal(final int theAmount) {
        setMyHealth(Integer.min(getMyHealth() + theAmount, getMyMaxHealth()));
    }

    /**
     * Damages a character for a given theAmount, but they can not fall below 0.
     * @param theAmount The theAmount taken.
     */
    public void takeDamage(final int theAmount) {
        setMyHealth(Integer.max(0, getMyHealth() - theAmount));
    }
}
