/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.server.servlet;

/**
 *
 * @author filip
 */
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Guice;
import com.google.inject.Singleton;
import es.upm.fi.dia.oeg.map4rdf.client.services.IDBService;
import es.upm.fi.dia.oeg.map4rdf.server.db.SQLconnector;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.tmatesoft.sqljet.core.SqlJetException;

@Singleton
public class DBServiceServlet extends RemoteServiceServlet implements IDBService {

    @Override
    public List<ConfigPropertie> getValues(List<String> keys) {
            HttpSession session = this.getThreadLocalRequest().getSession(true);
            if (session.getAttribute("admin") == null || !session.getAttribute("admin").toString().equals("true")) {
                return null;
            }
            SQLconnector dbConnector = Guice.createInjector().getInstance(SQLconnector.class);
            return dbConnector.getProperties(keys);
  
    }

    @Override
    public Boolean setValues(List<ConfigPropertie> propertiesList) {
        HttpSession session = this.getThreadLocalRequest().getSession(true);
        if (session.getAttribute("admin") == null || !session.getAttribute("admin").toString().equals("true")) {
            return false;
        }
        SQLconnector dbConnector = Guice.createInjector().getInstance(SQLconnector.class);
        return dbConnector.setProperties(propertiesList);
    }
}