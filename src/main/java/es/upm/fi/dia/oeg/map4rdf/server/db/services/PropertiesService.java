/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.server.db.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Singleton;
import es.upm.fi.dia.oeg.map4rdf.client.services.IPropertiesService;

/**
 *
 * @author filip
 */
@Singleton
public class PropertiesService extends RemoteServiceServlet implements IPropertiesService{

    @Override
    public String getValue(String key) {
        return "kluczyk";
    }
    
}
