/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
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
        VerticalPanel mainPanel = new VerticalPanel();
        HorizontalPanel caractYGraficas = new HorizontalPanel();
        VerticalPanel caracteristicas = new VerticalPanel();
        VerticalPanel graficas = new VerticalPanel();
        caracteristicas.setSpacing(0);
        graficas.setSpacing(0);
        String text = "No se han obtenidos datos de observacion";
        try{
            AemetResource ae= (AemetResource)result.getValue();
            //if(ae==null)return;
            ArrayList<AemetObs> obs = ae.getObs();
            AemetObs ao = null;         
            if(obs.isEmpty()){
                mainPanel.add(new Label(text));
            }else{
                ao = obs.get(0);
                mainPanel.add(new Label("Estacion "+ao.getEstacion()));
                mainPanel.add(new Label(ao.getIntervalo()));
                for(int i = 0; i< obs.size(); i++){
                    ao = obs.get(i);
                    HorizontalPanel obsActual = new HorizontalPanel();
                    obsActual.setSpacing(5);
                    obsActual.add(new Anchor(ao.getPropiedad(), ao.getUriObs(), "_blank"));
                    obsActual.add(new Label(ao.getValor()));                                        
                    caracteristicas.add(obsActual);

                    HorizontalPanel graf = new HorizontalPanel();
                    graf.setSpacing(5);
                    //Graficas de alex aqui
                    graf.add(new Anchor("dia", "ESTO.ESTA.POR HACER", "_blank"));
                    graf.add(new Label("|"));
                    graf.add(new Anchor("semana", "ESTO.ESTA.POR HACER", "_blank"));
                    graf.add(new Label("|"));
                    graf.add(new Anchor("mes", "ESTO.ESTA.POR HACER", "_blank"));
                    graficas.add(graf);
                }
                caractYGraficas.add(caracteristicas);
                caractYGraficas.add(graficas);
                mainPanel.add(caractYGraficas);
            }
            
            
        }catch(Exception e){
            Window.alert("Error: "+e.getMessage()+ e.toString());
            System.err.println("Error: "+e.getMessage());
            mainPanel.add(new Label(text));
        }
        iwc = new InfoWindowContent(mainPanel);
        this.infoWindow.open(latl, iwc);
    }


}
