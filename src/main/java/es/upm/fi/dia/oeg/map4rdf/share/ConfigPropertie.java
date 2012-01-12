/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.share;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;

/**
 *
 * @author filip
 */
public class ConfigPropertie implements Serializable {
    private String key;
    private String value;
    
    public ConfigPropertie(String key, String value) {
        this.key = key;
        this.value = value;
    }
    public ConfigPropertie(){
        
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getKey() {
        return this.key;
    }
    public String getValue() {
        return this.value;
    }
    
}
