/**
 * Copyright (c) 2012 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informtica, Universidad 
 * Politcnica de Madrid, Spain
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
package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.io.Serializable;
import java.util.Set;

import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;

/**
 * @author Jonathan Gonzalez (jonathan@jonbaraq.eu)
 */
public class GetGeoResourcesFromRdfModelBase implements Serializable {

	private BoundingBox boundingBox;
	private String modelConfiguration;

	GetGeoResourcesFromRdfModelBase() {
		// for serialization
	}

	public GetGeoResourcesFromRdfModelBase(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public String getModelConfiguration() {
		return modelConfiguration;
	}

	public void setModelConfiguration(String modelConfiguration) {
		this.modelConfiguration = modelConfiguration;
	}

}
