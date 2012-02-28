package es.upm.fi.dia.oeg.map4rdf.server.db;


import java.util.HashMap;
import java.util.Map;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
/**
 *
 * @author filip
 */

/**
 * possible values 
 * - GeoLinkedData
 * sparqlEndpoint=http://geo.linkeddata.es/sparql
 * geometrymodel=OEG
 *
 * - DBPedia
 * sparqlEndpoint=http://dbpedia.linkeddata.es/sparql
 * geometrymodel=DBPEDIA
 *
 * - vcard
 * sparlEndpoint=http://linkeddata.uriburner.com/sparql
 * geometrymodel=VCARD
 */

public class DbConfig {
    public static final String DB_NAME = "map4rdf.db";
    public static final Map<String, String> DB_SEED = new HashMap<String, String>(){
        {
            put(ParameterNames.ENDPOINT_URL, "http://geo.linkeddata.es/sparql");
            put(ParameterNames.GEOMETRY_MODEL, "OEG");
            put(ParameterNames.GOOGLE_MAPS_API_KEY, "ABQIAAAAzLYqFkZVLHv0seO36vhZahTVXfBc-erLsJZtZLx-fZLjxiIMWBTUgr5s9aY_jQ5Fyqku0qQKuoGE8A");
            put(ParameterNames.FACETS_AUTO, "true");
            put(ParameterNames.ADMIN, "password");
        }
    };
    
}
