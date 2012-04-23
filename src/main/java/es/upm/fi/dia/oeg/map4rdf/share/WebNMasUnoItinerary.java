/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Daniel Garijo
 */
public class WebNMasUnoItinerary extends GeoResource implements Serializable {
    private ArrayList<String> titulosViajes; //titulos de los Viajes a los que pertenece este itinerario.
    public WebNMasUnoItinerary() {
        titulosViajes = new ArrayList<String>();
    }

     public WebNMasUnoItinerary(String uri, Geometry geometry) {
         super(uri, geometry);
         titulosViajes = new ArrayList<String>();
    }

    public ArrayList<String> getViajes() {
        return titulosViajes;
    }

    public void setViajes(ArrayList<String> p){
        titulosViajes = p;
    }

    public void addViaje (String titulo){
        titulosViajes.add(titulo);
    }



}
