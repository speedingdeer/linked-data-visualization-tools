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
package es.upm.fi.dia.oeg.map4rdf.server.command.db;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.db.Login;
import es.upm.fi.dia.oeg.map4rdf.client.action.db.Logout;
import es.upm.fi.dia.oeg.map4rdf.server.db.SQLconnector;

/**
 * @author Filip
 */
public class LogoutHandler implements
		ActionHandler<Logout, SingletonResult<Boolean>> {
	
	Provider<HttpSession> sessionProvider; 
	Provider<HttpServletRequest> requestProvider; 
	Provider<HttpServletResponse> responseProvider;
	SQLconnector sqlConnector;
	
	@Override
	public Class<Logout> getActionType() {
		return Logout.class;
	}
	
	@Inject
	public LogoutHandler(Provider<HttpSession> sessionProvider, Provider<HttpServletRequest> requestProvider, 
			Provider<HttpServletResponse> responseProvider, SQLconnector sqlConnector) {
		this.sessionProvider=sessionProvider;
		this.requestProvider=requestProvider;
		this.responseProvider=responseProvider;
		this.sqlConnector=sqlConnector;
	}

	@Override
	public SingletonResult<Boolean> execute(Logout action,
			ExecutionContext context) throws ActionException {
			sessionProvider.get().removeAttribute("admin");
			return new SingletonResult<Boolean>(true);
	}

	@Override
	public void rollback(Logout action, SingletonResult<Boolean> result,
			ExecutionContext context) throws ActionException {
		// TODO Auto-generated method stub
		
	}
}
