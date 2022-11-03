package DungeonAdventure.Test;

import DungeonAdventure.DungeonCharacter;
import org.junit.jupiter.api.*;
import DungeonAdventure.Hero;

public class HeroTest {

    class MockHero extends Hero{
        MockHero(String theName, double theHitChance, double theDodgeChance) {
            super("test",theName,50, 1,
                    theHitChance, 10, 20, theDodgeChance);

        }

        @Override
        public int specialSkill(DungeonCharacter theTarget) {
            return 0;
        }
    }
    @Test
    public void testDodge() {
        MockHero test1 = new MockHero("test1", 1, 1);
        MockHero test2 = new MockHero("test2", 1, 1);
        int result = test1.attack(test2);
        Assertions.assertEquals(-1, result);
    }
}
