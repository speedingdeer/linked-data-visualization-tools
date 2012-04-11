package es.upm.fi.dia.oeg.map4rdf.share;

import java.util.ArrayList;

/**
 * 
 * @author Daniel Garijo
 * 
 */
public class AemetResource extends GeoResource {
	private ArrayList<AemetObs> obs; // prueba, quizas sea mejor en
										// hashmap

	AemetResource() {
		// for serialization
	}

	public AemetResource(String uri) {
		super(uri);
		obs = new ArrayList<AemetObs>();
	}

	public AemetResource(String uri, Geometry geometry) {
		super(uri, geometry);
		obs = new ArrayList<AemetObs>();
	}

	public ArrayList<AemetObs> getObs() {
		return obs;
	}

	public void addObs(AemetObs observ) {
		obs.add(observ);
	}

}
