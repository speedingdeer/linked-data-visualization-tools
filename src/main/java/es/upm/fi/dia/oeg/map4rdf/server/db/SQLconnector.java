/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.server.db;

import com.google.inject.Singleton;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.text.StyledEditorKit.BoldAction;

/**
 *
 * @author filip
 */
@Singleton
public class SQLconnector {
    
    Connection connection;
    
    public SQLconnector() throws ClassNotFoundException {
        try {
            Class.forName("org.sqlite.JDBC");
            
            File file=new File(Constants.DB_NAME);
            
            Statement statement;
            
            if (!file.exists()) {
                connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DB_NAME);
                statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
           
                //statement.executeUpdate("drop table if exists person");
                statement.executeUpdate("create table config (key string, value string)");
                statement.executeUpdate("insert into config values('key', 'val')");
            }
            else {
                connection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DB_NAME);
                statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
            }
            //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Boolean setProperties() {
        return true;
    }

    private String getProperties(String key) {
        return "";
    }

}
