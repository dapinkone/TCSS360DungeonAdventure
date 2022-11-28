package tcss.dungeonadventure.test;

import tcss.dungeonadventure.Hero;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HeroTest {

    @Test
    public void testDodge() {
        MockHero test1 = new MockHero("test1", 1, 1);
        MockHero test2 = new MockHero("test2", 1, 1);
        int result = test1.attack(test2);
        Assertions.assertEquals(-1, result);
    }

    @Test
    public void testHealingPotsOverflow() {
        MockHero test1 = new MockHero("test1", 1, 0);
        MockHero test2 = new MockHero("test2", 1, 1);
        test1.setMyHealingPots(1);
        test2.attack(test1);
        test1.useHealingPot();
        Assertions.assertEquals(test1.getMyMaxHealth(), test1.getMyHealth());
    }

    class MockHero extends Hero {
        MockHero(String theName, double theHitChance, double theDodgeChance) {
            super("test", theName, 50, 1,
                    theHitChance, 10, 20, theDodgeChance);

        }

        @Override
        public int specialSkill() {
            return 0;
        }
    }
}
