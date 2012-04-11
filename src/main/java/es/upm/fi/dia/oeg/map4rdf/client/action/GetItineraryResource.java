/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.action;
import net.customware.gwt.dispatch.shared.Action;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoItinerary;

/**
 *
 * @author Daniel Garijo
 */
public class GetItineraryResource implements Action<SingletonResult<WebNMasUnoItinerary>> {
private String uri;

	GetItineraryResource() {
		// for serialization
	}

	public GetItineraryResource(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return uri;
	}
}
