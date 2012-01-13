/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.server.db.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Guice;
import com.google.inject.Singleton;
import es.upm.fi.dia.oeg.map4rdf.client.services.IDBService;
import es.upm.fi.dia.oeg.map4rdf.server.db.SQLconnector;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.tmatesoft.sqljet.core.SqlJetException;

/**
 *
 * @author filip
 */
@Singleton
public class DBService extends RemoteServiceServlet implements IDBService{
    
    @Override
    public String getValue(String key) {
        SQLconnector dbConnector = Guice.createInjector().getInstance(SQLconnector.class);
        try {
            return dbConnector.getPropertie(key);
        } catch (SqlJetException ex) {
            return "";
            //Logger.getLogger(PropertiesService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<ConfigPropertie> getValues(List<String> keys) {
        SQLconnector dbConnector = Guice.createInjector().getInstance(SQLconnector.class);
        try {
            return dbConnector.getProperties(keys);
        } catch (SqlJetException ex) {
            return null;
            //Logger.getLogger(PropertiesService.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }

    @Override
    public Boolean setValues(List<ConfigPropertie> propertiesList) {
        SQLconnector dbConnector = Guice.createInjector().getInstance(SQLconnector.class);
        try {
            HttpSession session = this.getThreadLocalRequest().getSession();
            String a = (String) session.getAttribute("admin");
            if(a.equals("true")) {
                return dbConnector.setProperties(propertiesList);
            }
            else {
                return false;
            }
        } catch (SqlJetException ex) {
            return false;
            //Logger.getLogger(PropertiesService.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }

    @Override
    public Boolean login(String password) {
        SQLconnector dbConnector = Guice.createInjector().getInstance(SQLconnector.class);
        try {
            if(dbConnector.login(password)) {
                HttpSession session = this.getThreadLocalRequest().getSession();
                session.setAttribute("admin", "true");
                return true;
            }
            else {
                return false;
            }
            
        } catch (SqlJetException ex) {
            return false;
            //Logger.getLogger(PropertiesService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void logout() {
        HttpSession session = this.getThreadLocalRequest().getSession();
        session.setAttribute("admin", "false");
    }
}
