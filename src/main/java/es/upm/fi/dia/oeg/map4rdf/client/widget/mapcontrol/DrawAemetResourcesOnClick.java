/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.geom.LatLng;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.share.AemetObs;
import es.upm.fi.dia.oeg.map4rdf.share.AemetResource;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import java.util.ArrayList;
import net.customware.gwt.presenter.client.Display;

/**
 *
 * @author Daniel Garijo
 */
public class DrawAemetResourcesOnClick extends DrawGeoResourceOnClick {

    public DrawAemetResourcesOnClick(String uri, final Display disp,GeoResourcesMapControl g,LatLng latLong){
        super(uri,disp, g, latLong);
    }

    @Override
    public void draw(SingletonResult<GeoResource> result) {
        InfoWindowContent iwc;
        String text = "<b>No se han obtenidos datos de observacion </b> <br>";;
        try{
            AemetResource ae= (AemetResource)result.getValue();
            if(ae==null)return;
            ArrayList<AemetObs> obs = ae.getObs();
            AemetObs ao = null;
            if(!obs.isEmpty()){
                text = "<b>Provincia : "+obs.get(0).getProv()+"<br>"+obs.get(0).getEstacion()+"  </b> <br>";
                //text += "<div style=\"height:120px;width:550px;overflow:scroll;\">";
                for(int i = 0; i< obs.size(); i++){
                        ao = obs.get(i);
                        text += "<b>Datos de la Observacion : </b>"+ao.getIdObs()+"   <br>"+
                                "<span style=\"color:gray\"><b>Altitud : </b>"+ao.getAlt()+"<br>"+
                                "<b>Tiempo de inicio de medicion : </b>"+ao.getTInicio()+"<br>"+
                                "<b>Tiempo de fin de medicion : </b>"+ao.getTFin()+"<br>"+
                                "<b>QVV10m : </b>"+ao.getQv()+"<br>"+
                                "<b>Velocidad media del viento : </b>"+ao.getVv()+"<br>"+
                                "<b>Direccion media del viento : </b>"+ao.getDv()+"<br>"+
                                "<b>Recorrido del viento : </b>"+ao.getRviento()+"<br>"+
                                "<b>QRviento : </b>"+ao.getRviento()+"<br>"+
                                "<b>Humedad relativa (%) : </b>"+ao.getTHR()+"<br>"+
                                "<b>Velocidad maxima del viento : </b>"+ao.getVMax()+"<br>"+
                                "<b>QVMAx : </b>"+ao.getQrviento()+"<br>"+
                                "<b>Temperatura del aire : </b>"+ao.getTa()+"<br>"+
                                "<b>indisinop : </b>"+ao.getIndisinop()+"<br>"+
                                "<b>TMAX : </b>"+ao.getTMax()+"<br>"+
                                "<b>Temperatura del punto del rocio : </b>"+ao.getTpr()+"<br>"+
                                "<b>Presion reducida al nivel del mar : </b>"+ao.getPresnMar()+"<br>"+
                                "<b>Presion(hPa) : </b>"+ao.getPres()+"<br></span>";

                }
                text=text + "Mas informacion <a href='"
                        + ao.getUriObs() + "' target='_blank'>Aqui</a>";
            }
        }catch(Exception e){
            System.err.println("Error: "+e.getMessage());
        }
        iwc = new InfoWindowContent(text);
        this.infoWindow.open(latl, iwc);
    }


}
