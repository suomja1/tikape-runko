package tikape.runko.database;

import java.sql.*;
import java.util.List;
import tikape.runko.domain.*;

public class VastausDao {
    private Database database;

    //Konstruktori
    
    public VastausDao(Database database) {
        this.database = database;
    }

    //Metodit
    
    public Vastaus findOne(Integer key) throws SQLException {
        AvausDao avausDao = new AvausDao(database);
        String query = "SELECT * FROM Vastaus WHERE id = ?";

        return (Vastaus) database.queryAndCollect(query, rs -> new Vastaus(
                rs.getInt("id"), 
                avausDao.findOne(rs.getInt("avaus")), 
                rs.getString("teksti"), 
                rs.getString("ajankohta"), 
                rs.getString("kirjoittaja")
        ), key).get(0);
    }

    public void delete(Integer key) throws SQLException {
        database.update("DELETE FROM Vastaus WHERE id = ?", key);
    }

    public Vastaus create(Vastaus t) throws SQLException {
        String query = "INSERT INTO Vastaus (avaus, teksti, kirjoittaja) VALUES (?, ?, ?)";
        int id = database.update(query, t.getAvaus().getId(), t.getTeksti(), t.getKirjoittaja());
        return this.findOne(id);
    }
    
    public List<Vastaus> findAll(Integer key) throws SQLException {
        AvausDao avausDao = new AvausDao(database);
        String query = "SELECT * FROM Vastaus "
                + "INNER JOIN Keskustelunavaus ON Vastaus.avaus = Keskustelunavaus.id "
                + "AND Keskustelunavaus.id = ?";

        return database.queryAndCollect(query, rs -> new Vastaus(
                rs.getInt("id"), 
                avausDao.findOne(rs.getInt("avaus")), 
                rs.getString("teksti"), 
                rs.getString("ajankohta"), 
                rs.getString("kirjoittaja")
        ), key);
    }

    public List<Vastaus> findAll(int avaus, int alku, int maara) throws SQLException {
        AvausDao avausDao = new AvausDao(database);
        String query = "SELECT * FROM Vastaus "
                + "WHERE avaus = ? "
                + "ORDER BY ajankohta "
                + "LIMIT ?, ?";
        
        return database.queryAndCollect(query, rs -> new Vastaus(
                rs.getInt("id"), 
                avausDao.findOne(rs.getInt("avaus")), 
                rs.getString("teksti"), 
                rs.getString("ajankohta"), 
                rs.getString("kirjoittaja")
        ), avaus, alku, maara);
    }

    public int noOfRows(Integer idd) throws SQLException {
        String query = "SELECT COUNT(*) AS rivit FROM Vastaus WHERE avaus = ?";
        return (int) database.queryAndCollect(query, rs -> rs.getInt("rivit"), idd).get(0);
    }
}