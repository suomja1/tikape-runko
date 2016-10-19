package tikape.runko.database;

import java.sql.SQLException;
import java.util.List;
import tikape.runko.domain.*;

public class KeskustelualueDao {
    private Database database;
    
    //Konstruktori

    public KeskustelualueDao(Database database) {
        this.database = database;
    }
    
    //Metodit
    
    public Keskustelualue findOne(Integer key) throws SQLException {
        String query = "SELECT * FROM Keskustelualue WHERE id = ?";
        return (Keskustelualue) database.queryAndCollect(query, rs -> new Keskustelualue(
                rs.getInt("id"), 
                rs.getString("aihealue"), 
                rs.getString("kuvaus"), 
                rs.getString("perustettu"), 
                rs.getString("perustaja")
        ), key).get(0);
    }

    public void delete(Integer key) throws SQLException {
        database.update("DELETE FROM Keskustelualue WHERE id = ?", key);
    }

    public Keskustelualue create(Keskustelualue t) throws SQLException {
        String query = "INSERT INTO Keskustelualue (aihealue, kuvaus, perustaja) VALUES (?, ?, ?)";
        int id = database.update(query, t.getAihealue(), t.getKuvaus(), t.getPerustaja());
        return this.findOne(id);
    }

    public void update(Integer key, Keskustelualue t) throws SQLException {
        String query = "UPDATE Keskustelualue SET aihealue = ? WHERE id = ?";
        database.update(query, t.getAihealue(), key);
    }
    
    public List<Avausnakyma> findAll() throws SQLException {
        String query = "SELECT Keskustelualue.id AS Id, Keskustelualue.aihealue AS Alue, "
                + "COUNT(DISTINCT Keskustelunavaus.id) + COUNT(DISTINCT Vastaus.id) AS Viesteja_yhteensa, "
                + "MAX(IFNULL(MAX(DATETIME(Vastaus.ajankohta, 'localtime')), 0), MAX(DATETIME(Keskustelunavaus.aloitettu, 'localtime'))) AS Viimeisin_viesti "
                + "FROM Keskustelualue "
                + "LEFT JOIN Keskustelunavaus ON Keskustelualue.id = Keskustelunavaus.alue "
                + "LEFT JOIN Vastaus ON Keskustelunavaus.id = Vastaus.avaus "
                + "GROUP BY Keskustelualue.id "
                + "ORDER BY Keskustelualue.aihealue";
        
        return database.queryAndCollect(query, rs -> new Avausnakyma(
                Integer.parseInt(rs.getString("Id")), 
                rs.getString("Alue"), 
                Integer.parseInt(rs.getString("Viesteja_yhteensa")), 
                rs.getString("Viimeisin_viesti")
        ));
    }
}