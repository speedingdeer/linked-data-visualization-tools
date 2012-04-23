/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.rpc.AsyncCallback;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import net.customware.gwt.dispatch.client.DefaultDispatchAsync;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.Display;

/**
 *
 * @author Daniel Garijo
 */
public class DrawGeoResourceOnClick {
    private String uri; //uri of the resource from which we want to recover stuff
    protected InfoWindow infoWindow;
    protected LatLng latl;
    protected Display disp;
    protected GeoResourcesMapControl ref;

    public DrawGeoResourceOnClick() {
    }

    public DrawGeoResourceOnClick(String uri, final Display disp, GeoResourcesMapControl refMap,LatLng latLong){
                infoWindow = refMap.getWindow();
                latl = latLong;
                this.disp = disp;
                this.ref = refMap;

		GetGeoResource action = new GetGeoResource(uri);
		disp.startProcessing();
                DispatchAsync d = new DefaultDispatchAsync();
		d.execute(action, new AsyncCallback<SingletonResult<GeoResource>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				disp.stopProcessing();
                                System.err.println("Fallo "+caught.getMessage());
			}
                        @Override
                        public void onSuccess(SingletonResult<GeoResource> result) {
                             draw(result);
                             disp.stopProcessing();
                        }
                    });
	}

    public void draw(SingletonResult<GeoResource> result){
        //Esta funcion es la que debe ser extendida por las implementaciones
    }

}
