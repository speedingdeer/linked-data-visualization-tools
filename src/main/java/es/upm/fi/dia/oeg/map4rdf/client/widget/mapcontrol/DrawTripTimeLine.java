/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetTripProvenanceResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.widget.SimileTimeLine;
import es.upm.fi.dia.oeg.map4rdf.share.SimileTimeLineEventContainer;
import net.customware.gwt.dispatch.client.DefaultDispatchAsync;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.Display;

/**
 * This class draws all the events from a timeline, after querying the server
 * @author Daniel Garijo
 */
public class DrawTripTimeLine {

    public DrawTripTimeLine(){

    }

    public DrawTripTimeLine(String uriViaje, final Display disp, final GeoResourcesMapControl refMap){
		if(uriViaje==null||uriViaje.equals(""))return;
                GetTripProvenanceResource action = new GetTripProvenanceResource(uriViaje);

                disp.startProcessing();
                DispatchAsync d = new DefaultDispatchAsync();
		d.execute(action, new AsyncCallback<SingletonResult<SimileTimeLineEventContainer>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				disp.stopProcessing();
                                System.err.println("Fallo "+caught.getMessage());
			}
                        @Override
                        public void onSuccess(SingletonResult<SimileTimeLineEventContainer> result) {
                            final SimileTimeLineEventContainer listaEventos = result.getValue();

                            //Window.alert(listaEventos.toXml());
                            SimileTimeLine.getInstance().addEvents(listaEventos.toXml(),""+listaEventos.getDateToCenter());
                            disp.stopProcessing();
                        }
                    });
    }

}
