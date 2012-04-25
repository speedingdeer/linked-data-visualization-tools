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

import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.Geo;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoItinerary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ctc.wstx.io.EBCDICCodec;
import com.google.gwt.rpc.client.impl.EscapeUtil;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.RDFS;

import es.upm.fi.dia.oeg.map4rdf.share.AemetObs;
import es.upm.fi.dia.oeg.map4rdf.share.AemetResource;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResourceOverlay;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLineBean;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.SimileTimeLineEvent;
import es.upm.fi.dia.oeg.map4rdf.share.SimileTimeLineEventContainer;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoGuide;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoImage;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoResourceContainer;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoTrip;
import es.upm.fi.dia.oeg.map4rdf.share.Year;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Alexander De Leon. Extendido por Daniel Garijo
 */
public class DbPediaDaoImpl extends CommonDaoImpl implements Map4rdfDao {

	private static final Logger LOG = Logger.getLogger(DbPediaDaoImpl.class);

	@Inject
	public DbPediaDaoImpl(@Named(ParameterNames.ENDPOINT_URL) String endpointUri) {
		super(endpointUri);
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox)
			throws DaoException {
		return getGeoResources(boundingBox, null);
	}

	@Override
	public GeoResource getGeoResource(String uri) throws DaoException {
		QueryExecution execution = QueryExecutionFactory.sparqlService(
				endpointUri, createGetResourceQuery(uri));

		try {
			ResultSet queryResult = execution.execSelect();
			GeoResource resource = null;
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();

					if (resource == null) {
						resource = new GeoResource(uri, new PointBean(uri, lng,
								lat));
					}
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(),
								labelLiteral.getString());
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: "
							+ e.getMessage());
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
	public List<GeoResource> getGeoResources(BoundingBox boundingBox,
			Set<FacetConstraint> constraints) throws DaoException {
		return getGeoResources(boundingBox, constraints, null);
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox,
			Set<FacetConstraint> constraints, int max) throws DaoException {
		return getGeoResources(boundingBox, constraints, new Integer(max));
	}

	@Override
	public List<GeoResourceOverlay> getGeoResourceOverlays(
			StatisticDefinition statisticDefinition, BoundingBox boundingBox,
			Set<FacetConstraint> constraints) throws DaoException {
		// TODO What can be done here?
		return Collections.emptyList();
	}

	@Override
	public List<Facet> getFacets(String predicateUri, BoundingBox boundingBox)
			throws DaoException {
		Map<String, Facet> result = new HashMap<String, Facet>();
		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("select distinct ?class ?label where { ");
		// puntos
		queryBuffer.append("{?x <" + Geo.lat + "> _:lat. ");
		queryBuffer.append("?x <" + Geo.lng + "> _:lng. ");
		queryBuffer.append("?x <" + predicateUri + "> ?class . ");
		queryBuffer.append("OPTIONAL {?class <" + RDFS.label + "> ?label . }}");

		// guias y aristas
		queryBuffer.append("UNION");
		queryBuffer
				.append("{?x <http://www.w3.org/2003/01/geo/wgs84_pos#location> ?loc.");
		queryBuffer.append("?x a ?class.}");

		// viajes
		queryBuffer.append("UNION");
		queryBuffer
				.append("{?x <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> ?t.");
		queryBuffer.append("?x a ?class.}");

		queryBuffer.append("}");

		/*
		 * ========== StringBuilder queryBuffer = new StringBuilder();
		 * queryBuffer.append("select distinct ?class ?label where { ");
		 * queryBuffer.append("?x <" + Geo.lat + "> _:lat. ");
		 * queryBuffer.append("?x <" + Geo.lng + "> _:lng. ");
		 * queryBuffer.append("?x <" + predicateUri + "> ?class . ");
		 * queryBuffer.append("optional {?class <" + RDFS.label +
		 * "> ?label . }}"); >>>>>>> master
		 */
		QueryExecution execution = QueryExecutionFactory.sparqlService(
				endpointUri, queryBuffer.toString());

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

	// <<<<<<< HEAD
	/**
	 * private List<GeoResource> getGeoResources(BoundingBox boundingBox,
	 * Set<FacetConstraint> constraints, Integer max) ======= private
	 * List<GeoResource> getGeoResources(BoundingBox boundingBox,
	 * Set<FacetConstraint> constraints, Integer max) >>>>>>> master throws
	 * DaoException { // TODO: use location to restrict the query to the
	 * specifies geographic // area.
	 * 
	 * HashMap<String, GeoResource> result = new HashMap<String, GeoResource>();
	 * 
	 * QueryExecution execution =
	 * QueryExecutionFactory.sparqlService(endpointUri,
	 * createGetResourcesQuery(boundingBox, constraints, max));
	 * 
	 * try { ResultSet queryResult = execution.execSelect(); while
	 * (queryResult.hasNext()) { QuerySolution solution = queryResult.next();
	 * try { String uri = solution.getResource("r").getURI(); double lat =
	 * solution.getLiteral("lat").getDouble(); double lng =
	 * solution.getLiteral("lng").getDouble();
	 * 
	 * GeoResource resource = result.get(uri); if (resource == null) { resource
	 * = new GeoResource(uri, new PointBean(uri, lng, lat)); result.put(uri,
	 * resource); } if (solution.contains("label")) { Literal labelLiteral =
	 * solution.getLiteral("label"); <<<<<<< HEAD
	 * resource.addLabel(labelLiteral.getLanguage(),
	 * labelLiteral.getString()+"NANANA"); } } catch (NumberFormatException e) {
	 * LOG.warn("Invalid Latitud or Longitud value: " + e.getMessage()); } }
	 * 
	 * return new ArrayList<GeoResource>(result.values()); } catch (Exception e)
	 * { throw new DaoException("Unable to execute SPARQL query", e); } finally
	 * { execution.close(); } }
	 **/

	/**
	 * Metodo de prueba para llamar a mis GeoResources
	 */
	private List<GeoResource> getGeoResources(BoundingBox boundingBox,
			Set<FacetConstraint> constraints, Integer max) throws DaoException {
		// TODO: use location to restrict the query to the specifies geographic
		// area.
		// HACER: extender geoResource. Cambiar la query para anadir uri, title.
		// dibujar todos los GeoResource nuevos.

		HashMap<String, GeoResource> result = new HashMap<String, GeoResource>();

		// ORIGINAL
		// QueryExecution execution =
		// QueryExecutionFactory.sparqlService(endpointUri,
		// createGetResourcesQuery(boundingBox, constraints, max));
		// PRUEBA
		QueryExecution execution = QueryExecutionFactory.sparqlService(
				endpointUri,
				createGetResourcesQueryAdaptedWebNMasUno(boundingBox,
						constraints, max));

		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String uri = solution.getResource("r").getURI();
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();
					GeoResource resource;
					try {
						resource = (WebNMasUnoResourceContainer) result
								.get(uri);
					} catch (Exception e) {
						// No es un webNmasuno resource
						// es un aemet resource?
						try {
							resource = (AemetResource) result.get(uri);
						} catch (Exception e2) {
							// sino, es un resource normal
							resource = result.get(uri);
						}
					}
					//
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(),
								labelLiteral.getString());
					}

					// prueba para AEMET
					// if (resource == null) {
					// resource = new AemetResource(uri, new PointBean(uri, lng,
					// lat));
					// result.put(uri, resource);
					// }

					// prueba WebNmasUno (habria que separar)
					if (resource == null) {
						resource = new WebNMasUnoResourceContainer(uri,
								new PointBean(uri, lng, lat));
						result.put(uri, resource);
					}

					// it can be a small problem
					// resource.addLabel(labelLiteral.getLanguage(),
					// labelLiteral.getString());
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: "
							+ e.getMessage());
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

	private String createGetResourcesQuery(BoundingBox boundingBox,
			Set<FacetConstraint> constraints, Integer limit) {
		StringBuilder query = new StringBuilder(
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT distinct ?r ?label ?lat ?lng ");
		query.append("WHERE { ");
		query.append("?r <" + Geo.lat + "> ?lat. ");
		query.append("?r <" + Geo.lng + "> ?lng . ");
		query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
		if (constraints != null) {
			for (FacetConstraint constraint : constraints) {
				query.append("{ ?r <" + constraint.getFacetId() + "> <"
						+ constraint.getFacetValueId() + ">. } UNION");
			}
			query.delete(query.length() - 5, query.length());
		}
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	/**
	 * @param boundingBox
	 * @param constraints
	 * @param max
	 * @return
	 */
	// igual que createGetResourcesQuery, pero en vez de recuperar todos los
	// puntos,
	// anadimos solo los puntos que cumplan que son de guias o de viajes
	// (itinerarios)
	// los facets constraints nos dicen cual es (guias/viaje)
	private String createGetResourcesQueryAdaptedWebNMasUno(
			BoundingBox boundingBox, Set<FacetConstraint> constraints,
			Integer limit) {
		StringBuilder query = new StringBuilder(
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  SELECT distinct ?r ?lat ?lng ?label ");
		query.append("WHERE { ");
		query.append("?r <" + Geo.lat + "> ?lat. ");
		query.append("?r <" + Geo.lng + "> ?lng . ");
		query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
		if (constraints != null) {
			for (FacetConstraint constraint : constraints) {
				if (constraint.getFacetValueId().contains("Trip")) {
					// tratamiento especial si es un viaje
					query.append("{ ?t <" + constraint.getFacetId() + "> <"
							+ constraint.getFacetValueId() + ">.");
					query.append("?t <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> ?it.");
					query.append("?it <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?pe.");
					query.append("?pe <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPoint> ?r. } UNION");
				} else {
					if (constraint.getFacetValueId().contains("Point")) {
						query.append("{ ?r <" + constraint.getFacetId() + "> <"
								+ constraint.getFacetValueId() + ">. } UNION");
					} else {
						// cualquier cosa con localizacion (guias, aristas)
						query.append("{ ?g <" + constraint.getFacetId() + "> <"
								+ constraint.getFacetValueId() + ">. ");
						query.append("?g <http://www.w3.org/2003/01/geo/wgs84_pos#location> ?r. } UNION");

					}
				}
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

	/**
	 * Variacion de la query de recuperacion de recursos original para que
	 * devuelva Titulos de guias y viajes asociados a un recurso
	 */
	private String createGetGuidesTripsQuery(Integer limit, String uri) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?noticia ?title ?url ?dateG ?trip ?tripTitle ?tripURL ?it ?dateV ");
		query.append("WHERE { ");
		query.append("{?noticia <http://www.w3.org/2003/01/geo/wgs84_pos#location> "
				+ "<" + uri + "> . ");
		query.append("?noticia a <http://webenemasuno.linkeddata.es/ontology/OPMO/Guide>.");
		query.append("OPTIONAL {?noticia <http://rdfs.org/sioc/ns#title> ?title . }");
		query.append("OPTIONAL {?noticia <http://rdfs.org/sioc/ns#created_at> ?dateG . }");
		query.append("OPTIONAL {?noticia <http://openprovenance.org/model/opmo#pname> ?url . }}");
		query.append("UNION");
		// query.append("{?t a <http://webenemasuno.linkeddata.es/ontology/OPMO/Trip>.");
		query.append("{?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> ?it. ");
		query.append("?it <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?part. ");
		query.append("?part <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPoint> "
				+ "<" + uri + "> .");
		query.append("OPTIONAL{?trip <http://openprovenance.org/model/opmo#pname> ?tripURL. }");
		query.append("OPTIONAL {?trip <http://rdfs.org/sioc/ns#created_at> ?dateV . }");
		query.append("OPTIONAL{?trip <http://purl.org/dc/terms/title> ?tripTitle. }}");
		/**
		 * select distinct ?t where {?t a
		 * <http://webenemasuno.linkeddata.es/ontology/OPMO/Trip>. ?t
		 * <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> ?it.
		 * ?it <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?part.
		 * ?part <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPoint>
		 * ?uriResource} http://purl.org/dc/terms/title
		 */

		// if (constraints != null) {
		// for (FacetConstraint constraint : constraints) {
		// query.append("{ ?r <" + constraint.getFacetId() + "> <" +
		// constraint.getFacetValueId() + ">. } UNION");
		// }
		// query.delete(query.length() - 5, query.length());
		// }

		// filters
		// if (boundingBox!=null) {
		// query = addBoundingBoxFilter(query, boundingBox);
		// }

		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	private String createGetObs(Integer limit, String uri) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?obs ?estacion ?prov ?alt ?qv ?vv ?dv ?tIni ?tFin ?hr ?in ?vm ?est ?rviento ?qrviento ?ta ?prec ?tpr ?pres ?raglob ?presnMar ");
		query.append("WHERE { ");
		query.append("?estacion <http://www.w3.org/2003/01/geo/wgs84_pos#location> "
				+ "<" + uri + "> . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/property/deEstacion> ?estacion . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/property/provincia> ?prov . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/property/ALT> ?alt . ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/QVV10m> ?qv }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/VV10m> ?vv }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/DV10m> ?dv }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/tiempo_inicio> ?tIni }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/tiempo_fin> ?tFin }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/HR> ?hr }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/indsinop> ?in }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/VMAX10m> ?vm }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/DMAX10m> ?est }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/RVIENTO> ?rviento }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/QRVIENTO> ?qrviento }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/TA> ?ta }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/PREC> ?prec }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/TPR> ?tpr }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/PRES> ?pres }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/RAGLOB> ?raglob }. ");
		query.append("OPTIONAL {?obs <http://aemet.linkeddata.es/ontology/property/PRES_nmar> ?presnMar }. ");

		// if (constraints != null) {
		// for (FacetConstraint constraint : constraints) {
		// query.append("{ ?r <" + constraint.getFacetId() + "> <" +
		// constraint.getFacetValueId() + ">. } UNION");
		// }
		// query.delete(query.length() - 5, query.length());
		// }
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	private String createGetResourceQuery(String uri) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?lat ?lng ?label ");
		query.append("WHERE { ");
		query.append("<" + uri + "> <" + Geo.lat + "> ?lat. ");
		query.append("<" + uri + "> <" + Geo.lng + "> ?lng . ");
		query.append("OPTIONAL { <" + uri + "> <" + RDFS.label + "> ?label } .");
		query.append("}");
		return query.toString();
	}

	@Override
	public GeoResource getDatosObservacion(String uri) throws DaoException {
		// throw new UnsupportedOperationException("Not supported yet.");
		// Date d = new Date();
		AemetResource aemetR = new AemetResource();
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetObs(10, uri)); // cogemos las 10 ultimas
		ResultSet queryResult2 = exec2.execSelect();
		while (queryResult2.hasNext()) {
			QuerySolution solution2 = queryResult2.next();
			// ?obs ?estacion ?prov ?alt ?qv ?vv ?dv ?tIni ?tFin ?hr ?in ?vm
			// ?est ?rviento ?qrviento ?ta ?prec ?tpr ?pres ?raglob ?presnMar
			// String est1 = solution2.getResource("estacion").getLocalName();
			String estacion = solution2.getResource("estacion").getLocalName();
			String obs = solution2.getResource("obs").getLocalName();
			String uriObs = solution2.getResource("obs").getURI();
			String prov = solution2.getLiteral("prov").getLexicalForm();
			String alt = solution2.getLiteral("alt").getLexicalForm();
			String qv = "Sin valor", vv = "Sin valor", dv = "Sin valor", tIni = "Sin valor", tFin = "Sin valor", hr = "Sin valor", indi = "Sin valor", vM = "Sin valor", est = "Sin valor", rviento = "Sin valor", qrviento = "Sin valor", ta = "Sin valor", prec = "Sin valor", tpr = "Sin valor", pres = "Sin valor", raglob = "Sin valor", presnMar = "Sin valor";
			if (solution2.contains("qv")) {
				qv = solution2.getLiteral("qv").getLexicalForm();
			}
			if (solution2.contains("vv")) {
				vv = solution2.getLiteral("vv").getLexicalForm();
			}
			if (solution2.contains("dv")) {
				dv = solution2.getLiteral("dv").getLexicalForm();
			}
			if (solution2.contains("tIni")) {
				tIni = solution2.getLiteral("tIni").getLexicalForm();
			}
			if (solution2.contains("tFin")) {
				tFin = solution2.getLiteral("tFin").getLexicalForm();
			}
			// // String url = solution2.getLiteral("url").getLexicalForm();
			if (solution2.contains("hr")) {
				hr = solution2.getLiteral("hr").getLexicalForm();
			}
			if (solution2.contains("in")) {
				indi = solution2.getLiteral("in").getLexicalForm();
			}
			if (solution2.contains("vm")) {
				vM = solution2.getLiteral("vm").getLexicalForm();
			}
			if (solution2.contains("est")) {
				est = solution2.getLiteral("est").getLexicalForm();
			}
			if (solution2.contains("rviento")) {
				rviento = solution2.getLiteral("rviento").getLexicalForm();
			}
			if (solution2.contains("qrviento")) {
				qrviento = solution2.getLiteral("qrviento").getLexicalForm();
			}
			if (solution2.contains("ta")) {
				ta = solution2.getLiteral("ta").getLexicalForm();
			}
			if (solution2.contains("prec")) {
				prec = solution2.getLiteral("prec").getLexicalForm();
			}
			if (solution2.contains("tpr")) {
				tpr = solution2.getLiteral("tpr").getLexicalForm();
			}
			if (solution2.contains("pres")) {
				pres = solution2.getLiteral("pres").getLexicalForm();
			}
			if (solution2.contains("raglob")) {
				raglob = solution2.getLiteral("raglob").getLexicalForm();
			}
			if (solution2.contains("presnMar")) {
				presnMar = solution2.getLiteral("presnMar").getLexicalForm();
			}
			// ?estacion ?obs ?tIni ?tFin ?hr ?in ?vm ?est
			AemetObs observ = new AemetObs(obs, uriObs, estacion, tIni, tFin,
					hr, indi, vM, est, prov, alt, qv, vv, dv, rviento,
					qrviento, ta, prec, tpr, pres, raglob, presnMar);
			// AemetObs observ = new AemetObs(obs, tIni, tFin, hr,indi ,vM,est);
			((AemetResource) aemetR).addObs(observ);
		}

		return aemetR;

	}

	@Override
	public GeoResource getDatosGuiasViajes(String uri) throws DaoException {
		/**
		 * Add titles to resource. We do it in a separate query Because there
		 * exist more than 1k guides. i would like to do this query when you
		 * click on the resource.
		 */
		GeoResource resource = new WebNMasUnoResourceContainer();
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetGuidesTripsQuery(100, uri));
		ResultSet queryResult2 = exec2.execSelect();
		// guia
		String uriGuide = "", urlGuide = "", titleGuide = "", dateGuia = "";
		// viaje
		String uriTrip = "", titTrip = "", idIt = "", tripURL = "", dateViaje = "";

		/**
		 * ?noticia ?title ?url ?trip ?tripTitle
		 */
		while (queryResult2.hasNext()) {
			QuerySolution solution2 = queryResult2.next();
			if (solution2.contains("noticia")) {
				uriGuide = solution2.getResource("noticia").getURI();
			} else {
				uriGuide = "";
			}// reiniciamos para que en caso de que sea una guia incompleta no
				// guarde el anterior
			if (solution2.contains("title")) {
				titleGuide = solution2.getLiteral("title").getLexicalForm();
			} else {
				titleGuide = "";
			}
			if (solution2.contains("url")) {
				urlGuide = solution2.getLiteral("url").getLexicalForm();
			} else {
				urlGuide = "";
			}
			if (solution2.contains("dateG")) {
				dateGuia = solution2.getLiteral("dateG").getLexicalForm();
			} else {
				dateGuia = "";
			}
			if (solution2.contains("trip")) {
				uriTrip = solution2.getResource("trip").getURI();
			} else {
				uriTrip = "";
			}
			if (solution2.contains("it")) {
				idIt = solution2.getResource("it").getURI();
			} else {
				idIt = "";
			}
			if (solution2.contains("tripTitle")) {
				titTrip = solution2.getLiteral("tripTitle").getLexicalForm();
			} else {
				titTrip = "";
			}
			if (solution2.contains("tripURL")) {
				tripURL = solution2.getLiteral("tripURL").getLexicalForm();
			} else {
				tripURL = "";
			}
			if (solution2.contains("dateV")) {
				dateViaje = solution2.getLiteral("dateV").getLexicalForm();
			} else {
				dateViaje = "";
			}

			if (!uriGuide.equals("")) {
				WebNMasUnoGuide g = new WebNMasUnoGuide(titleGuide, urlGuide,
						uriGuide, dateGuia);
				((WebNMasUnoResourceContainer) resource)
						.addWebNMasUnoResource(g);
			} else if (!uriTrip.equals("")) {
				WebNMasUnoTrip t = new WebNMasUnoTrip(titTrip, tripURL,
						uriTrip, idIt, dateViaje);
				((WebNMasUnoResourceContainer) resource)
						.addWebNMasUnoResource(t);
			}

		}
		return resource;
	}

	@Override
	public WebNMasUnoItinerary getItinerary(String uriItinerario)
			throws DaoException {
		/**
		 * Returns the full itinerary of a trip. The uri is the uri of the
		 * itinerary, so the sparql query is quite easy.
		 */
		WebNMasUnoItinerary it;

		// query 1. The path & order of the itinerary.
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetItineraryQuery(1000, uriItinerario));// como mucho un
																// itinerario de
																// 1000 puntos
		ResultSet queryResult2 = exec2.execSelect();
		String order = "", point = "", title = "";
		double lat, longitude;
		ArrayList puntosOrdenados = new ArrayList();
		while (queryResult2.hasNext()) {
			QuerySolution solution2 = queryResult2.next();
			order = solution2.getLiteral("order").getLexicalForm();
			point = solution2.getResource("point").getURI();
			lat = solution2.getLiteral("lat").getDouble();
			longitude = solution2.getLiteral("long").getDouble();
			PointBean p1 = new PointBean(point, longitude, lat);
			puntosOrdenados.add(p1);
		}

		// Los puntos vienen ordenados ya por ?order (en la consulta)
		PolyLineBean p = new PolyLineBean(uriItinerario, puntosOrdenados);
		it = new WebNMasUnoItinerary(uriItinerario, p);

		// query 2: titles of the trips that have this itinerary.
		QueryExecution exec3 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetTitleTrip(1000, uriItinerario));// como mucho un
															// itinerario de
															// 1000 puntos
		ResultSet queryResult3 = exec3.execSelect();
		while (queryResult3.hasNext()) {
			QuerySolution solution2 = queryResult3.next();
			title = solution2.getLiteral("tit").getLexicalForm();
			it.addViaje(title);
		}
		return it;
	}

	private String createGetItineraryQuery(Integer limit, String uri) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?order ?point ?lat ?long ");
		query.append("WHERE{ ");
		query.append("<"
				+ uri
				+ "> <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?path.");
		query.append("?path <http://webenemasuno.linkeddata.es/ontology/OPMO/hasOrder> ?order.");
		query.append("?path <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPoint> ?point.");
		query.append("?point <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat.");
		query.append("?point <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long.");

		query.append("}");
		query.append("ORDER BY ?order");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	private String createGetTitleTrip(Integer limit, String uri) {
		StringBuilder query = new StringBuilder("SELECT distinct ?tit ");
		query.append("WHERE{ ");
		query.append("?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> <"
				+ uri + ">.");
		query.append("?trip <http://purl.org/dc/terms/title> ?tit.");

		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	@Override
	public GeoResource getDatosImgsReferencia(String uri) throws DaoException {
		WebNMasUnoResourceContainer cont = new WebNMasUnoResourceContainer();

		// query 1. The path & order of the itinerary.
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetImagesQuery(10, uri));
		ResultSet queryResult2 = exec2.execSelect();
		String uriRes = "", title = "", pname = "", nextV = "";
		// ?reference ?pname ?tit
		while (queryResult2.hasNext()) {
			QuerySolution solution2 = queryResult2.next();
			uriRes = solution2.getResource("reference").getURI();
			pname = solution2.getLiteral("pname").getLexicalForm();
			if (solution2.contains("title")) {
				title = solution2.getLiteral("title").getLexicalForm();
			}
			WebNMasUnoImage i = new WebNMasUnoImage(title, uriRes, pname);
			cont.addWebNMasUnoResource(i);
		}
		return cont;
	}

	private String createGetImagesQuery(Integer limit, String uri) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?reference ?pname ?tit ");
		query.append("WHERE{ ");
		query.append("?gen <http://openprovenance.org/model/opmo#effect> <"
				+ uri + ">.");
		query.append("?gen <http://openprovenance.org/model/opmo#cause> ?process.");
		query.append("?used <http://openprovenance.org/model/opmo#effect> ?process.");
		query.append("?used <http://openprovenance.org/model/opmo#cause> ?reference.");
		query.append("?reference a <http://webenemasuno.linkeddata.es/ontology/OPMO/Image>.");
		query.append("?reference <http://openprovenance.org/model/opmo#pname> ?pname.");
		query.append("OPTIONAL{?reference <http://metadata.net/mpeg7/mpeg7.owl#title> ?t.");
		query.append("?t <http://www.w3.org/2000/01/rdf-schema#label> ?tit}");

		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	@Override
	public SimileTimeLineEventContainer getProvenanceTrip(String uriViaje)
			throws DaoException {
		SimileTimeLineEventContainer cont = new SimileTimeLineEventContainer();

		// query 0.5: Metadata about the trip:
		QueryExecution exec = QueryExecutionFactory.sparqlService(endpointUri,// "http://localhost:8890/sparql",
				createGetTripMetadata(1000, uriViaje));
		ResultSet queryResult = exec.execSelect();
		String created = "", tit = "", pl = "", pH = "", dL = "", dH = "", tD = "", prL = "", prH = "";
		while (queryResult.hasNext()) {
			QuerySolution solution = queryResult.next();// fecha obligatoria
			ArrayList<String> features = new ArrayList();
			created = solution.getLiteral("created").getLexicalForm();
			SimileTimeLineEvent i = new SimileTimeLineEvent();
			i.setIsDuration(true);
			i.setStart(created);
			i.setLink(uriViaje);
			if (solution.contains("title")) {
				tit = solution.getLiteral("title").getLexicalForm();
				i.setTitle(tit);
			}
			if (solution.contains("pL")) {
				pl = solution.getLiteral("pL").getLexicalForm();
				features.add("Precio: Menos de " + pl + " Euros");
			}
			if (solution.contains("pH")) {
				pH = solution.getLiteral("pH").getLexicalForm();
				features.add("Precio: Más de " + pH + " Euros");
			}
			if (solution.contains("dL")) {
				dL = solution.getLiteral("dL").getLexicalForm();
				Calendar c = Calendar.getInstance();
				c.setTime(i.getStart());// tiene que existir previamente. Todo
										// viaje tiene fecha de creacion
				c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(dL) * 7);
				i.setEnd(c.getTime());
				features.add("Duración: Menos de " + dL + " semanas.");
			}
			if (solution.contains("dH")) {
				dH = solution.getLiteral("dH").getLexicalForm();
				Calendar c = Calendar.getInstance();
				c.setTime(i.getStart());// tiene que existir previamente. Todo
										// viaje tiene fecha de creacion
				c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(dH) * 7);
				i.setEnd(c.getTime());
				features.add("Duración: Más de " + pH + " semanas");
			}
			if (solution.contains("tD")) {
				tD = solution.getLiteral("tD").getLexicalForm();
				features.add("Tipo de viaje: " + tD);
			}
			if (solution.contains("prL")) {
				prL = solution.getLiteral("prL").getLexicalForm();
				features.add("Distancia: Menos de " + prL + " Km");
			}
			if (solution.contains("prH")) {
				prH = solution.getLiteral("prH").getLexicalForm();
				features.add("Distancia: Más de " + prH + " Km");
			}
			i.setDescription("Características:", features);
			cont.addEvent(i);
			System.out.println(i.toXml());
		}
		// query 1. References & next Version of the trip.
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetTripProvenance(1000, uriViaje));
		ResultSet queryResult2 = exec2.execSelect();

		// query 2: while it has Next Version, get the next version (1). Get the
		// references (2)
		String nextV = "";
		while (queryResult2.hasNext()) {
			QuerySolution solution2 = queryResult2.next();
			if (solution2.contains("nextV")) {
				nextV = solution2.getResource("nextV").getURI();
			}
			SimileTimeLineEvent s = creaEventoRefTrip(solution2, created);
			if (s != null) {
				cont.addEvent(s);
			}
		}
		// bucle de versiones
		while (!nextV.equals("")) {
			// queries for later versions
			exec2 = QueryExecutionFactory.sparqlService(endpointUri,// "http://localhost:8890/sparql",
					createGetTripProvenance(1000, nextV));
			queryResult2 = exec2.execSelect();
			nextV = "";
			while (queryResult2.hasNext()) {
				QuerySolution solution2 = queryResult2.next();
				if (solution2.contains("nextV")) {
					nextV = solution2.getResource("nextV").getURI();
				}
				SimileTimeLineEvent s = creaEventoRefTrip(solution2, created);
				if (s != null) {
					cont.addEvent(s);
				}
			}
		}
		// System.out.println(cont.toXml());
		return cont;
	}

	private SimileTimeLineEvent creaEventoRefTrip(QuerySolution qs,
			String refTime) {
		SimileTimeLineEvent i = new SimileTimeLineEvent();
		// ?reference ?time ?pname ?tit ?sub ?rights ?cr ?lt ?lr ?la ?blog
		// ?nextV
		String reference = "", time = "", pname = "", tit = "", sub = "", lt = "", la = "", blog = "";
		ArrayList<String> features = new ArrayList<String>();

		reference = qs.getResource("reference").getURI();
		if (!reference.contains("/Post") && !reference.contains("/Image")
				&& !reference.contains("/Video")
				&& !reference.contains("/Guide")) {
			return null;// no se representa
		}
		i.setLink(reference);// link al rdf
		if (qs.contains("tit")) {
			tit = qs.getLiteral("tit").getLexicalForm();
			// features.add("Título: "+tit);
			i.setTitle(tit);
		}
		if (qs.contains("time")) {
			time = qs.getLiteral("time").getLexicalForm();
			i.setStart(time);
		} else {
			i.setStart(refTime);
		}
		if (reference.contains("/Post/")) {
			// POST: pname, blog link, RDF
			blog = qs.getResource("blog").getURI();
			features.add("Tipo: Post");
			if (qs.contains("blog")) {
				features.add("&lt;a href=\"" + blog
						+ "\"&gt;RDF del Blog&lt;/a&gt;");
			}
		} else if (reference.contains("/Image/")
				|| reference.contains("/Video/")) {
			// IMAGE-VIDEO: title, description, rights, creator, RDF
			// cont.addEvent(creaEvento(title, null));
			features.add("Tipo: Imagen");
			if (qs.contains("lt")) {
				lt = qs.getLiteral("lt").getLexicalForm();
				features.add("Título: " + lt);
			}
			if (qs.contains("la")) {
				la = qs.getLiteral("la").getLexicalForm();
				features.add("Descripción: " + la);
			}
			if (pname.contains(".png") || pname.contains(".jpg")
					|| pname.contains(".gif")) {
				i.setImage(pname);
			}
		} else if (reference.contains("/Guide/")) {
			// GUIDE: description, rights, creator, RDF
			features.add("Tipo: Guía");
			if (qs.contains("sub")) {
				sub = qs.getLiteral("sub").getLexicalForm();
				features.add("Descripción: " + sub);
			}
			i.setImage("http://www.publicatufoto.com/data/media/2/oso-polar-blanco.jpg");
		}
		if (qs.contains("pname")) {
			pname = qs.getLiteral("pname").getLexicalForm();
			features.add("&lt;a href=\"" + pname + "\"&gt;Enlace&lt;/a&gt;");
		}
		// i.setDescription("HOLA");
		i.setDescription("Características:", features);
		System.out.println(i.toXml());
		return i;
	}

	private String createGetTripMetadata(Integer limit, String uri) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?created ?title ?pL ?pH ?dL ?dH ?tD ?prL ?prH WHERE{ ");
		query.append("<" + uri
				+ "> <http://rdfs.org/sioc/ns#created_at> ?created.");
		// title
		query.append("OPTIONAL {<" + uri
				+ "> <http://purl.org/dc/terms/title> ?title}.");
		// price
		query.append("OPTIONAL{<"
				+ uri
				+ "> <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPrice> ?p.");
		query.append("OPTIONAL{?p <http://webenemasuno.linkeddata.es/ontology/OPMO/lessEurosThan> ?pL}.");
		query.append("OPTIONAL{?p <http://webenemasuno.linkeddata.es/ontology/OPMO/moreEurosThan> ?pH }}.");
		// duration
		query.append("OPTIONAL{<"
				+ uri
				+ "> <http://webenemasuno.linkeddata.es/ontology/OPMO/hasDuration> ?d.");
		query.append("OPTIONAL{?d <http://webenemasuno.linkeddata.es/ontology/OPMO/lessWeeksThan> ?dL}.");
		query.append("OPTIONAL{?d <http://webenemasuno.linkeddata.es/ontology/OPMO/moreWeeksThan> ?dH }}.");
		// description (if any)
		query.append("OPTIONAL{<"
				+ uri
				+ "> <http://webenemasuno.linkeddata.es/ontology/OPMO/tripDescription> ?tD}.");
		// distance
		query.append("OPTIONAL{<"
				+ uri
				+ "> <http://webenemasuno.linkeddata.es/ontology/OPMO/hasDistance> ?pr.");
		query.append("OPTIONAL{?pr <http://webenemasuno.linkeddata.es/ontology/OPMO/lessKmThan> ?prL}.");
		query.append("OPTIONAL{?pr <http://webenemasuno.linkeddata.es/ontology/OPMO/moreKmThan> ?prH }}.");

		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		System.out.println(query.toString());
		return query.toString();

	}

	private String createGetTripProvenance(Integer limit, String uri) {

		StringBuilder query = new StringBuilder(
				"SELECT distinct ?reference ?time ?pname ?tit ?sub ?lt ?la ?blog ?nextV WHERE{ ");
		query.append("?gen <http://openprovenance.org/model/opmo#effect> <"
				+ uri + ">.");
		query.append("?gen <http://openprovenance.org/model/opmo#cause> ?process.");
		query.append("?used <http://openprovenance.org/model/opmo#effect> ?process.");
		query.append("?used <http://openprovenance.org/model/opmo#cause> ?reference.");
		// date para el timeline
		query.append("OPTIONAL{?used <http://openprovenance.org/model/opmo#time> ?t.");
		query.append("?t <http://openprovenance.org/model/opmo#exactlyAt> ?time.}");
		// pname es comun
		query.append("OPTIONAL{?reference <http://openprovenance.org/model/opmo#pname> ?pname}");
		// GUIDE: title, description, rights, creator, RDF
		query.append("OPTIONAL{?reference <http://rdfs.org/sioc/ns#title> ?tit}");
		query.append("OPTIONAL{?reference <http://webenemasuno.linkeddata.es/ontology/OPMO/subtitle> ?sub}");
		// query.append("OPTIONAL{?reference <http://purl.org/dc/terms/rightsHolder> ?rights}");
		// query.append("OPTIONAL{?reference <http://rdfs.org/sioc/ns#has_creator> ?cr}");
		// IMAGE-VIDEO: title, description, rights, creator, RDF
		// query.append("OPTIONAL{?reference <http://webenemasuno.linkeddata.es/ontology/MPEG7/rightsHolder> ?rights."
		// + "?rights <http://www.w3.org/2000/01/rdf-schema#label> ?lr}.");
		query.append("OPTIONAL{?reference <http://metadata.net/mpeg7/mpeg7.owl#title> ?tI."
				+ "?tI <http://www.w3.org/2000/01/rdf-schema#label> ?lt}.");
		query.append("OPTIONAL{?reference <http://metadata.net/mpeg7/mpeg7.owl#abstract> ?a."
				+ "?a <http://www.w3.org/2000/01/rdf-schema#label> ?la}.");
		// POST: blog link
		query.append("OPTIONAL{?reference <http://rdfs.org/sioc/ns#has_container> ?blog}.");// title
																							// ya
																							// viene
																							// de
																							// guide

		// next Version
		query.append("OPTIONAL{?b <http://openprovenance.org/model/opmo#cause> <"
				+ uri + ">.");
		query.append("?b a <http://webenemasuno.linkeddata.es/ontology/OPMO/LaterVersionThan>.");
		query.append("?b <http://openprovenance.org/model/opmo#effect> ?nextV }");

		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		System.out.println(query.toString());
		return query.toString();
	}

}
