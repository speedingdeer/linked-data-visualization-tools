/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.server.command;
import com.google.inject.Inject;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetImagesResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;


/**
 *
 * @author Daniel Garijo
 */
public class GetImagesHandler implements ActionHandler<GetImagesResource, SingletonResult<GeoResource>> {

	private final Map4rdfDao dao;

	@Inject
	public GetImagesHandler(Map4rdfDao dao) {
		this.dao = dao;
	}

	@Override
	public SingletonResult<GeoResource> execute(GetImagesResource action, ExecutionContext ec) throws ActionException {
		String uri = action.getUri();
		if (uri == null || uri.length() == 0) {
			throw new ActionException("Invalid URI: " + uri);
		}
		try {
			GeoResource resource = dao.getDatosImgsReferencia(uri);
			return new SingletonResult<GeoResource>(resource);
		} catch (DaoException e) {
			throw new ActionException("Data access error", e);
		}
	}

	@Override
	public Class<GetImagesResource> getActionType() {
		return GetImagesResource.class;
	}

	@Override
	public void rollback(GetImagesResource action, SingletonResult<GeoResource> result, ExecutionContext context)
			throws ActionException {
		// nothing to do

	}

}
