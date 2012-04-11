/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

/**
 * @author Daniel Garijo
 */
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.event.PolylineClickHandler.PolylineClickEvent;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.maps.client.overlay.PolylineOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetItineraryResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLineBean;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoItinerary;
import java.util.ArrayList;
import net.customware.gwt.dispatch.client.DefaultDispatchAsync;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.Display;

/**
 *
 * @author Daniel Garijo
 */
public class DrawItineraryOnClick {

    public DrawItineraryOnClick() {
    }
    
    DrawItineraryOnClick(String uri, final Display disp, final GeoResourcesMapControl refMap){
		if(uri==null||uri.equals(""))return;
                GetItineraryResource action = new GetItineraryResource(uri);
                //esto debe ser GetGeometry o GetItinerary
		
                disp.startProcessing();
                DispatchAsync d = new DefaultDispatchAsync();
		d.execute(action, new AsyncCallback<SingletonResult<WebNMasUnoItinerary>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				disp.stopProcessing();
                                System.err.println("Fallo "+caught.getMessage());
			}
                        @Override
                        public void onSuccess(SingletonResult<WebNMasUnoItinerary> result) {
                            final WebNMasUnoItinerary itinerario = result.getValue();
                            refMap.drawGeometry(itinerario.getFirstGeometry(), new WebNMasUnoClickHandler() {
                                @Override
                                public void onClick(ClickEvent event, Polyline p) {
                                    //Window.alert("Uri del itinerario: "+itinerario.getUri());
                                    //lanzar una ventana con info del itinerario, y opcion de cambiar color
                                    //por ejemplo, que cuando se pase por encima de los puntos, se centre en el
                                    //(o al clickar)
                                    //tambien sacar que es un itinerario del viaje de título x (de la consulta sparql)                                    
//                                    PolyStyleOptions op = PolyStyleOptions.newInstance("#B40404");
//                                    p.setStrokeStyle(op);
                                    //Window.alert("Long linea"+p.getLength());
                                    InfoWindowContent iwc = getPanelItinerarioOnClick(itinerario,p);
                                    refMap.getWindow().open(refMap.getLatLng(), iwc);
                                }
                            });
                             disp.stopProcessing();
                        }
                    });
	}

    private InfoWindowContent getPanelItinerarioOnClick(WebNMasUnoItinerary itinerario, final Polyline p){
        VerticalPanel panelVertical = new VerticalPanel();
        HorizontalPanel panelLinea = new HorizontalPanel();
        panelLinea.setSpacing(8);
        panelLinea.add(new Label("Informacion del itinerario:"));

        //anadimos todos los titulos de viajes que tiene el ititnerario
        VerticalPanel titulos = new VerticalPanel();
        ArrayList<String> t = itinerario.getViajes();
        for (int i = 0; i<t.size();i++){
            titulos.add(new Label(t.get(i)));
        }
        panelLinea.add(titulos);

        //linea 2: info en rdf del itinerario
        HorizontalPanel panelLinea2 = new HorizontalPanel();
        panelLinea2.setSpacing(8);
        panelLinea2.add(new Label("Informacion en RDF:"));
        panelLinea2.add(new Anchor("rdf",itinerario.getUri(),"_blank"));

        //linea 3: cambiar color del viaje. Con el click handler adecuado
        HorizontalPanel panelLinea3 = new HorizontalPanel();
        panelLinea3.setSpacing(8);
        panelLinea3.add(new Label("Cambiar color del viaje:"));
        ListBox lb = new ListBox();
        lb.addItem("verde");
        lb.addItem("azul");
        lb.addItem("rojo");
        lb.addItem("naranja");
        lb.addItem("morado");
        lb.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                //Window.alert("Aqui cambiar el color de la polilinea");
                ListBox l = (ListBox)event.getSource();
                //String nombre = l.getItemText(l.getSelectedIndex());
                //Window.alert(nombre);
                String color = null;
                switch(l.getSelectedIndex()){
                    case (0):color = "#088A08";
                        break;
                    case (1):color = "#08088A";
                        break;
                    case (2):color = "#B40404";
                        break;
                    case (3):color = "#FF5B00";
                        break;
                    case (4):color = "#8A0886";
                        break;
                }               
                PolyStyleOptions op = PolyStyleOptions.newInstance(color);
                p.setStrokeStyle(op);
            }
        });
        panelLinea3.add(lb);

        panelVertical.add(panelLinea);
        panelVertical.add(panelLinea2);
        panelVertical.add(panelLinea3);
        return new InfoWindowContent(panelVertical);
    }

}
