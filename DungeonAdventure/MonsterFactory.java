package DungeonAdventure;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class MonsterFactory {
    private static MonsterFactory instance = new MonsterFactory();

    SQLiteDataSource ds = null;

    /**
     * Access point for the Singleton MonsterFactory class.
     * @return the global instance of MonsterFactory.
     */
    public static MonsterFactory getInstance() {
        return instance;
    }

    private MonsterFactory() {

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
                "('Skitter', 70, 5, .8, 10, 20, .4, 10, 20)";
        String query2 = "INSERT INTO monsters VALUES" +
                "('Crawler', 100, 3, .8, 15, 30, .3, 15 ,30)";
        String query3 = "INSERT INTO monsters VALUES" +
                "('Predator', 200, 2, .6, 20, 40, .1, 20, 40)";
        String query4 = "INSERT INTO monsters VALUES" +
                "('Awoken Horror', 330, 2, .6, 30, 40, .1, 30, 40)";

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

    /**
     * Generates a random monster from the non-boss monsters.
     * @return The random monster.
     */
    public Monster generateRandom() {
        Random rand = new Random();
        int num = rand.nextInt(3) + 1;
        String name = "";
        switch (num) {
            case(1)-> name = "gremlin";
            case(2)-> name = "skeleton";
            case(3)-> name = "ogre";
        }
        return generateMonster(name);
    }

}
