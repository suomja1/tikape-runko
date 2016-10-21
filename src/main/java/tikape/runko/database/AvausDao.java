package tikape.runko.database;

import java.sql.SQLException;
import java.util.List;
import tikape.runko.domain.*;

public class AvausDao {
    private Database database;

    //Konstruktori
    
    public AvausDao(Database database) {
        this.database = database;
    }
    
    //Metodit

    public Avaus findOne(Integer key) throws SQLException {
        AlueDao alueDao = new AlueDao(database);
        String query = "SELECT * FROM Keskustelunavaus WHERE id = ?";
        
        return (Avaus) database.queryAndCollect(query, rs -> new Avaus(
                rs.getInt("id"), 
                alueDao.findOne(rs.getInt("alue")), 
                rs.getString("otsikko"), 
                rs.getString("avaus"), 
                rs.getString("aloitettu"), 
                rs.getString("aloittaja")
        ), key).get(0);
    }
    
    public void delete(Integer key) throws SQLException {
        database.update("DELETE FROM Keskustelunavaus WHERE id = ?", key);
    }

    public Avaus create(Avaus t) throws SQLException {
        String query = "INSERT INTO Keskustelunavaus (alue, otsikko, avaus, aloittaja) VALUES (?, ?, ?, ?)";
        int id = database.update(query, t.getAlue().getId(), t.getOtsikko(), t.getAvaus(), t.getAloittaja());
        return this.findOne(id);
    }
    
    public void update(Integer key, Avaus t) throws SQLException {
        String query = "UPDATE Keskustelunavaus SET otsikko = ? WHERE id = ?";
        database.update(query, t.getOtsikko(), key);
    }
    
    public List<Avausnakyma> findAll(int key) throws SQLException {
        String query = "SELECT Keskustelunavaus.id AS Id, Keskustelunavaus.otsikko AS Alue, "
                + "COUNT(DISTINCT Vastaus.id) + 1 AS Viesteja_yhteensa, "
                + "MAX(IFNULL(MAX(DATETIME(Vastaus.ajankohta, 'localtime')), 0), MAX(DATETIME(Keskustelunavaus.aloitettu, 'localtime'))) AS Viimeisin_viesti "
                + "FROM Keskustelunavaus "
                + "INNER JOIN Keskustelualue ON Keskustelunavaus.alue = Keskustelualue.id "
                + "AND Keskustelualue.id = ? "
                + "LEFT JOIN Vastaus ON Keskustelunavaus.id = Vastaus.avaus "
                + "GROUP BY Keskustelunavaus.id "
                + "ORDER BY Viimeisin_viesti DESC "
                + "LIMIT 10"; // kymmenen uusinta
        
        return database.queryAndCollect(query, rs -> new Avausnakyma(Integer.parseInt(rs.getString("Id")), rs.getString("Alue"), Integer.parseInt(rs.getString("Viesteja_yhteensa")), rs.getString("Viimeisin_viesti")), key);
    }
}