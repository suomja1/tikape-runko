package tikape.runko.database;

import java.sql.*;
import java.util.List;
import tikape.runko.domain.Vastaus;

public class VastausDao {
    private Database database;

    //Konstruktori
    
    public VastausDao(Database database) {
        this.database = database;
    }

    //Metodit
    
    public Vastaus findOne(Integer key) throws SQLException {
        KeskustelunavausDao keskustelunavausdao = new KeskustelunavausDao(database);
        String query = "SELECT * FROM Vastaus WHERE id = ?";

        return (Vastaus) database.queryAndCollect(query, rs -> new Vastaus(rs.getInt("id"), keskustelunavausdao.findOne(rs.getInt("avaus")), rs.getString("teksti"), rs.getString("ajankohta"), rs.getString("kirjoittaja")), key).get(0);
    }

    public void delete(Integer key) throws SQLException {
        database.update("DELETE FROM Vastaus WHERE id = ?", key);
    }

    public Vastaus create(Vastaus t) throws SQLException {
        database.update("INSERT INTO Vastaus (avaus, teksti, kirjoittaja) VALUES (?, ?, ?)", t.getAvaus().getId(), t.getTeksti(), t.getKirjoittaja());
        return findByParameters(t.getAvaus().getId(), t.getTeksti(), t.getKirjoittaja()); // saadaan oikeat arvot sarakkeisiin 'id' ja 'ajankohta' -- toivottavasti...
    }
    
    public Vastaus findByParameters(Integer avaus, String teksti, String kirjoittaja) throws SQLException {
        KeskustelunavausDao keskustelunavausdao = new KeskustelunavausDao(database);
        String query = "SELECT * FROM Vastaus WHERE avaus = ? AND teksti = ? AND kirjoittaja = ?";
        
        return (Vastaus) database.queryAndCollect(query, rs -> new Vastaus(rs.getInt("id"), keskustelunavausdao.findOne(rs.getInt("avaus")), rs.getString("teksti"), rs.getString("ajankohta"), rs.getString("kirjoittaja")), avaus, teksti, kirjoittaja).get(0);
    }

    public List<Vastaus> findAll(Integer key) throws SQLException {
        KeskustelunavausDao keskustelunavausdao = new KeskustelunavausDao(database);
        String query = "SELECT * FROM Vastaus "
                + "INNER JOIN Keskustelunavaus ON Vastaus.avaus = Keskustelunavaus.id "
                + "AND Keskustelunavaus.id = ?";

        return database.queryAndCollect(query, rs -> new Vastaus(rs.getInt("id"), keskustelunavausdao.findOne(rs.getInt("avaus")), rs.getString("teksti"), rs.getString("ajankohta"), rs.getString("kirjoittaja")), key);
    }
}