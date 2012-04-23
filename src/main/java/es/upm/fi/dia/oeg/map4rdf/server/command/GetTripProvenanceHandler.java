/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.server.command;
import com.google.inject.Inject;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetTripProvenanceResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.share.SimileTimeLineEventContainer;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;


/**
 *
 * @author Daniel Garijo
 */
public class GetTripProvenanceHandler implements ActionHandler<GetTripProvenanceResource, SingletonResult<SimileTimeLineEventContainer>> {

	private final Map4rdfDao dao;

	@Inject
	public GetTripProvenanceHandler(Map4rdfDao dao) {
		this.dao = dao;
	}

	@Override
	public SingletonResult<SimileTimeLineEventContainer> execute(GetTripProvenanceResource action, ExecutionContext ec) throws ActionException {
		String uri = action.getUri();
		if (uri == null || uri.length() == 0) {
			throw new ActionException("Invalid URI: " + uri);
		}
		try {
			SimileTimeLineEventContainer resource = dao.getProvenanceTrip(uri);
			return new SingletonResult<SimileTimeLineEventContainer>(resource);
		} catch (DaoException e) {
			throw new ActionException("Data access error", e);
		}
	}

	@Override
	public Class<GetTripProvenanceResource> getActionType() {
		return GetTripProvenanceResource.class;
	}

	@Override
	public void rollback(GetTripProvenanceResource action, SingletonResult<SimileTimeLineEventContainer> result, ExecutionContext context)
			throws ActionException {
		// nothing to do

	}
    

}
