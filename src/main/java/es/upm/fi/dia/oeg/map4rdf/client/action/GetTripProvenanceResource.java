/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.action;

import net.customware.gwt.dispatch.shared.Action;
import es.upm.fi.dia.oeg.map4rdf.share.SimileTimeLineEventContainer;

/**
 *
 * @author Daniel Garijo
 */
public class GetTripProvenanceResource implements Action<SingletonResult<SimileTimeLineEventContainer>> {
    private String uri;

	GetTripProvenanceResource() {
		// for serialization
	}

	public GetTripProvenanceResource(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

}
