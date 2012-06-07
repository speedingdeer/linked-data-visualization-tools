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
package es.upm.fi.dia.oeg.map4rdf.share;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import org.apache.log4j.Logger;

import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.Geo;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.GeoLinkedDataEsOwlVocabulary;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Processes RDF models and returns GeoResources to be displayed in a map.
 *
 * @author Jonathan Gonzalez (jonathan@jonbaraq.eu)
 */
public class RDFModelProcessor {

    private static final String ENDPOINT_URL = "endpoint.url";

    public static List<GeoResource> parseRdfFile(String filePath) {
        System.out.println("Parsing RDF file: " + filePath);
        // Create an empty model
        Model model = ModelFactory.createDefaultModel();

        // Use the FileManager to find the input file
        InputStream in = FileManager.get().open(filePath);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + filePath + " not found");
        }

        // Read the RDF/XML file
        model.read(in, null);
        
        System.out.println("Model to String: " + model.toString());

        return processRdfModel(model);
    }
    
    public static List<GeoResource> processRdfModel(Model model) {
        String queryString = createGetResourcesQuery();
        Query query = QueryFactory.create(queryString);

        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();
        System.out.println("Results:" + results.hasNext());

        // TODO(jonbaraq): use location to restrict the query to the specifies geographic
        // area.
        HashMap<String, GeoResource> result = new HashMap<String, GeoResource>();

        while (results.hasNext()) {
            System.out.println("Inside the while");
            QuerySolution solution = results.next();
            try {
                String uri = solution.getResource("r").getURI();
                String geoUri = solution.getResource("geo").getURI();
                String geoTypeUri = solution.getResource("geoType").getURI();
                System.out.println("Processing queryResult with \n"
                        + "  uri: " + uri
                        + "\n  geoUri:" + geoUri
                        + "\n geoTypeUri:" + geoTypeUri);
                GeoResource resource = result.get(uri);
                // Just resources with new URIs are inserted into the map.
                if (resource == null) {
                    try {
                        resource = new GeoResource(uri,
                                getGeometry(geoUri, geoTypeUri, solution));
                    } catch (DaoException ex) {

                    }
                    result.put(uri, resource);
                } else if (!resource.hasGeometry(geoUri)) {
                    try {
                        resource.addGeometry(getGeometry(
                                geoUri, geoTypeUri, solution));
                    } catch (DaoException ex) {
                    }
                }
                if (solution.contains("label")) {
                    Literal labelLiteral = solution.getLiteral("label");
                    resource.addLabel(labelLiteral.getLanguage(),
                            labelLiteral.getString());
                }
            } catch (Exception e) {
            }
        }
        
        // Important - free up resources used running the query
        qe.close();
        
        List<GeoResource> geoResources = new ArrayList<GeoResource>(result.values());
        System.out.println("Number of GEORESOURCES: " + geoResources.size());
        return new ArrayList<GeoResource>(result.values());
    }

    private static String createGetResourcesQuery() {
        StringBuilder query = new StringBuilder(
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> SELECT distinct ?r ?label "
                + "?geo ?geoType ?lat ?lng ");
        query.append("WHERE { ");
        query.append("?r <").append(Geo.geometry).append(">  ?geo. ");
        query.append("?geo <").append(RDF.type).append("> ?geoType . ");
        query.append("?geo" + "<").append(Geo.lat).append(
                ">" + " ?lat;" + "<").append(
                Geo.lng).append(">" + " ?lng" + ".");
        query.append("OPTIONAL { ?r <").append(
                RDFS.label).append("> ?label } .");
        query.append("}");

        return query.toString();
    }

    // TODO(jonbaraq): Refactor all these methods from GeoLinkedDataDaoImpl into a new Library.
    public static Geometry getGeometry(
            String geoUri, String geoType, QuerySolution solution)
            throws DaoException {
        if (geoType.equals(Geo.Point.getURI())) {
            return getPoint(geoUri, solution);
        }
        if (geoType.equals(GeoLinkedDataEsOwlVocabulary.Curva.getURI())) {
            return getPolyline(geoUri);
        }
        if (geoType.equals(GeoLinkedDataEsOwlVocabulary.Poligono.getURI())) {
            return getPolygon(geoUri);
        }
        return null;
    }

    public static PolyLine getPolyline(String uri) throws DaoException {
        List<Point> points = getGeometryPoints(uri);
        return points.isEmpty() ? null : new PolyLineBean(uri, points);
    }

    public static Polygon getPolygon(String uri) throws DaoException {
        List<Point> points = getGeometryPoints(uri);
        return points.isEmpty() ? null : new PolygonBean(uri, points);
    }

    public static List<Point> getGeometryPoints(
            String uri) throws DaoException {
        List<Point> points = new ArrayList<Point>();
        return points;
    }

    public static Point getPoint(
            String uri, QuerySolution solution) throws DaoException {
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