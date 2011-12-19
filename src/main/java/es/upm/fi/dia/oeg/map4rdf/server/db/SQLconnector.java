/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.server.db;

import com.google.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            connection = DriverManager.getConnection("jdbc:sqlite:maprdf.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo')");
            statement.executeUpdate("insert into person values(2, 'yui')");
            //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void helloWorld() {
    }
    
}
