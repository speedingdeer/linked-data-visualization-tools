package es.upm.fi.dia.oeg.map4rdf.client.view.v2;

import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.event.VectorBeforeFeatureAddedListener;
import org.gwtopenmaps.openlayers.client.event.VectorBeforeFeatureAddedListener.BeforeFeatureAddedEvent;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.handler.RegularPolygonHandler;
import org.gwtopenmaps.openlayers.client.layer.Vector;

public class FilterAreaLayer {

	// drawing
	private Vector filterVector;
	private RegularPolygonHandler regularPolygonHandler;
	private DrawFeature df;
	private Map map;
	
	public FilterAreaLayer(Map map) {
		this.map = map;
		addAreaFilterDrawingTools();
	}
	
	public void setAreaFilterDrawing(Boolean value) {
		if (value) {
			df.activate();
		} else {
			df.deactivate();
		}
	}
	
	public void clearAreaFilterDrawing() {
		filterVector.destroyFeatures();
	}

	private void addAreaFilterDrawingTools(){
		// Drawing part
		regularPolygonHandler = new RegularPolygonHandler();
		filterVector = new Vector("filterVector");
		filterVector.setDisplayInLayerSwitcher(false);
		map.addLayer(filterVector);
		filterVector
				.addVectorBeforeFeatureAddedListener(new VectorBeforeFeatureAddedListener() {
					@Override
					public void onBeforeFeatureAdded(
							BeforeFeatureAddedEvent eventObject) {
						filterVector.destroyFeatures();
					}
				});

		DrawFeatureOptions drawFeatureOptions = new DrawFeatureOptions();
		df = new DrawFeature(filterVector, regularPolygonHandler,
				drawFeatureOptions);
		map.addControl(df);
	}
	public Vector getFilterVector() {
		return this.filterVector;
	}
	
}

