/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.server.command;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetItineraryResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoItinerary;

/**
 *
 * @author Daniel Garijo
 */
public class GetWebNMasUnoItineraryHandler implements ActionHandler<GetItineraryResource, SingletonResult<WebNMasUnoItinerary>> {
        private final Map4rdfDao dao;

	@Inject
	public GetWebNMasUnoItineraryHandler(Map4rdfDao dao) {
		this.dao = dao;
	}

	@Override
	public Class<GetItineraryResource> getActionType() {
		return GetItineraryResource.class;
	}	

        @Override
        public SingletonResult<WebNMasUnoItinerary> execute(GetItineraryResource action, ExecutionContext ec) throws ActionException {
                String uri = action.getUri();
		if (uri == null || uri.length() == 0) {
			throw new ActionException("Invalid URI: " + uri);
		}
		try {
			WebNMasUnoItinerary itinerary = dao.getItinerary(uri);
			return new SingletonResult<WebNMasUnoItinerary>(itinerary);
		} catch (DaoException e) {
			throw new ActionException("Data access error", e);
		}
        }

        @Override
        public void rollback(GetItineraryResource a, SingletonResult<WebNMasUnoItinerary> r, net.customware.gwt.dispatch.server.ExecutionContext ec) throws ActionException {
            
        }
}
