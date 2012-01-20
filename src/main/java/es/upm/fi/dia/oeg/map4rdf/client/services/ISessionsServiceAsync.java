/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author filip
 */
public interface ISessionsServiceAsync {
    public void login(String password, AsyncCallback<Boolean> loginCallback);
    public void logout(AsyncCallback loginCallback);   
}
