/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import java.util.List;

/**
 *
 * @author filip
 */

public interface  IDBServiceAsync {
    
    public void getValue(String key, AsyncCallback<String> callback);
    public void getValues(List<String> keysList, AsyncCallback<List<ConfigPropertie>> valuesLoadCallback);
    public void setValues(List<ConfigPropertie> propertiesList, AsyncCallback<Boolean> valuesLoadCallback);
    public void login(String password, AsyncCallback<Boolean> loginCallback);
    public void logout(AsyncCallback loginCallback);
}
