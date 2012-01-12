package es.upm.fi.dia.oeg.map4rdf.server.db;


import java.util.HashMap;
import java.util.Map;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
/**
 *
 * @author filip
 */
public class DbConfig {
    public static final String DB_NAME = "map4rdf.db";
    public static final String ADMIN_PASSWORD = "password";
    public static final Map<String, String> DB_SEED = new HashMap<String, String>(){
        {
            put(ParameterNames.ENDPOINT_URL, "value_end_point");
            put(ParameterNames.GEOMETRY_MODEL, "value_geometry");
            put(ParameterNames.GOOGLE_MAPS_API_KEY, "value_ui");
            put(ParameterNames.FACETS_AUTO, "value_facet");
        }
    };
    
}
