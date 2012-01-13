/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.server.db;

import com.google.inject.Singleton;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 *
 * @author filip
 */
@Singleton
public final class SQLconnector {
    
    private static SqlJetDb db;
    
    public SQLconnector() throws ClassNotFoundException, SQLException, SqlJetException {
        
            //check that db exists
            File dbFile=new File(DbConfig.DB_NAME);
            Boolean init = true;
            if (dbFile.exists()) {
                    init = false;
            }
            db = SqlJetDb.open(dbFile, true);
            if(init) {
                //init seed values for database
                try {
                    db.beginTransaction(SqlJetTransactionMode.WRITE);
                    db.createTable("CREATE TABLE config (key TEXT NOT NULL PRIMARY KEY, value TEXT NOT NULL)");
                    db.createIndex("CREATE INDEX key_index ON config(key,value)");
                } finally {
                    db.commit();
                }
                //initiate all properties
                for ( String key : DbConfig.DB_SEED.keySet() ) {
                    addPropertie(key,DbConfig.DB_SEED.get(key));
                }
            }
    }

    public  Boolean addPropertie(String key, String value) throws SqlJetException {
        //check that connection and statement are ok

        ISqlJetTable table;
        db.beginTransaction(SqlJetTransactionMode.WRITE);
        try {    
            table = db.getTable("config");
            if(key.equals("admin")) {
                table.insert(key,crypt(value));
            } else {
                table.insert(key,value);
            }
            
        } catch (SqlJetException ex) {
            db.commit();
            return false;
        }
        db.commit();
        return true;
    }

    public String getPropertie(String key) throws SqlJetException  {
        ISqlJetTable table;
        db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
        try {
            table = db.getTable("config");
            ISqlJetCursor cursor = table.lookup("key_index",key);
            String a = cursor.getString("value");
            //ISqlJetCursor cursor = table.open();
            db.commit();
            return a;
        } catch (SqlJetException ex) {
                    //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.commit();
        return "";
    }

    
    public List<ConfigPropertie> getProperties(List<String> keys) throws SqlJetException {
        ISqlJetTable table;
        db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
        try {
            table = db.getTable("config");
            List<ConfigPropertie> resultList = new LinkedList<ConfigPropertie>();
            for (String key : keys) {
                ISqlJetCursor cursor = table.lookup("key_index",key);
                resultList.add(new ConfigPropertie(key, cursor.getString("value")));
            }
            //ISqlJetCursor cursor = table.open();
            db.commit();
            return resultList;
        } catch (SqlJetException ex) {
        db.commit();
        return null;         //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }
    
    public Boolean setProperties(List<ConfigPropertie> properties) throws SqlJetException {
        ISqlJetTable table;
        db.beginTransaction(SqlJetTransactionMode.WRITE);
        try {
            table = db.getTable("config");
            for (ConfigPropertie p : properties) {
                ISqlJetCursor cursorUpdate = table.lookup("key_index",p.getKey());
                //resultList.add(new ConfigPropertie(key, cursor.getString("value")));
                if(p.getKey().equals("admin")) {
                    cursorUpdate.update(cursorUpdate.getValue("key"),crypt(p.getValue()));
                }
                else {
                    cursorUpdate.update(cursorUpdate.getValue("key"),p.getValue());
                }
            }
            db.commit();
            return true;
        } catch (SqlJetException ex) {
            //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
            db.commit();
            return false;
        }

    }
    
        public Boolean login(String password) throws SqlJetException  {
        ISqlJetTable table;
        db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
        try {
            table = db.getTable("config");
            ISqlJetCursor cursor = table.lookup("key_index","admin");
            String a = cursor.getString("value");
            //ISqlJetCursor cursor = table.open();
            db.commit();
            return BCrypt.checkpw(password, a);
        } catch (SqlJetException ex) {
                    //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.commit();
        return false;
    }

        
    private String crypt(String string) {
        return BCrypt.hashpw(string, BCrypt.gensalt(12));
    }
}
            