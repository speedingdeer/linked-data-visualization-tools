/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.server.db;

import com.google.inject.Singleton;
import es.upm.fi.dia.oeg.map4rdf.share.db.StringProcessor;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author filip
 */
@Singleton
public class SQLconnector {
    
    public static Connection connection;
    public static Statement statement;
    
    public SQLconnector() throws ClassNotFoundException, SQLException {
        
        try {
            Class.forName("org.sqlite.JDBC");
            
            File file=new File(DbConfig.DB_NAME);
            Boolean init = true;
            if (file.exists()) {
                    init = false;
            }
            
            connection = DriverManager.getConnection("jdbc:sqlite:" + DbConfig.DB_NAME);
            statement = connection.createStatement();
            if(init) {
                //init seed values for database
                statement.executeUpdate("create table config (key string, value string)");
                
                //crypt and save admin password
                setProperties("admin",StringProcessor.crypt(DbConfig.ADMIN_PASSWORD));
                
                for ( String key : DbConfig.DB_SEED.keySet() ) {
                    setProperties(key,DbConfig.DB_SEED.get(key));
                }
            }
            
            //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
            statement.close();
            connection.close();
        }
    }
    
    public static Boolean setProperties(String key, String value) {
        //check that connection and statement are ok
        
        try {
            statement.executeUpdate("INSERT INTO config VALUES('"+key+"', '"+value+"')");
            return true;
        } catch (SQLException ex) {
          return false;
        }
       
    }

    public static String getProperties(String key) {
        try {
            ResultSet result =  statement.executeQuery("SELECT value FROM config WHERE key='"+key+"'");
            return result.getString("value");
        } catch (SQLException ex) {
            //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
  
    @Override
    protected void finalize() throws Throwable {
        if (statement != null) statement.close();
        if (connection != null) connection.close();
        super.finalize();
    }
}
