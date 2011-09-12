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
import com.hp.hpl.jena.vocabulary.RDFS;

import es.upm.fi.dia.oeg.map4rdf.server.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.Geo;
import es.upm.fi.dia.oeg.map4rdf.share.AemetObs;
import es.upm.fi.dia.oeg.map4rdf.share.AemetResource;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResourceOverlay;
import es.upm.fi.dia.oeg.map4rdf.share.Intervalo;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLineBean;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoGuide;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoItinerary;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoResourceContainer;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoTrip;
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
	public List<Facet> getFacets(String predicateUri, BoundingBox boundingBox) throws DaoException {
		Map<String, Facet> result = new HashMap<String, Facet>();
		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("select distinct ?class ?label where { ");
		// puntos
		queryBuffer.append("{?x <" + Geo.lat + "> _:lat. ");
		queryBuffer.append("?x <" + Geo.lng + "> _:lng. ");
		queryBuffer.append("?x <" + predicateUri + "> ?class . ");
		queryBuffer.append("OPTIONAL {?class <" + RDFS.label + "> ?label . }}");

		queryBuffer.append("}");
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

	/**
	 * private List<GeoResource> getGeoResources(BoundingBox boundingBox,
	 * Set<FacetConstraint> constraints, Integer max) throws DaoException { //
	 * TODO: use location to restrict the query to the specifies geographic //
	 * area.
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
	 * solution.getLiteral("label");
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
	private List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints, Integer max)
			throws DaoException {
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
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri,
				createGetResourcesQueryAdaptedWebNMasUno(boundingBox, constraints, max));

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
						resource = result.get(uri);
					} catch (Exception e) {
						// No es un webNmasuno resource
						// es un aemet resource?
						try {
							resource = result.get(uri);
						} catch (Exception e2) {
							// sino, es un resource normal
							resource = result.get(uri);
						}
					}
					//
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(), labelLiteral.getString());
					}

					// prueba para AEMET
					if (resource == null) {
						resource = new AemetResource(uri, new PointBean(uri, lng, lat));
						result.put(uri, resource);
					}

					// prueba WebNmasUno (habria que separar)
					// if (resource == null) {
					// resource = new WebNMasUnoResourceContainer(uri, new
					// PointBean(uri, lng, lat));
					// result.put(uri, resource);
					// }

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
		StringBuilder query = new StringBuilder("SELECT distinct ?r ?lat ?lng ?label ");
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
	private String createGetResourcesQueryAdaptedWebNMasUno(BoundingBox boundingBox, Set<FacetConstraint> constraints,
			Integer limit) {
		StringBuilder query = new StringBuilder("SELECT distinct ?r ?lat ?lng ?label ");
		query.append("WHERE { ");
		query.append("?r <" + Geo.lat + "> ?lat. ");
		query.append("?r <" + Geo.lng + "> ?lng . ");
		query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
		if (constraints != null) {
			for (FacetConstraint constraint : constraints) {
				if (constraint.getFacetValueId().contains("Trip")) {
					// tratamiento especial si es un viaje
					query.append("{ ?t <" + constraint.getFacetId() + "> <" + constraint.getFacetValueId() + ">.");
					query.append("?t <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> ?it.");
					query.append("?it <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?pe.");
					query.append("?pe <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPoint> ?r. } UNION");
				} else {
					if (constraint.getFacetValueId().contains("Point")) {
						query.append("{ ?r <" + constraint.getFacetId() + "> <" + constraint.getFacetValueId()
								+ ">. } UNION");
					} else {
						// cualquier cosa con localizacion (guias, aristas)
						query.append("{ ?g <" + constraint.getFacetId() + "> <" + constraint.getFacetValueId() + ">. ");
						query.append("?g <http://www.w3.org/2003/01/geo/wgs84_pos#location> ?r. } UNION");

					}
				}
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
	 * Variacion de la query de recuperacion de recursos original para que
	 * devuelva Titulos de guias y viajes asociados a un recurso
	 */
	private String createGetGuidesTripsQuery(Integer limit, String uri) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?noticia ?title ?url ?dateG ?trip ?tripTitle ?tripURL ?it ?dateV ");
		query.append("WHERE { ");
		query.append("{?noticia <http://www.w3.org/2003/01/geo/wgs84_pos#location> " + "<" + uri + "> . ");
		query.append("?noticia a <http://webenemasuno.linkeddata.es/ontology/OPMO/Guide>.");
		query.append("OPTIONAL {?noticia <http://rdfs.org/sioc/ns#title> ?title . }");
		query.append("OPTIONAL {?noticia <http://rdfs.org/sioc/ns#created_at> ?dateG . }");
		query.append("OPTIONAL {?noticia <http://openprovenance.org/model/opmo#pname> ?url . }}");
		query.append("UNION");
		// query.append("{?t a <http://webenemasuno.linkeddata.es/ontology/OPMO/Trip>.");
		query.append("{?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> ?it. ");
		query.append("?it <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?part. ");
		query.append("?part <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPoint> " + "<" + uri + "> .");
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
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	// quiza convenga separarlo en 2 queries (para no repetir nombre estacion e
	// intervalo)
	private String createGetObs(Integer limit, String uri, String date) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?estacion ?obs ?est ?prop ?dato ?q ?h ?min ?dia ?mes ?anno ");
		query.append("WHERE { ");
		query.append("?estacion <http://www.w3.org/2003/01/geo/wgs84_pos#location> " + "<" + uri + "> . ");
		query.append("?estacion <http://aemet.linkeddata.es/ontology/stationName> ?est . ");
		query.append("?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?estacion . ");
		query.append("?obs <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> ?prop . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/valueOfObservedData> ?dato . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/observedDataQuality> ?q . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/observedInInterval> ?inter . ");
		query.append("?inter <http://www.w3.org/2006/time#hasBeginning> ?instant . ");
		query.append("?instant <http://www.w3.org/2006/time#inDateTime> ?tiempoFecha . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#hour> ?h . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#minute> ?min . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#day> ?dia . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#month> ?mes . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#year> ?anno . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#inXSDDateTime> \"" + date
				+ "\"^^<http://www.w3.org/2001/XMLSchema#dateTime> . }");

		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		System.out.println(query.toString());
		return query.toString();
	}

	private String createGetMaxDate(String uri) {
		StringBuilder query = new StringBuilder("SELECT (MAX(?dt) AS ?date) ");
		query.append("WHERE { ");
		query.append("?estacion <http://www.w3.org/2003/01/geo/wgs84_pos#location> " + "<" + uri + "> . ");
		query.append("?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?estacion . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/observedInInterval> ?inter . ");
		query.append("?inter <http://www.w3.org/2006/time#hasBeginning> ?instant . ");
		query.append("?instant <http://www.w3.org/2006/time#inDateTime> ?tiempoFecha . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#inXSDDateTime> ?dt . }");

		System.out.println(query.toString());
		return query.toString();
	}

	private String createGetObsForProperty(String station, String property, Intervalo start, Intervalo end) {
		StringBuilder query = new StringBuilder(
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n\n SELECT distinct ?obs ?dato ?q ?h ?min ?dia ?mes ?anno ");
		query.append("WHERE { ");
		query.append("?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <" + station + ">. ");
		query.append("?obs <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <" + property + "> . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/valueOfObservedData> ?dato . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/observedDataQuality> ?q . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/observedInInterval> ?inter . ");
		query.append("?inter <http://www.w3.org/2006/time#hasBeginning> ?instant . ");
		query.append("?instant <http://www.w3.org/2006/time#inDateTime> ?tiempoFecha . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#hour> ?h . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#minute> ?min . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#day> ?dia . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#month> ?mes . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#year> ?anno . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#inXSDDateTime> ?dt . ");
		query.append("FILTER(?dt >= xsd:dateTime(\"" + start.asXSDDateTime() + "\")). ");
		query.append("FILTER(?dt <= xsd:dateTime(\"" + end.asXSDDateTime() + "\")). ");
		query.append("}");

		System.out.println(query.toString());
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

	@Override
	public GeoResource getDatosObservacion(String uri) throws DaoException {
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri, createGetMaxDate(uri)); // cogemos
		ResultSet queryResult2 = exec2.execSelect();
		String date = null;
		while (queryResult2.hasNext()) {
			QuerySolution sol = queryResult2.next();
                        if(sol.contains("date")){
                            date = sol.getLiteral("date").getString();
                        }

		}
		if (date == null) {
			return null;
		}
		return getDatosObservacion(uri, date);
	}

	public GeoResource getDatosObservacion(String uri, String date) throws DaoException {
		// throw new UnsupportedOperationException("Not supported yet.");
		// Date d = new Date();
		AemetResource aemetR = null;
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri, createGetObs(100, uri, date)); // cogemos
																												// las
																												// 1000
																												// ultimas
		ResultSet queryResult2 = exec2.execSelect();
		while (queryResult2.hasNext()) {
			/*
			 * String id,String uriObs,String estacion,String valor, String
			 * calidad, String prop, String feature, String intervalo ?obs
			 * ?nombreEst ?prop ?dato ?q ?intervalo
			 */
			QuerySolution solution2 = queryResult2.next();
			String sttionUri = solution2.getResource("estacion").getURI();
			String idObs = solution2.getResource("obs").getURI();
			String nombreEstacion = solution2.getLiteral("est").getLexicalForm();
			String prop = solution2.getResource("prop").getURI();
			String propLabel = solution2.getResource("prop").getLocalName();
			double dato = solution2.getLiteral("dato").getDouble();
			String q = "No disponible";
			if (solution2.contains("q")) {
				q = solution2.getLiteral("q").getLexicalForm();
			}
			int min, h, dia, mes, anno;
			min = solution2.getLiteral("min").getInt();
			h = solution2.getLiteral("h").getInt();
			dia = solution2.getLiteral("dia").getInt();
			mes = solution2.getLiteral("mes").getInt();
			anno = solution2.getLiteral("anno").getInt();
			Intervalo intervalo = new Intervalo(anno, mes, dia, h, min);
			/*
			 * AemetObs observ = new AemetObs(idObs, nombreEstacion, dato, q,
			 * prop, "", intervalo);
			 */
			if (aemetR == null) {
				aemetR = new AemetResource(sttionUri);
				aemetR.addLabel("", nombreEstacion);
			}

			Resource propR = new Resource(prop);
			propR.addLabel("", propLabel);
			AemetObs observ = new AemetObs(idObs, aemetR, dato, q, propR, "", intervalo);
			(aemetR).addObs(observ);
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
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri, createGetGuidesTripsQuery(100, uri));
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
				WebNMasUnoGuide g = new WebNMasUnoGuide(titleGuide, urlGuide, uriGuide, dateGuia);
				((WebNMasUnoResourceContainer) resource).addWebNMasUnoResource(g);
			} else if (!uriTrip.equals("")) {
				WebNMasUnoTrip t = new WebNMasUnoTrip(titTrip, tripURL, uriTrip, idIt, dateViaje);
				((WebNMasUnoResourceContainer) resource).addWebNMasUnoResource(t);
			}

		}
		return resource;
	}

	@Override
	public WebNMasUnoItinerary getItinerary(String uriItinerario) throws DaoException {
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
		QueryExecution exec3 = QueryExecutionFactory
				.sparqlService(endpointUri, createGetTitleTrip(1000, uriItinerario));
		// puntos
		ResultSet queryResult3 = exec3.execSelect();
		while (queryResult3.hasNext()) {
			QuerySolution solution2 = queryResult3.next();
			title = solution2.getLiteral("tit").getLexicalForm();
			it.addViaje(title);
		}
		return it;
	}

	@Override
	public List<AemetObs> getObservations(String stationUri, String propertyUri, Intervalo start, Intervalo end) {
		List<AemetObs> result = new ArrayList<AemetObs>();
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetObsForProperty(stationUri, propertyUri, start, end));
		ResultSet queryResult2 = exec2.execSelect();
		while (queryResult2.hasNext()) {
			/*
			 * String id,String uriObs,String estacion,String valor, String
			 * calidad, String prop, String feature, String intervalo ?obs
			 * ?nombreEst ?prop ?dato ?q ?intervalo
			 */
			QuerySolution solution2 = queryResult2.next();
			String idObs = solution2.getResource("obs").getURI();
			String nombreEstacion = stationUri;
			String prop = propertyUri;
			double dato = solution2.getLiteral("dato").getDouble();
			String q = "No disponible";
			if (solution2.contains("q")) {
				q = solution2.getLiteral("q").getLexicalForm();
			}
			int min, h, dia, mes, anno;
			min = solution2.getLiteral("min").getInt();
			h = solution2.getLiteral("h").getInt();
			dia = solution2.getLiteral("dia").getInt();
			mes = solution2.getLiteral("mes").getInt();
			anno = solution2.getLiteral("anno").getInt();
			Intervalo intervalo = new Intervalo(anno, mes, dia, h, min);
			/*
			 * AemetObs observ = new AemetObs(idObs, nombreEstacion, dato, q,
			 * prop, "", intervalo);
			 */
			Resource estation = new Resource(stationUri);
			estation.addLabel("", nombreEstacion);

			Resource propR = new Resource(propertyUri);

			AemetObs observ = new AemetObs(idObs, estation, dato, q, propR, "", intervalo);
			result.add(observ);
		}

		return result;
	}

	private String createGetItineraryQuery(Integer limit, String uri) {
		StringBuilder query = new StringBuilder("SELECT distinct ?order ?point ?lat ?long ");
		query.append("WHERE{ ");
		query.append("<" + uri + "> <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?path.");
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
		query.append("?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> <" + uri + ">.");
		query.append("?trip <http://purl.org/dc/terms/title> ?tit.");

		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

}
