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
import es.upm.fi.dia.oeg.map4rdf.client.services.ISessionsService;
import es.upm.fi.dia.oeg.map4rdf.server.db.SQLconnector;
import javax.servlet.http.HttpSession;


@Singleton
public class SessionsServiceServlet extends RemoteServiceServlet implements ISessionsService{
    
    @Override
    public Boolean login(String password) {
        SQLconnector dbConnector = Guice.createInjector().getInstance(SQLconnector.class);
            if(dbConnector.login(password)) {
                //session does not work on the server site. Why not?
                HttpSession session = this.getThreadLocalRequest().getSession(true);
                session.setAttribute("admin", "true");
                session.setMaxInactiveInterval(1000 * 60 * 15 ); //15min
                
                return true;
            }
            return false;
    }

    @Override
    public void logout() {
        HttpSession session = this.getThreadLocalRequest().getSession();
        session.removeAttribute("admin");
    }
}