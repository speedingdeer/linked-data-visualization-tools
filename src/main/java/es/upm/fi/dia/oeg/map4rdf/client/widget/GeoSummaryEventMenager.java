package es.upm.fi.dia.oeg.map4rdf.client.widget;

import net.customware.gwt.dispatch.client.DefaultDispatchAsync;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetItineraryResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.DrawTripEvent;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoItinerary;

public class GeoSummaryEventMenager {
	
	private EventBus eventBus;
	private DispatchAsync dispatchAsync;
	
	public GeoSummaryEventMenager(EventBus eventBus) {
		this.dispatchAsync = new DefaultDispatchAsync();
		this.eventBus = eventBus;
	}
	
	public void drawTrip(String uri){
		if(uri==null||uri.equals("")) {
			return;
		}
		GetItineraryResource action = new GetItineraryResource(uri);
	     dispatchAsync.execute(action, new AsyncCallback<SingletonResult<WebNMasUnoItinerary>>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}
			@Override
			public void onSuccess(SingletonResult<WebNMasUnoItinerary> result) {
				WebNMasUnoItinerary itinerario = result.getValue();
				DrawTripEvent event = new DrawTripEvent(itinerario);
				eventBus.fireEvent(event);
			}
	     });
	}
	
}
