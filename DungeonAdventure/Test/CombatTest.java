package DungeonAdventure.Test;

import DungeonAdventure.*;

import java.util.ArrayList;
import java.util.List;

public class CombatTest {

    public static void main(String[] theArgs) {
//        testSingleCombat();
        testMultiCombat();
    }

    private static void testSingleCombat() {
        Hero hero = new Priestess("Hero");
        MonsterFactory monsterFactory = MonsterFactory.getInstance();
        List<Monster> monsters = new ArrayList<>();
        monsters.add(monsterFactory.generateMonster("ogre"));
        TestCombat c = new TestCombat(monsters, hero);
        System.out.println(c.doCombat());
    }
    private static void testMultiCombat() {
        Hero hero = new Warrior("Hero");
        MonsterFactory monsterFactory = MonsterFactory.getInstance();
        List<Monster> monsters = new ArrayList<>();
        monsters.add(monsterFactory.generateMonster("gremlin"));
        monsters.add(monsterFactory.generateMonster("skeleton"));
        TestCombat c = new TestCombat(monsters, hero);
        System.out.println(c.doCombat());
    }




}
