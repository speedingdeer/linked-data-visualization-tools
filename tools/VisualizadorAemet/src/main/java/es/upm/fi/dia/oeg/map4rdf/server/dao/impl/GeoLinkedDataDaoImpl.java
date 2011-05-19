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
package es.upm.fi.dia.oeg.map4rdf.server.dao.impl;

import java.util.ArrayList;
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

import es.upm.fi.dia.oeg.map4rdf.server.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.Geo;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.GeoLinkedDataEsOwlVocabulary;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.Scovo;
import es.upm.fi.dia.oeg.map4rdf.share.AemetObs;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResourceOverlay;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.Intervalo;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLineBean;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;
import es.upm.fi.dia.oeg.map4rdf.share.PolygonBean;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoItinerary;
import es.upm.fi.dia.oeg.map4rdf.share.Year;

/**
 * @author Alexander De Leon
 */
public class GeoLinkedDataDaoImpl implements Map4rdfDao {

	private static final Logger LOG = Logger.getLogger(GeoLinkedDataDaoImpl.class);

	private final String endpointUri;

	@Inject
	public GeoLinkedDataDaoImpl(@Named(ParameterNames.ENDPOINT_URL) String endpointUri) {
		this.endpointUri = endpointUri;
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox) throws DaoException {
		return getGeoResources(boundingBox, null);
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints, int max)
			throws DaoException {
		return getGeoResources(boundingBox, constraints, new Integer(max));
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints)
			throws DaoException {
		return getGeoResources(boundingBox, constraints, null);
	}

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
					String geoUri = solution.getResource("geo").getURI();
					String geoTypeUri = solution.getResource("geoType").getURI();
					GeoResource resource = result.get(uri);
					if (resource == null) {
						resource = new GeoResource(uri, getGeometry(geoUri, geoTypeUri));
						result.put(uri, resource);
					} else if (!resource.hasGeometry(geoUri)) {
						resource.addGeometry(getGeometry(geoUri, geoTypeUri));
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
	};

	@Override
	public GeoResource getGeoResource(String uri) throws DaoException {
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetResourceQuery(uri));

		try {
			ResultSet queryResult = execution.execSelect();
			GeoResource resource = null;
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String geoUri = solution.getResource("geo").getURI();
					String geoTypeUri = solution.getResource("geoType").getURI();
					if (resource == null) {
						resource = new GeoResource(uri, getGeometry(geoUri, geoTypeUri));
					} else if (!resource.hasGeometry(geoUri)) {
						resource.addGeometry(getGeometry(geoUri, geoTypeUri));
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
	public List<GeoResourceOverlay> getGeoResourceOverlays(StatisticDefinition statisticDefinition,
			BoundingBox boundingBox, Set<FacetConstraint> constraints) throws DaoException {

		List<GeoResourceOverlay> result = new ArrayList<GeoResourceOverlay>();

		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri,
				createGetStatisticsQuery(boundingBox, statisticDefinition));

		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				String uri = solution.getResource("r").getURI();
				GeoResource resource = getGeoResource(uri);
				String statUri = solution.getResource("stat").getURI();
				double statValue = solution.getLiteral("statValue").getDouble();
				GeoResourceOverlay overlay = new GeoResourceOverlay(statUri, resource, statValue);
				result.add(overlay);
			}
			return result;
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	@Override
	public List<Facet> getFacets(String predicateUri, BoundingBox boundingBox) throws DaoException {
		Map<String, Facet> result = new HashMap<String, Facet>();

		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("select distinct ?class ?label where { ");
		queryBuffer.append("?x <" + Geo.geometry + "> ?g. ");
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

	public Geometry getGeometry(String geoUri, String geoType) throws DaoException {
		if (geoType.equals(Geo.Point.getURI())) {
			return getPoint(geoUri);
		}
		if (geoType.equals(GeoLinkedDataEsOwlVocabulary.Curva.getURI())) {
			return getPolyline(geoUri);
		}
		if (geoType.equals(GeoLinkedDataEsOwlVocabulary.Poligono.getURI())) {
			return getPolygon(geoUri);
		}
		return null;
	}

	public PolyLine getPolyline(String uri) throws DaoException {
		List<Point> points = getGeometryPoints(uri);
		return points.isEmpty() ? null : new PolyLineBean(uri, points);
	}

	public Polygon getPolygon(String uri) throws DaoException {
		List<Point> points = getGeometryPoints(uri);
		return points.isEmpty() ? null : new PolygonBean(uri, points);
	}

	public List<Point> getGeometryPoints(String uri) throws DaoException {
		List<Point> points = new ArrayList<Point>();
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetGeometryPointsQuery(uri));
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String pointUri = solution.getResource("p").getURI();
					points.add(getPoint(pointUri));
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
		return points;
	}

	public Point getPoint(String uri) throws DaoException {
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetPointQuery(uri));
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();
					return new PointBean(uri, lng, lat);
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
		return null;
	}

	@Override
	public List<Year> getYears(String datasetUri) throws DaoException {
		List<Year> years = new ArrayList<Year>();
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetYearsQuery(datasetUri));
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String yearUri = solution.getResource("yearUri").getURI();
					int yearVal = solution.getLiteral("yearVal").getInt();
					years.add(new Year(yearUri, yearVal));
				} catch (NumberFormatException e) {
					LOG.warn("Invalid literal value: " + e.getMessage());
				}
			}
			return years;
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	@Override
	public List<Resource> getStatisticDatasets() throws DaoException {
		Map<String, Resource> result = new HashMap<String, Resource>();
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetStatisticDatasetsQuery());
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();

				String uri = solution.getResource("uri").getURI();
				Resource resource = result.get(uri);
				if (resource == null) {
					resource = new Resource(uri);
					result.put(uri, resource);
				}
				if (solution.contains("label")) {
					Literal labelLiteral = solution.getLiteral("label");
					resource.addLabel(labelLiteral.getLanguage(), labelLiteral.getString());
				}

			}
			return new ArrayList<Resource>(result.values());
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	private String createGetStatisticDatasetsQuery() {
		StringBuilder query = new StringBuilder("SELECT DISTINCT ?uri ?label WHERE { ");
		query.append("?uri <" + RDF.type + ">  <" + Scovo.Dataset + "> . ");
		query.append("OPTIONAL { ?uri <" + RDFS.label + "> ?label } .");
		query.append("}");
		return query.toString();
	}

	private String createGetYearsQuery(String datasetUri) {
		StringBuilder query = new StringBuilder("SELECT DISTINCT ?yearUri ?yearVal WHERE { ");
		query.append("_:stat <" + Scovo.dataset + ">  <" + datasetUri + "> . ");
		query.append("_:stat <" + Scovo.dimension + ">  ?yearUri . ");
		query.append("?yearUri <" + RDF.type + ">  <" + GeoLinkedDataEsOwlVocabulary.Anyo + "> . ");
		query.append("?yearUri <" + RDF.value + ">  ?yearVal . ");
		query.append("}");
		return query.toString();
	}

	private String createGetGeometryPointsQuery(String uri) {
		StringBuilder query = new StringBuilder("SELECT ?p WHERE { ");
		query.append("<" + uri + "> <" + GeoLinkedDataEsOwlVocabulary.formadoPor + ">  ?p . ");
		query.append("?p <" + RDF.type + ">  <" + Geo.Point + "> . ");
		query.append("?p <" + GeoLinkedDataEsOwlVocabulary.orden + ">  ?o . ");
		query.append("} ORDER BY ASC(?o)");
		return query.toString();
	}

	private String createGetPointQuery(String uri) {
		StringBuilder query = new StringBuilder("SELECT ?lat ?lng WHERE { ");
		query.append("<" + uri + "> <" + Geo.lat + ">  ?lat ; ");
		query.append("<" + Geo.lng + "> ?lng . ");
		query.append("}");
		return query.toString();
	}

	private String createGetResourcesQuery(BoundingBox boundingBox, Set<FacetConstraint> constraints, Integer limit) {
		StringBuilder query = new StringBuilder("SELECT distinct ?r ?label ?geo ?geoType ");
		query.append("WHERE { ");
		query.append("?r <" + Geo.geometry + ">  ?geo. ");
		query.append("?geo <" + RDF.type + "> ?geoType . ");
		query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
		if (constraints != null) {
			for (FacetConstraint constraint : constraints) {
				query.append("{ ?r <" + constraint.getFacetId() + "> <" + constraint.getFacetValueId() + ">. } UNION");
			}
			query.delete(query.length() - 5, query.length());
		}
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	private String createGetResourcesFallbackQuery(BoundingBox boundingBox, Set<FacetConstraint> constraints,
			Integer limit) {
		StringBuilder query = new StringBuilder("SELECT distinct ?r ?label ");
		query.append("WHERE { ");
		query.append("?r <" + Geo.lat + ">  _:lat. ");
		query.append("?r <" + Geo.lng + "> _:lng . ");
		query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
		if (constraints != null) {
			for (FacetConstraint constraint : constraints) {
				query.append("{ ?r <" + constraint.getFacetId() + "> <" + constraint.getFacetValueId() + ">. } UNION");
			}
			query.delete(query.length() - 5, query.length());
		}
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	private String createGetStatisticsQuery(BoundingBox boundingBox, StatisticDefinition statisticDefinition) {
		StringBuilder query = new StringBuilder("SELECT distinct ?r ?stat ?statValue ");
		query.append("WHERE { ");
		query.append("?stat <" + Scovo.dimension + "> ?r. ");
		query.append("?r <" + Geo.geometry + "> _:geo. ");
		query.append("?stat <" + Scovo.dataset + "> <" + statisticDefinition.getDataset() + "> .");
		for (String dimension : statisticDefinition.getDimensions()) {
			query.append("?stat <" + Scovo.dimension + "> <" + dimension + ">. ");
		}
		query.append("?stat <" + RDF.value + "> ?statValue. ");

		query.append("} LIMIT 1000");
		return query.toString();
	}

	private String createGetResourceQuery(String uri) {
		StringBuilder query = new StringBuilder("SELECT ?label ?geo ?geoType ");
		query.append("WHERE { ");
		query.append("<" + uri + "> <" + Geo.geometry + ">  ?geo. ");
		query.append("?geo <" + RDF.type + "> ?geoType . ");
		query.append("OPTIONAL { <" + uri + "> <" + RDFS.label + "> ?label } .");
		query.append("}");
		return query.toString();
	}

	@Override
	public GeoResource getDatosObservacion(String uri) throws DaoException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public GeoResource getDatosGuiasViajes(String uri) throws DaoException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public WebNMasUnoItinerary getItinerary(String uri) throws DaoException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<AemetObs> getObservations(String stationUri, String propertyUri, Intervalo start, Intervalo end) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
