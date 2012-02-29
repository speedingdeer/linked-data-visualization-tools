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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.gwt.core.client.GWT;
import com.google.inject.Guice;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectDescriptions;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectLabel;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SaveRdfFile;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.server.db.SQLconnector;
import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

/**
 * @author Filip
 */
public class SaveRdfFIleHandler implements
		ActionHandler<SaveRdfFile, SingletonResult<String>> {

	@Override
	public Class<SaveRdfFile> getActionType() {
		return SaveRdfFile.class;
	}
	
	@Inject
	public SaveRdfFIleHandler() {
	}

	@Override
	public SingletonResult<String> execute(SaveRdfFile action,
			ExecutionContext context) throws ActionException {
		
		//take path
		SQLconnector dbConnector = Guice.createInjector().getInstance(SQLconnector.class);
		String path = dbConnector.getValue(ParameterNames.RDF_STORE_PATH);
    	
		File file = new File(path + action.getFileName());
    	if (file.exists()) {
    		return new SingletonResult<String>("ERROR");
    	}	
    	
    	BufferedWriter output;
    	
		try {
			output = new BufferedWriter(new FileWriter(file));
	    	output.write(action.getFileContent());
	    	output.close();
	    	
		} catch (IOException e) {
			e.printStackTrace();
			return new SingletonResult<String>("ERROR");
		}
		
		return new SingletonResult<String>("");
	}

	@Override
	public void rollback(SaveRdfFile action,
			SingletonResult<String> result,
			ExecutionContext context) throws ActionException {
		// TODO Auto-generated method stub
	}
}
