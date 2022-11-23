package DungeonAdventure;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Combat {
    private List<Monster> myMonsters;
    private Hero myHero;
    private List<Integer> myTurnOrder;

    public Combat(List<Monster> theMonsters, Hero theHero) {
        myMonsters = theMonsters;
        myHero = theHero;
        myTurnOrder = turnOrdering();
    }

    private List<Integer> turnOrdering() {
        class SpeedNode implements Comparable<SpeedNode> {
            int index = 0;
            int speed = 0;

            private SpeedNode(final int theIndex, final int theSpeed) {
                index = theIndex;
                speed = theSpeed;
            }

            @Override
            public int compareTo(SpeedNode o) {
                return o.speed - this.speed;
            }
        }
        int heroSpeed = myHero.getMyAttackSpeed();
        List<SpeedNode> speeds = new LinkedList<>();
        speeds.add(new SpeedNode(0, heroSpeed));
        int index = 1;
        for (Monster monster : myMonsters) {
            speeds.add(new SpeedNode(index, monster.getMyAttackSpeed()));
            index++;
        }
        Collections.sort(speeds);
        List<Integer> turnOrderIndexes = new LinkedList<>();
        //combat loop
        int min = speeds.get(speeds.size() - 1).speed;
        while (speeds.get(0).speed > 0) {
            for (SpeedNode sn : speeds) {

                if (sn.speed > 0) {
                    turnOrderIndexes.add(sn.index);
                    sn.speed = sn.speed - min;
                }
            }
            Collections.sort(speeds);
            min = speeds.get(speeds.size() - 1).speed;
        }
        return turnOrderIndexes;
    }
    public DungeonCharacter getTurn(int theIndex) {
        if (theIndex == 0) {
            return myHero;
        } else {
            return myMonsters.get(theIndex);
        }
    }

    public int heroTurn(int theOption, int theTargetIndex) {
        int result = 0;
        if (theOption == 1) { //ATTACK
            result = myHero.attack(myMonsters.get(theTargetIndex));
        } else if (theOption == 2) { //SPECIAL SKILL

        } else if (theOption == 3) { //USE ITEMS

        }
        return result;
    }

    private int heroSpecial(int theTargetIndex) {
        int result = myHero.specialSkill();
        String heroClass = myHero.getMyClass();

        if (heroClass.equals("Bruiser")) {
            Monster target = myMonsters.get(theTargetIndex);
            target.setMyHealth(target.getMyHealth() - result);
        }
        if (heroClass.equals("Scout")) {
            if (result == 0) { //fail

            } if (result == 1) { //normal

            } if (result == 2) { //crit

            }
        }

        return result;
    }

    public List<Monster> getMonsters() {
        return myMonsters;
    }
}
