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
        String query = "CREATE TABLE IF NOT EXISTS monsters ( " +
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
            stmt.executeUpdate( query );

        } catch ( SQLException e ) {
            e.printStackTrace();
            System.exit( 0 );
        }

        String query1 = "INSERT INTO monsters VALUES" +
                "('Predator', 200, 2, .6, 30, 60, .1, 30, 60)";
        String query2 = "INSERT INTO monsters VALUES" +
                "('Skitter', 70, 5, .8, 15, 30, .4, 20, 40)";
        String query3 = "INSERT INTO monsters VALUES" +
                "('Crawler', 100, 3, .8, 30, 50, .3, 30,50)";
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
    //TODO: Correctly implement method using SQLlite
//    public Monster generateMonster(String name) {
//        String query = "SELECT name FROM monsters";
//
//        try {
//            Connection conn = ds.getConnection();
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery(query);
//
//
//
//        } catch ( SQLException e ) {
//            e.printStackTrace();
//            System.exit( 0 );
//        }
//        return new Monster();
//    }

    public Monster Ogre() {
        return new Monster("Predator", 200, 2,.6,
                30,60,.1,30,60);
    }
    public Monster Gremlin() {
        return new Monster("Skitter", 70, 5, .8,
                15, 30, .4, 20, 40);
    }
    public Monster Skeleton() {
        return new Monster("Crawler", 100, 3, .8,
                30, 50, .3, 30,50);
    }
    public Monster Boss() {
        return new Monster("Awoken Horror", 330, 3,.6,
                40,60,.1,30,60);
    }
}
