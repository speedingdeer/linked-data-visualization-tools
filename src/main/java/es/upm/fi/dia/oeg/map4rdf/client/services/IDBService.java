/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import java.util.List;

/**
 *
 * @author filip
 */
@RemoteServiceRelativePath("properties")
public interface IDBService extends RemoteService{
    public String getValue(String key);
    public List<ConfigPropertie> getValues(List<String> keys);
    public Boolean setValues(List<ConfigPropertie> propertiesList);
    public Boolean login(String password);
    public void logout();
}
