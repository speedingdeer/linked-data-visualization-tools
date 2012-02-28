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
package es.upm.fi.dia.oeg.map4rdf.server.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.server.util.DescriptionsFactory;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.Geo;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResourceOverlay;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.Year;

/**
 * @author Alexander De Leon
 */
public class DbPediaDaoImpl implements Map4rdfDao {

	private static final Logger LOG = Logger.getLogger(DbPediaDaoImpl.class);

	private final String endpointUri;

	@Inject
	public DbPediaDaoImpl(@Named(ParameterNames.ENDPOINT_URL) String endpointUri) {
		this.endpointUri = endpointUri;
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox) throws DaoException {
		return getGeoResources(boundingBox, null);
	}

	@Override
	public GeoResource getGeoResource(String uri) throws DaoException {
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetResourceQuery(uri));

		try {
			ResultSet queryResult = execution.execSelect();
			GeoResource resource = null;
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();

					if (resource == null) {
						resource = new GeoResource(uri, new PointBean(uri, lng, lat));
					}
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(), labelLiteral.getString());
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: " + e.getMessage());
				}
			}
			return resource;
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints)
			throws DaoException {
		return getGeoResources(boundingBox, constraints, null);
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints, int max)
			throws DaoException {
		return getGeoResources(boundingBox, constraints, new Integer(max));
	}

	@Override
	public List<GeoResourceOverlay> getGeoResourceOverlays(StatisticDefinition statisticDefinition,
			BoundingBox boundingBox, Set<FacetConstraint> constraints) throws DaoException {
		// TODO What can be done here?
		return Collections.emptyList();
	}
	
	@Override
	public List<SubjectDescription> getSubjectDescription(String subject)
			throws DaoException {
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetSubjectDescriptionString(subject));
		ArrayList<SubjectDescription> result = new ArrayList<SubjectDescription>();
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				result.add(DescriptionsFactory.getSubjectDescription(solution));
			}
			return result;
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	@Override
	public String getLabel(String uri) throws DaoException {
		String result = "";
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetLabelQuery(uri));
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				Literal l = solution.getLiteral("?label");
				result = l.getLexicalForm();
			}
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
		return result;
	}
	

	@Override
	public List<Facet> getFacets(String predicateUri, BoundingBox boundingBox) throws DaoException {
		Map<String, Facet> result = new HashMap<String, Facet>();

		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("select distinct ?class ?label where { ");
		queryBuffer.append("?x <" + Geo.lat + "> _:lat. ");
		queryBuffer.append("?x <" + Geo.lng + "> _:lng. ");
		queryBuffer.append("?x <" + predicateUri + "> ?class . ");
		queryBuffer.append("optional {?class <" + RDFS.label + "> ?label . }}");

		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, queryBuffer.toString());

		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				String uri = solution.getResource("class").getURI();
				Facet value = null;
				if (result.containsKey(uri)) {
					value = result.get(uri);
				} else {
					value = new Facet(uri);
					result.put(uri, value);
				}
				if (solution.contains("label")) {
					Literal label = solution.getLiteral("label");
					value.addLabel(label.getLanguage(), label.getString());
				}
			}
			return new ArrayList<Facet>(result.values());
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	@Override
	public List<Year> getYears(String datasetUri) throws DaoException {
		// TODO not applicable
		return Collections.emptyList();
	}

	@Override
	public List<Resource> getStatisticDatasets() throws DaoException {
		// TODO What can be done here?
		return Collections.emptyList();
	}

	/* --------------------- helper methods --- */

	private List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints, Integer max)
			throws DaoException {
		// TODO: use location to restrict the query to the specifies geographic
		// area.

		HashMap<String, GeoResource> result = new HashMap<String, GeoResource>();

		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri,
				createGetResourcesQuery(boundingBox, constraints, max));

		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String uri = solution.getResource("r").getURI();
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();

					GeoResource resource = result.get(uri);
					if (resource == null) {
						resource = new GeoResource(uri, new PointBean(uri, lng, lat));
						result.put(uri, resource);
					}
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(), labelLiteral.getString());
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: " + e.getMessage());
				}
			}

			return new ArrayList<GeoResource>(result.values());
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	/**
	 * @param boundingBox
	 * @param constraints
	 * @param max
	 * @return
	 */
	
	private String createGetResourcesQuery(BoundingBox boundingBox, Set<FacetConstraint> constraints, Integer limit) {
		StringBuilder query = new StringBuilder("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT distinct ?r ?label ?lat ?lng ");
		query.append("WHERE { ");
		query.append("?r <" + Geo.lat + "> ?lat. ");
		query.append("?r <" + Geo.lng + "> ?lng . ");
		query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
		if (constraints != null) {
			for (FacetConstraint constraint : constraints) {
				query.append("{ ?r <" + constraint.getFacetId() + "> <" + constraint.getFacetValueId() + ">. } UNION");
			}
			query.delete(query.length() - 5, query.length());
		}
		
		//filters
		if (boundingBox!=null) {
			query = addBoundingBoxFilter(query, boundingBox);
		}
		
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}
	

	private String createGetResourceQuery(String uri) {
		StringBuilder query = new StringBuilder("SELECT distinct ?lat ?lng ?label ");
		query.append("WHERE { ");
		query.append("<" + uri + "> <" + Geo.lat + "> ?lat. ");
		query.append("<" + uri + "> <" + Geo.lng + "> ?lng . ");
		query.append("OPTIONAL { <" + uri + "> <" + RDFS.label + "> ?label } .");
		query.append("}");
		return query.toString();
	}

	private String createGetLabelQuery(String uri) {
		StringBuilder query = new StringBuilder("SELECT ?label WHERE {");
		query.append(" <" + uri +"> <" + RDFS.label + "> ?label.");
		query.append("}");
		return query.toString();
	}
	
	private String createGetSubjectDescriptionString(String subject) {
		StringBuilder query = new StringBuilder("SELECT ?p ?o WHERE {");
		query.append("<" +subject+ ">");
		query.append(" ?p ?o .");
		query.append("}");
		return query.toString();
	}

	private StringBuilder addBoundingBoxFilter(StringBuilder query, BoundingBox boundingBox) {
		query.append(" FILTER(");
	    query.append("(");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getTop().getY() + ")" + "-" + "(" + boundingBox.getLeft().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getLeft().getX() + ")" + "-" + "(" + boundingBox.getTop().getX() + "))"+ "+");
		query.append("((" + boundingBox.getTop().getX() + ")*(" + boundingBox.getLeft().getY() + ") - (" + boundingBox.getTop().getY() + ")*(" + boundingBox.getLeft().getX() + "))");
		query.append(") >= 0");
		
		query.append("&&");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getLeft().getY() + ")" + "-" + "(" + boundingBox.getRight().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getRight().getX() + ")" + "-" + "(" + boundingBox.getLeft().getX() + "))"+ "+");
		query.append("((" + boundingBox.getLeft().getX() + ")*(" + boundingBox.getRight().getY() + ") - (" + boundingBox.getLeft().getY() + ")*(" + boundingBox.getRight().getX() + "))");
		query.append(") >= 0");
		
		query.append("&&");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getRight().getY() + ")" + "-" + "(" + boundingBox.getTop().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getTop().getX() + ")" + "-" + "(" + boundingBox.getRight().getX() + "))"+ "+");
		query.append("((" + boundingBox.getRight().getX() + ")*(" + boundingBox.getTop().getY() + ") - (" + boundingBox.getRight().getY() + ")*(" + boundingBox.getTop().getX() + "))");
		query.append(") >= 0");
		
		query.append(") || (");
				
		//d1 = px*(ay-by) + py*(bx-ax) + (ax*by-ay*bx);        
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getBottom().getY() + ")" + "-" + "(" + boundingBox.getLeft().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getLeft().getX() + ")" + "-" + "(" + boundingBox.getBottom().getX() + "))"+ "+");
		query.append("((" + boundingBox.getBottom().getX() + ")*(" + boundingBox.getLeft().getY() + ") - (" + boundingBox.getBottom().getY() + ")*(" + boundingBox.getLeft().getX() + "))");
		query.append(") >= 0");
		
		query.append("&&");
		//d2 = px*(by-cy) + py*(cx-bx) + (bx*cy-by*cx);
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getLeft().getY() + ")" + "-" + "(" + boundingBox.getRight().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getRight().getX() + ")" + "-" + "(" + boundingBox.getLeft().getX() + "))"+ "+");
		query.append("((" + boundingBox.getLeft().getX() + ")*(" + boundingBox.getRight().getY() + ") - (" + boundingBox.getLeft().getY() + ")*(" + boundingBox.getRight().getX() + "))");
		query.append(") >= 0");
		
		query.append("&&");
	    //d3 = px*(cy-ay) + py*(ax-cx) + (cx*ay-cy*ax);
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getRight().getY() + ")" + "-" + "(" + boundingBox.getBottom().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getBottom().getX() + ")" + "-" + "(" + boundingBox.getRight().getX() + "))"+ "+");
		query.append("((" + boundingBox.getRight().getX() + ")*(" + boundingBox.getBottom().getY() + ") - (" + boundingBox.getRight().getY() + ")*(" + boundingBox.getBottom().getX() + "))");
		query.append(") >= 0");
		
		query.append(") || (");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getTop().getY() + ")" + "-" + "(" + boundingBox.getLeft().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getLeft().getX() + ")" + "-" + "(" + boundingBox.getTop().getX() + "))"+ "+");
		query.append("((" + boundingBox.getTop().getX() + ")*(" + boundingBox.getLeft().getY() + ") - (" + boundingBox.getTop().getY() + ")*(" + boundingBox.getLeft().getX() + "))");
		query.append(") <= 0");
		
		query.append("&&");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getLeft().getY() + ")" + "-" + "(" + boundingBox.getRight().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getRight().getX() + ")" + "-" + "(" + boundingBox.getLeft().getX() + "))"+ "+");
		query.append("((" + boundingBox.getLeft().getX() + ")*(" + boundingBox.getRight().getY() + ") - (" + boundingBox.getLeft().getY() + ")*(" + boundingBox.getRight().getX() + "))");
		query.append(") <= 0");
		
		query.append("&&");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getRight().getY() + ")" + "-" + "(" + boundingBox.getTop().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getTop().getX() + ")" + "-" + "(" + boundingBox.getRight().getX() + "))"+ "+");
		query.append("((" + boundingBox.getRight().getX() + ")*(" + boundingBox.getTop().getY() + ") - (" + boundingBox.getRight().getY() + ")*(" + boundingBox.getTop().getX() + "))");
		query.append(") <= 0");
		
		query.append(") || (");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getBottom().getY() + ")" + "-" + "(" + boundingBox.getLeft().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getLeft().getX() + ")" + "-" + "(" + boundingBox.getBottom().getX() + "))"+ "+");
		query.append("((" + boundingBox.getBottom().getX() + ")*(" + boundingBox.getLeft().getY() + ") - (" + boundingBox.getBottom().getY() + ")*(" + boundingBox.getLeft().getX() + "))");
		query.append(") <= 0");
		
		query.append("&&");
		//d2 = px*(by-cy) + py*(cx-bx) + (bx*cy-by*cx);
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getLeft().getY() + ")" + "-" + "(" + boundingBox.getRight().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getRight().getX() + ")" + "-" + "(" + boundingBox.getLeft().getX() + "))"+ "+");
		query.append("((" + boundingBox.getLeft().getX() + ")*(" + boundingBox.getRight().getY() + ") - (" + boundingBox.getLeft().getY() + ")*(" + boundingBox.getRight().getX() + "))");
		query.append(") <= 0");
		
		query.append("&&");
	    //d3 = px*(cy-ay) + py*(ax-cx) + (cx*ay-cy*ax);
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getRight().getY() + ")" + "-" + "(" + boundingBox.getBottom().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getBottom().getX() + ")" + "-" + "(" + boundingBox.getRight().getX() + "))"+ "+");
		query.append("((" + boundingBox.getRight().getX() + ")*(" + boundingBox.getBottom().getY() + ") - (" + boundingBox.getRight().getY() + ")*(" + boundingBox.getBottom().getX() + "))");
		query.append(") <= 0");
		
		query.append(")");
		
		query.append(").");
		return query;
	}
	
}
