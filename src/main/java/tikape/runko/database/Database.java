package tikape.runko.database;

import java.sql.*;
import java.util.*;

public class Database<T> {
    private String address;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.address = databaseAddress;
        
        init();
    }
    
     private void init() {
        List<String> lauseet = null;
        if (this.address.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
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
    
    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("DROP TABLE Keskustelualue;");
        // heroku käyttää SERIAL-avainsanaa uuden tunnuksen automaattiseen luomiseen
        lista.add("CREATE TABLE Keskustelualue (id Integer SERIAL PRIMARY KEY, aihealue varchar(64) NOT NULL, "
                + "kuvaus varchar(128), perustettu timestamp default current_timestamp NOT NULL, "
                + "perustaja varchar(16) NOT NULL);");
        lista.add("INSERT INTO Keskustelualue (aihealue, kuvaus, perustettu, perustaja) VALUES ('postgresql-keskustelualue');");
        
        lista.add("DROP TABLE Keskustelunavaus;");
        
        lista.add("CREATE TABLE Keskustelunavaus (id Integer SERIAL PRIMARY KEY, alue Integer NOT NULL, "
                + "otsikko varchar(64) NOT NULL, avaus varchar NOT NULL, "
                + "aloitettu timestamp default current_timestamp NOT NULL, "
                + "aloittaja varchar(16) NOT NULL, FOREIGN KEY(alue) REFERENCES Keskustelualue)");
        
        lista.add("INSERT INTO Keskustelunavaus (alue, otsikko, avaus, aloitettu, aloittaja) VALUES ('postgresql-keskustelunavaus');");
        
        lista.add("DROP TABLE Vastaus;");
        
        lista.add("CREATE TABLE Vastaus (id Integer SERIAL PRIMARY KEY, avaus Integer NOT NULL, "
                + "teksti varchar NOT NULL, ajankohta timestamp default current_timestamp NOT NULL, "
                + "kirjoittaja varchar(16) NOT NULL, FOREIGN KEY(avaus) REFERENCES Keskustelunavaus)");
        
        lista.add("INSERT INTO Vastaus (avaus, teksti, ajankohta, kirjoittaja) VALUES ('postgresql-keskustelunavaus');");

        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("CREATE TABLE Keskustelualue (id Integer PRIMARY KEY, "
                + "aihealue varchar(64) NOT NULL, kuvaus varchar(128), "
                + "perustettu timestamp default current_timestamp NOT NULL, "
                + "perustaja varchar(16) NOT NULL)");
        lista.add("INSERT INTO Keskustelualue (aihealue, kuvaus, perustettu, perustaja) "
                + "VALUES ('sqlite-keskustelualue');");
        
        lista.add("CREATE TABLE Keskustelunavaus (id Integer PRIMARY KEY, alue Integer NOT NULL, "
                + "otsikko varchar(64) NOT NULL, avaus varchar NOT NULL, "
                + "aloitettu timestamp default current_timestamp NOT NULL, "
                + "aloittaja varchar(16) NOT NULL, FOREIGN KEY(alue) REFERENCES Keskustelualue);");
        lista.add("INSERT INTO Keskustelunavaus (alue, otsikko, avaus, aloitettu, aloittaja) VALUES ('sqlite-keskustelunavaus');");
        
        lista.add("CREATE TABLE Vastaus (id Integer PRIMARY KEY, avaus Integer NOT NULL, "
                + "teksti varchar NOT NULL, ajankohta timestamp default current_timestamp NOT NULL, "
                + "kirjoittaja varchar(16) NOT NULL, FOREIGN KEY(avaus) REFERENCES Keskustelunavaus);");
        lista.add("INSERT INTO Vastaus (avaus, teksti, ajankohta, kirjoittaja) VALUES ('sqlite-keskustelunavaus');");
        
        return lista;
    }
}