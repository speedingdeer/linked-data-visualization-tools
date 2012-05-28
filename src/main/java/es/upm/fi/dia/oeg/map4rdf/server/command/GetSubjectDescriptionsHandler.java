/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informetica, Universidad 
 * Politecnica de Madrid, Spain
 * 
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

import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectDescriptions;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;

/**
 * @author Filip
 */
public class GetSubjectDescriptionsHandler implements
		ActionHandler<GetSubjectDescriptions, ListResult<SubjectDescription>> {
	
	private final Map4rdfDao dao;

	@Override
	public Class<GetSubjectDescriptions> getActionType() {
		return GetSubjectDescriptions.class;
	}
	
	@Inject
	public GetSubjectDescriptionsHandler(Map4rdfDao dao) {
		this.dao = dao;
	}

	@Override
	public ListResult<SubjectDescription> execute(GetSubjectDescriptions action,
			ExecutionContext context) throws ActionException {
	
			List<SubjectDescription> descriptions;
			try {
				descriptions = dao.getSubjectDescription(action.getSubject());
			} catch (DaoException e) {
				e.printStackTrace();
				return new ListResult<SubjectDescription>();
			}
			return new ListResult<SubjectDescription>(descriptions);
	}

	@Override
	public void rollback(GetSubjectDescriptions action,
			ListResult<SubjectDescription> result,
			ExecutionContext context) throws ActionException {
		// TODO Auto-generated method stub
	}
}
