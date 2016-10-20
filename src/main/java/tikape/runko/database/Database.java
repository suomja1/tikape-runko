package tikape.runko.database;

import java.sql.*;
import java.util.*;

public class Database<T> {
    private String address;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.address = databaseAddress;
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(address);
    }

    public int update(String updateQuery, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(updateQuery, Statement.RETURN_GENERATED_KEYS); //tämän pitäisi osata palauttaa automaattisesti luotu id

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        int changes = stmt.executeUpdate();

        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);

                stmt.close();
                conn.close();

                return id;
            } else {
                stmt.close();
                conn.close();
                
                return changes;
            }
        }
    }

    public List<T> queryAndCollect(String query, Collector<T> col, Object... params) throws SQLException {
        Connection conn = getConnection();

        List<T> rows = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            rows.add(col.collect(rs));
        }

        rs.close();
        stmt.close();
        conn.close();

        return rows;
    }
}