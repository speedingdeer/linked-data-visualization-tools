/**
 * Copyright (c) 2011 Alexander De Leon Battista
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.upm.fi.dia.oeg.map4rdf.server.command;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetAemetObsForProperty;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.share.AemetObs;

/**
 * @author Alexander De Leon
 */
public class GetAemetObsForPropertyHandler implements ActionHandler<GetAemetObsForProperty, ListResult<AemetObs>> {

	private final Map4rdfDao dao;

	@Inject
	public GetAemetObsForPropertyHandler(Map4rdfDao dao) {
		this.dao = dao;
	}

	@Override
	public Class<GetAemetObsForProperty> getActionType() {
		return GetAemetObsForProperty.class;
	}

	@Override
	public ListResult<AemetObs> execute(GetAemetObsForProperty action, ExecutionContext context) throws ActionException {
		return new ListResult<AemetObs>(dao.getObservations(action.getStationUri(), action.getPropertyUri(),
				action.getStart(), action.getEnd()));
	}

	@Override
	public void rollback(GetAemetObsForProperty action, ListResult<AemetObs> result, ExecutionContext context)
			throws ActionException {
		// nothing to do
	}

}
