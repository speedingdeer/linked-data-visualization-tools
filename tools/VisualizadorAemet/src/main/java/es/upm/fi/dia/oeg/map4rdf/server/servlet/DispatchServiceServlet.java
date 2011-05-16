/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informática, Universidad 
 * Politécnica de Madrid, Spain
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
package es.upm.fi.dia.oeg.map4rdf.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.Dispatch;

import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This is a HACK to get the GWT application working behind the Apache Proxy
 * 
 * @author Alexander De Leon
 */
@Singleton
public class DispatchServiceServlet extends net.customware.gwt.dispatch.server.service.DispatchServiceServlet {

	@Inject
	public DispatchServiceServlet(Dispatch dispatch) {
		super(dispatch);
	}

	@Override
	protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL,
			String strongName) {
		return loadSerializationPolicy(this, request, moduleBaseURL, strongName);
	}

	/**
	 * Used by HybridServiceServlet.
	 */
	static SerializationPolicy loadSerializationPolicy(HttpServlet servlet, HttpServletRequest request,
			String moduleBaseURL, String strongName) {
		// The serialization policy path depends only by contraxt path
		String contextPath = request.getContextPath();

		SerializationPolicy serializationPolicy = null;

		String contextRelativePath = "/es.upm.fi.dia.oeg.map4rdf.map4rdf/";

		String serializationPolicyFilePath = SerializationPolicyLoader
				.getSerializationPolicyFileName(contextRelativePath + strongName);

		// Open the RPC resource file and read its contents.
		InputStream is = servlet.getServletContext().getResourceAsStream(serializationPolicyFilePath);
		try {
			if (is != null) {
				try {
					serializationPolicy = SerializationPolicyLoader.loadFromStream(is, null);
				} catch (ParseException e) {
					servlet.log("ERROR: Failed to parse the policy file '" + serializationPolicyFilePath + "'", e);
				} catch (IOException e) {
					servlet.log("ERROR: Could not read the policy file '" + serializationPolicyFilePath + "'", e);
				}
			} else {
				String message = "ERROR: The serialization policy file '" + serializationPolicyFilePath
						+ "' was not found; did you forget to include it in this deployment?";
				servlet.log(message);
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// Ignore this error
				}
			}
		}

		return serializationPolicy;
	}

}
