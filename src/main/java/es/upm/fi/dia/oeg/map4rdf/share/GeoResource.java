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
package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author Alexander De Leon
 */
public class GeoResource extends Resource implements Serializable {

	private HashMap<String, Geometry> geometries;

	GeoResource() {
		// for serialization
	}

	public GeoResource(String uri) {
		super(uri);
		geometries = new HashMap<String, Geometry>();
	}

	public GeoResource(String uri, Geometry geometry) {
		this(uri);
		addGeometry(geometry);
	}

	public Geometry getFirstGeometry() {
		if (geometries.isEmpty()) {
			return null;
		}
		return geometries.entrySet().iterator().next().getValue();
	}

	public boolean hasGeometry(String geometryUri) {
		return geometries.containsKey(geometryUri);
	}

	public void addGeometry(Geometry geometry) {
		geometries.put(geometry.getUri(), geometry);
	}

	public void removeGeometry(Geometry geometry) {
		geometries.remove(geometry);
	}

	public Collection<Geometry> getGeometries() {
		return geometries.values();
	}

	public boolean isMultiGeometry() {
		if (geometries == null || geometries.size() == 1) {
			return false;
		} else {
			return true;
		}
	}
}
