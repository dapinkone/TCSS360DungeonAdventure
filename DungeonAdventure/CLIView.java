package DungeonAdventure;

public class CLIView implements DungeonView {
    final Dungeon myDungeon;

    public CLIView(Dungeon myDungeon) {
        this.myDungeon = myDungeon;
    }
    public void showDungeon() {
        final StringBuilder sb = new StringBuilder();
        for (Room[] row: myDungeon.getRooms()) {
            for(int scanline = 0; scanline < 3; scanline++) {
                for (Room room : row) {
                    String s = roomAsASCII(room).split("\n")[scanline];
                    sb.append( s );
                }
                sb.append('\n');
            }
        }
        System.out.println(sb);
    }

    private String roomAsASCII(Room theRoom) {
        final StringBuilder sb = new StringBuilder();
        // top line
        if(theRoom.getDoor(Direction.NORTH)) {
            sb.append("- -\n");
        } else {
            sb.append("---\n");
        }
        // middle line
        sb.append(theRoom.getDoor(Direction.WEST) ? ' ' : '|');
        // append specific items / hero or whatever? should this be in the Dungeon itself?
        sb.append('.');
        sb.append(theRoom.getDoor(Direction.EAST) ? " \n" : "|\n");
        // bottom line
        if(theRoom.getDoor(Direction.SOUTH)) {
            sb.append("- -\n");
        } else {
            sb.append("---\n");
        }
        return sb.toString();
    }
    @Override
    public void setModel(DungeonModel myModel) {

    }

    @Override
    public void updateDisplay() {

    }
}
