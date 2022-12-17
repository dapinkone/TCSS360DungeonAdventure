package dungeon.adventure.Test;

import dungeon.adventure.ActionResultType;
import dungeon.adventure.DungeonCharacter;
import dungeon.adventure.Hero;
import dungeon.adventure.RecordQ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HeroTest {

    @Test
    public void testDodge() {
        MockHero test1 = new MockHero("test1", 1, 1);
        MockHero test2 = new MockHero("test2", 1, 1);
        test1.attack(test2);
        final var result = RecordQ.getInstance().poll();
        assert result != null;
        final var type = result.actionResultType();
        final var amount = result.amount();
        final var src = result.source();
        final var tgt = result.target();
        Assertions.assertEquals(ActionResultType.Miss, type);
    }

    @Test
    public void testHealingPotsOverflow() {
        MockHero test1 = new MockHero("test1", 1, 0);
        MockHero test2 = new MockHero("test2", 1, 1);
        test1.setHealingPots(1);
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
        public void specialSkill(DungeonCharacter target) {

        }
    }
}
