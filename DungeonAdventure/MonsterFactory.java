package DungeonAdventure;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MonsterFactory {
    SQLiteDataSource ds = null;
    public MonsterFactory() {

        //establish connection (creates db file if it does not exist :-)
        try {
            ds = new SQLiteDataSource();
            ds.setUrl("jdbc:sqlite:monsters.db");
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit(0);
        }

        //now create a table
        String query = "CREATE TABLE monsters ( " +
                "NAME string, " +
                "HEALTH int," +
                "ATTACKSPEED int," +
                "HITCHANCE double," +
                "MINDAMAGE int," +
                "MAXDAMAGE int," +
                "HEALCHANCE double," +
                "MINHEAL int," +
                "MAXHEAL int )";
        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("drop table if exists monsters");
            stmt.executeUpdate( query );

        } catch ( SQLException e ) {
            e.printStackTrace();
            System.exit( 0 );
        }

        String query1 = "INSERT INTO monsters VALUES" +
                "('Skitter', 70, 5, .8, 15, 30, .4, 20, 40)";
        String query2 = "INSERT INTO monsters VALUES" +
                "('Crawler', 100, 3, .8, 30, 50, .3, 30,50)";
        String query3 = "INSERT INTO monsters VALUES" +
                "('Predator', 200, 2, .6, 30, 60, .1, 30, 60)";
        String query4 = "INSERT INTO monsters VALUES" +
                "('Awoken Horror', 330, 3, .6, 40, 60, .1, 30, 60)";

        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate( query1 );
            stmt.executeUpdate( query2 );
            stmt.executeUpdate( query3 );
            stmt.executeUpdate( query4 );

        } catch ( SQLException e ) {
            e.printStackTrace();
            System.exit( 0 );
        }

    }

    /**
     * Generates a monster when given the name of the monster. Translates the name into
     * the in-game alias.
     * @param theName The name in the project specification to generate.
     * @return the Monster object.
     */
    public Monster generateMonster(String theName) {
        int rowID = 0;
        switch (theName) {
            case("gremlin") -> rowID = 1;
            case("skeleton") -> rowID = 2;
            case("ogre") -> rowID = 3;
            case("boss") -> rowID = 4;
        }
        String query = "SELECT * FROM monsters WHERE rowid = " + rowID;

        try {
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return new Monster(
                    rs.getString("name"),
                    rs.getInt("health"),
                    rs.getInt("attackspeed"),
                    rs.getDouble("hitchance"),
                    rs.getInt("mindamage"),
                    rs.getInt("maxdamage"),
                    rs.getDouble("healchance"),
                    rs.getInt("minheal"),
                    rs.getInt("maxheal")
            );
        } catch ( SQLException e ) {
            e.printStackTrace();
            System.exit( 0 );
        }
        return null;
    }

}
