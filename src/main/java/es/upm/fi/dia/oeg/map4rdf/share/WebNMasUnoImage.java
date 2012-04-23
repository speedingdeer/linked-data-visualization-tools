/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

/**
 *
 * @author Daniel Garijo
 */
public class WebNMasUnoImage extends WebNMasUnoResource implements Serializable {
    private String title;
    private String URI;
    private String pname;


    //serialization
    public WebNMasUnoImage (){

    }

    public WebNMasUnoImage(String title, String URI, String pname) {
        this.title = title;
        this.URI = URI;
        this.pname = pname;
    }

    public String getTitle() {
        return title;
    }

    public String getURI() {
        return URI;
    }

    public String getPname() {
        return pname;
    }

    

}
