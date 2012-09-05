/**
 * Copyright (c) 2012 Jonathan González Sánchez Permission is hereby granted,
 * free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package es.upm.fi.dia.oeg.map4rdf.server.util;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.Geo;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.GeoLinkedDataEsOwlVocabulary;
import es.upm.fi.dia.oeg.map4rdf.share.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Processes RDF models and returns GeoResources to be displayed in a map.
 *
 * @author Jonathan Gonzalez (jonathan@jonbaraq.eu)
 */
public class ShapeFileProcessor {

    private static final Logger LOG = Logger.getLogger(ShapeFileProcessor.class);

    public static List<GeoResource> getGeoResourcesFromModel(Model model) throws FileNotFoundException {
        String queryString = createGetResourcesQuery(true);
        Query query = QueryFactory.create(queryString);
        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        if (!results.hasNext()) {
            query = QueryFactory.create(createGetResourcesQuery(false));
            qe = QueryExecutionFactory.create(query, model);
            results = qe.execSelect();
        }

        // TODO(jonbaraq): use location to restrict the query to the specifies geographic
        // area.
        HashMap<String, GeoResource> result = new HashMap<String, GeoResource>();
        while (results.hasNext() && result.size() < 10000) {
            QuerySolution solution = results.next();
            try {
                String geoUri = solution.getResource("geo").getURI();
                // By default uri will be the same as geoUri.
                String uri = geoUri;
                if (solution.getResource("r") != null) { 
                    uri = solution.getResource("r").getURI();
                }
                String geoTypeUri = solution.getResource("geoType").getURI();
                GeoResource resource = result.get(uri);
                // Just resources with new URIs are inserted into the map.
                if (resource == null) {
                    try {
                        resource = new GeoResource(uri,
                                getGeometry(geoUri, geoTypeUri, solution, model));
                    } catch (DaoException ex) {
                        LOG.warn("DAO exception " + ex.getMessage());
                    }
                    result.put(uri, resource);
                } else if (!resource.hasGeometry(geoUri)) {
                    try {
                        resource.addGeometry(getGeometry(
                                geoUri, geoTypeUri, solution, model));
                    } catch (DaoException ex) {
                        LOG.warn("DAO exception " + ex.getMessage());
                    }
                    result.put(uri, resource);
                }
                if (solution.contains("label")) {
                    Literal labelLiteral = solution.getLiteral("label");
                    resource.addLabel(labelLiteral.getLanguage(),
                            labelLiteral.getString());
                    result.put(uri, resource);
                }
            } catch (Exception e) {
                LOG.warn("Exception " + e.getMessage());
            }
        }

        // Important - free up resources used running the query
        qe.close();

        List<GeoResource> geoResources = new ArrayList<GeoResource>(
                result.values());
        LOG.info("Number of GEORESOURCES parsed: " + geoResources.size());
        return new ArrayList<GeoResource>(result.values());
    }

    private static String createGetResourcesQuery(boolean withUri) {
        StringBuilder query = new StringBuilder(
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT distinct");
        if (withUri) {
            query.append("?r");
        }
        query.append(" ?label ?geo ?geoType ?lat ?lng ");
        query.append("WHERE { ");
        if (withUri) {
          query.append("?r <").append(Geo.geometry).append(">  ?geo. ");
        }
        query.append("?geo <").append(RDF.type).append("> ?geoType . ");
        query.append("?geo" + "<").append(Geo.lat).append(
                ">" + " ?lat;" + "<").append(
                Geo.lng).append(">" + " ?lng" + ".");
        query.append("OPTIONAL { ?r <").append(
                RDFS.label).append("> ?label } .");
        query.append("}");

        return query.toString();
    }

    public static Geometry getGeometry(
            String geoUri, String geoType, QuerySolution solution, Model model)
            throws DaoException {
        if (geoType.equals(Geo.Point.getURI())) {
            return getPoint(geoUri, solution);
        }
        if (geoType.equals(GeoLinkedDataEsOwlVocabulary.Curva.getURI())) {
            return getPolyline(geoUri, model);
        }
        if (geoType.equals(GeoLinkedDataEsOwlVocabulary.Poligono.getURI())) {
            return getPolygon(geoUri, model);
        }
        return null;
    }

    public static PolyLine getPolyline(String uri, Model model) throws DaoException {
        List<Point> points = getGeometryPoints(uri, model);
        return points.isEmpty() ? null : new PolyLineBean(uri, points);
    }

    public static Polygon getPolygon(String uri, Model model) throws DaoException {
        List<Point> points = getGeometryPoints(uri, model);
        return points.isEmpty() ? null : new PolygonBean(uri, points);
    }

    public static List<Point> getGeometryPoints(String uri, Model model)
            throws DaoException {
        List<Point> points = new ArrayList<Point>();
        
        QueryExecution execution = QueryExecutionFactory.create(
                createGetGeometryPointsQuery(uri), model);
        try {
            ResultSet queryResult = execution.execSelect();
            while (queryResult.hasNext()) {
                QuerySolution solution = queryResult.next();
                try {
                    String pointUri = solution.getResource("p").getURI();
                    points.add(getPoint(pointUri, model));
                } catch (NumberFormatException e) {
                    LOG.warn("Invalid Latitud or Longitud value: "
                            + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new DaoException("Unable to execute SPARQL query", e);
        } finally {
            execution.close();
        }
        return points;
    }

    public static Point getPoint(String uri, Model model) throws DaoException {
        QueryExecution execution = QueryExecutionFactory.create(
                createGetGeometryPointsQuery(uri), model);
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

    public static Point getPoint(String uri, QuerySolution solution)
            throws DaoException {
        try {
            double lat = solution.getLiteral("lat").getDouble() % 90;
            double lng = solution.getLiteral("lng").getDouble() % 90;
            return new PointBean(uri, lng, lat);
        } catch (NumberFormatException e) {
        }
        return null;
    }

    private static String createGetPointQuery(String uri) {
        StringBuilder query = new StringBuilder("SELECT ?lat ?lng WHERE { ");
        query.append("<").append(uri).append("> <").append(
                Geo.lat).append(">  ?lat ; ");
        query.append("<").append(Geo.lng).append("> ?lng . ");
        query.append("}");
        return query.toString();
    }

    private static String createGetGeometryPointsQuery(String uri) {
        StringBuilder query = new StringBuilder("SELECT ?p WHERE { ");
        query.append("<").append(uri).append("> <").append(
                GeoLinkedDataEsOwlVocabulary.formadoPor).append(">  ?p . ");
        query.append("?p <").append(RDF.type).append(
                ">  <").append(Geo.Point).append("> . ");
        query.append("?p <").append(
                GeoLinkedDataEsOwlVocabulary.orden).append(">  ?o . ");
        query.append("} ORDER BY ASC(?o)");
        return query.toString();
    }
    
}