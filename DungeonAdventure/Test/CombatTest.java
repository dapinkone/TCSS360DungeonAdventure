package DungeonAdventure.Test;

import DungeonAdventure.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

public class CombatTest {

    public static void main(String[] theArgs) {
        testSingleCombat();
    }

    private static void testSingleCombat() {
        Hero hero = new Warrior("Hero");
        MonsterFactory monsterFactory = new MonsterFactory();
        List<Monster> monsters = new ArrayList<>();
        monsters.add(monsterFactory.generateMonster("skeleton"));
        Combat c = new Combat(monsters, hero);
        System.out.println(c.doCombat());
    }


}