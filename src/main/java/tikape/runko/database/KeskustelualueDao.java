package tikape.runko.database;

import java.sql.SQLException;
import java.util.List;
import tikape.runko.domain.Keskustelualue;

public class KeskustelualueDao implements Dao<Keskustelualue, Integer> {
    private Database database;

    public KeskustelualueDao(Database database) {
        this.database = database;
    }

    @Override
    public Keskustelualue findOne(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Keskustelualue> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Keskustelualue create(Keskustelualue t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Integer key, Keskustelualue t) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
