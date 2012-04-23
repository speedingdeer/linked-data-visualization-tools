/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.server.command;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;

/**
 * @author Daniel Garijo
 */
public class GetWebNMasUnoResourceHandler implements ActionHandler<GetGeoResource, SingletonResult<GeoResource>> {

	private final Map4rdfDao dao;

	@Inject
	public GetWebNMasUnoResourceHandler(Map4rdfDao dao) {
		this.dao = dao;
	}

	@Override
	public SingletonResult<GeoResource> execute(GetGeoResource action, ExecutionContext context) throws ActionException {
		String uri = action.getUri();
		if (uri == null || uri.length() == 0) {
			throw new ActionException("Invalid URI: " + uri);
		}
		try {
			GeoResource resource = dao.getDatosGuiasViajes(uri);
			return new SingletonResult<GeoResource>(resource);
		} catch (DaoException e) {
			throw new ActionException("Data access error", e);
		}
	}

	@Override
	public Class<GetGeoResource> getActionType() {
		return GetGeoResource.class;
	}

	@Override
	public void rollback(GetGeoResource action, SingletonResult<GeoResource> result, ExecutionContext context)
			throws ActionException {
		// nothing to do

	}
}

