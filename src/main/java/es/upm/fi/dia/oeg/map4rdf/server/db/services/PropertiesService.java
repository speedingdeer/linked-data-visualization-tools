/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.server.db.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Guice;
import com.google.inject.Singleton;
import es.upm.fi.dia.oeg.map4rdf.client.services.IPropertiesService;
import es.upm.fi.dia.oeg.map4rdf.server.db.SQLconnector;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import java.util.List;
import org.tmatesoft.sqljet.core.SqlJetException;

/**
 *
 * @author filip
 */
@Singleton
public class PropertiesService extends RemoteServiceServlet implements IPropertiesService{
    
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
            return dbConnector.setProperties(propertiesList);
        } catch (SqlJetException ex) {
            return false;
            //Logger.getLogger(PropertiesService.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
}
