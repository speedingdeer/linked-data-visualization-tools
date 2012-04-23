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
package es.upm.fi.dia.oeg.map4rdf.server.conf;

/**
 * @author Alexander De Leon
 */
public class Constants {

	public static final String CONFIGURATION_FILE = "/WEB-INF/configuration.properties";

	public static final String FACET_CONFIGURATION_FILE = "/WEB-INF/facets.ttl";

	public static enum GeometryModel {
		/**
		 * OEG models geometries explicitly. The resource and its geometries are
		 * linked using the http://www.w3.org/2003/01/geo/wgs84_pos#geometry
		 * perdicate.
		 */
		OEG,
		/**
		 * The DBPEDIA model does NOT make distinction between the resource and
		 * its geometry. The resource is also a point and the predicates
		 * http://www.w3.org/2003/01/geo/wgs84_pos#lat and
		 * http://www.w3.org/2003/01/geo/wgs84_pos#long are used to assert its
		 * position.
		 */
		DBPEDIA;
	}

}
