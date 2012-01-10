package es.upm.fi.dia.oeg.map4rdf.server.db;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author filip
 */
public class DbConfig {
    public static final String DB_NAME = "map4rdf.db";
    public static final String ADMIN_PASSWORD = "password";
    public static final Map<String, String> DB_SEED = new HashMap<String, String>(){
        {
            put("endpoint_url", "value");
            put("geometry_model", "value");
            put("ui_google_maps_api_key", "value");
            put("facet_automatic", "value");
        }
    };
    
}
