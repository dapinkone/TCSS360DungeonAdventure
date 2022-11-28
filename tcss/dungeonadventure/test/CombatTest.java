package tcss.dungeonadventure.test;

import tcss.dungeonadventure.Combat;
import tcss.dungeonadventure.Hero;
import tcss.dungeonadventure.Monster;
import tcss.dungeonadventure.MonsterFactory;
import tcss.dungeonadventure.Priestess;
import tcss.dungeonadventure.TestCombat;
import tcss.dungeonadventure.Warrior;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CombatTest {

    public static void main(final String[] theArgs) {
//        testSingleCombat();
        testMultiCombat();
    }
    private static void testSingleCombat() {
        final Hero hero = new Priestess("Hero");
        final MonsterFactory monsterFactory = MonsterFactory.getInstance();
        final List<Monster> monsters = new ArrayList<>();
        monsters.add(monsterFactory.generateMonster("ogre"));
        final TestCombat c = new TestCombat(monsters, hero);
        System.out.println(c.doCombat());
    }

    private static void testMultiCombat() {
        final Hero hero = new Warrior("Hero");
        final MonsterFactory monsterFactory = MonsterFactory.getInstance();
        final List<Monster> monsters = new ArrayList<>();
        monsters.add(monsterFactory.generateMonster("gremlin"));
        monsters.add(monsterFactory.generateMonster("skeleton"));
        final TestCombat c = new TestCombat(monsters, hero);
        System.out.println(c.doCombat());
    }

    @Test
    public void testTurnOrder() {
        final Hero hero = new Warrior("Hero");
        final MonsterFactory monsterFactory = MonsterFactory.getInstance();
        final List<Monster> monsters = new ArrayList<>();
        monsters.add(monsterFactory.generateMonster("gremlin"));
        monsters.add(monsterFactory.generateMonster("skeleton"));
        final Combat combat = new Combat(monsters, hero);
        //should be gremlin, hero, skeleton.
        Assertions.assertEquals(1, combat.getMyNextTurn());
        Assertions.assertEquals(0, combat.getMyNextTurn());
        Assertions.assertEquals(2, combat.getMyNextTurn());
        Assertions.assertEquals(1, combat.getMyNextTurn());
    }
}
