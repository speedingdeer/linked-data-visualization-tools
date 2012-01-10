/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 *
 * @author filip
 */
@RemoteServiceRelativePath("properties")
public interface IPropertiesService extends RemoteService{
    public String getValue(String key);
}
