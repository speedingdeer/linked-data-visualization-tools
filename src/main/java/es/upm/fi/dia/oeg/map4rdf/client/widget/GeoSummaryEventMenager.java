package es.upm.fi.dia.oeg.map4rdf.client.widget;

import net.customware.gwt.dispatch.client.DefaultDispatchAsync;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetItineraryResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetTripProvenanceResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.DrawTripEvent;
import es.upm.fi.dia.oeg.map4rdf.share.SimileTimeLineEventContainer;
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
	
	public void drawHistory (String uriViaje, final Panel timelineContainer, final Panel replacedContainer) {

		if(uriViaje==null||uriViaje.equals(""))return;
		
		 GetTripProvenanceResource action = new GetTripProvenanceResource(uriViaje);
		 dispatchAsync.execute(action, new AsyncCallback<SingletonResult<SimileTimeLineEventContainer>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(
					SingletonResult<SimileTimeLineEventContainer> result) {
				// TODO Auto-generated method stub
				 final SimileTimeLineEventContainer listaEventos = result.getValue();
				 String a = listaEventos.toXml();
				 String b = ""+listaEventos.getDateToCenter();
				 SimileTimeLine.getInstance().addEvents(listaEventos.toXml(),""+listaEventos.getDateToCenter());
				 timelineContainer.clear();
				 timelineContainer.add(SimileTimeLine.getInstance());
				 replacedContainer.setVisible(false);
				 timelineContainer.setVisible(true);
					
			}
		 });
	}
	
	
}
