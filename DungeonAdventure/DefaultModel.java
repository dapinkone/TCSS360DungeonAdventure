package DungeonAdventure;

public class DefaultModel implements GameModel {
    private Dungeon myDungeon;
    public DefaultModel() {

    }

    public void saveGame() {

    }

    public void loadGame() {

    }

    @Override
    public void setHero(Hero theHero) {

    }

    @Override
    public Hero getHero() {
        return null;
    }

    @Override
    public Pair getHeroLocation() {
        return null;
    }

    @Override
    public Item[] getRoomItems(Pair p) {
        return new Item[0];
    }

    @Override
    public Direction[] getRoomDoors(Pair p) {
        return new Direction[0];
    }

    @Override
    public boolean pickupItem(Item theItem) {
        return false;
    }

    @Override
    public boolean useItem(Item theItem) {
        return false;
    }

    @Override
    public boolean move(Direction theDirection) {
        return false;
    }

    @Override
    public boolean checkCombat() {
        return false;
    }

    public String getCurrentRoom() {
        return null;
    }

    public String showDungeon() {
        return null;
    }
}
