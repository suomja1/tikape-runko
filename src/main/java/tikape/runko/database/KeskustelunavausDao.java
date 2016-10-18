package tikape.runko.database;

import java.sql.SQLException;
import java.util.List;
import tikape.runko.domain.Avausnakyma;
import tikape.runko.domain.Keskustelunavaus;

public class KeskustelunavausDao {
    private Database database;

    //Konstruktori
    
    public KeskustelunavausDao(Database database) {
        this.database = database;
    }
    
    //Metodit

    public Keskustelunavaus findOne(Integer key) throws SQLException {
        KeskustelualueDao keskustelualuedao = new KeskustelualueDao(database);
        String query = "SELECT * FROM Keskustelunavaus WHERE id = ?";
        
        return (Keskustelunavaus) database.queryAndCollect(query, rs -> new Keskustelunavaus(rs.getInt("id"), keskustelualuedao.findOne(rs.getInt("alue")), rs.getString("otsikko"), rs.getString("avaus"), rs.getString("aloitettu"), rs.getString("aloittaja")), key).get(0);
    }
    
    public void delete(Integer key) throws SQLException {
        database.update("DELETE FROM Keskustelunavaus WHERE id = ?", key);
    }

    public Keskustelunavaus create(Keskustelunavaus t) throws SQLException {
        database.update("INSERT INTO Keskustelunavaus (alue, otsikko, avaus, aloittaja) VALUES (?, ?, ?, ?)", t.getAlue().getId(), t.getOtsikko(), t.getAvaus(), t.getAloittaja());
        return findByParameters(t.getAlue().getId(), t.getOtsikko(), t.getAvaus(), t.getAloittaja()); // saadaan oikeat arvot sarakkeisiin 'id' ja 'aloitettu' -- toivottavasti...
    }
    
    public void update(Integer key, Keskustelunavaus t) throws SQLException {
        database.update("UPDATE Keskustelunavaus SET otsikko = ? WHERE id = ?", t.getOtsikko(), key);
    }
    
    public Keskustelunavaus findByParameters(Integer alue, String otsikko, String avaus, String aloittaja) throws SQLException {
        KeskustelualueDao keskustelualuedao = new KeskustelualueDao(database);
        String query = "SELECT * FROM Keskustelunavaus WHERE alue = ? AND otsikko = ? AND avaus = ? AND aloittaja = ?";
        
        return (Keskustelunavaus) database.queryAndCollect(query, rs -> new Keskustelunavaus(rs.getInt("id"), keskustelualuedao.findOne(rs.getInt("alue")), rs.getString("otsikko"), rs.getString("avaus"), rs.getString("aloitettu"), rs.getString("aloittaja")), alue, otsikko, avaus, aloittaja).get(0);
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
                + "LIMIT 10";
        
        return database.queryAndCollect(query, rs -> new Avausnakyma(Integer.parseInt(rs.getString("Id")), rs.getString("Alue"), Integer.parseInt(rs.getString("Viesteja_yhteensa")), rs.getString("Viimeisin_viesti")), key);
    }
}