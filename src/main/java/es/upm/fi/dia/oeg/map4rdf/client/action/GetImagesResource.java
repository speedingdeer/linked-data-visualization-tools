/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.action;
import net.customware.gwt.dispatch.shared.Action;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;

/**
 *
 * @author Daniel Garijo
 */
public class GetImagesResource implements Action<SingletonResult<GeoResource>> {
    private String uri;

	GetImagesResource() {
		// for serialization
	}

	public GetImagesResource(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

}
