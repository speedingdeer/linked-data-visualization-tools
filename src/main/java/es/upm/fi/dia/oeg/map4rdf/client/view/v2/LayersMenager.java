package es.upm.fi.dia.oeg.map4rdf.client.view.v2;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.layer.Google;
import org.gwtopenmaps.openlayers.client.layer.GoogleOptions;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.OSMOptions;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

import es.upm.fi.dia.oeg.map4rdf.client.util.GMapType;

public class LayersMenager {

	private static final String WMS_URL = "http://www.idee.es/wms-c/IDEE-Base/IDEE-Base";
	
	public static WMS getIdeaLayer(double[] resolutions){
		WMSParams wmsParams = new WMSParams();
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsParams.setLayers("Todas");
		//wmsLayerParams.setMaxExtent(new Bounds(-50, -50, 50, 50));
		wmsLayerParams.setAttribution("Maps provided by <a href =\"http://www.idee.es\">IDEE</a>");
		wmsLayerParams.setResolutions(resolutions);
		WMS wmsLayer = new WMS("IDEE", WMS_URL, wmsParams, wmsLayerParams);
		return wmsLayer;
	}
	public static OSM getOpenStreetMapsLayer() {
		OSMOptions options = new OSMOptions();
		OSM openStreetMap = OSM.Mapnik("Open Street Maps",options);
		return openStreetMap;
	}
	public static WMS getOpenLayersSphericalLayer(){
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("basic");
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
		WMS wmsLayer = new WMS(
				"Open Layers Maps",
				"http://vmap0.tiles.osgeo.org/wms/vmap0",wmsParams,wmsLayerParams);
		return wmsLayer;
	}
	
	public static WMS getOpenLayersFlatLayer() {
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("Vmap0");
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
		WMS wmsLayer = new WMS(
				"Open Layers Maps (Full)",
				"http://vmap0.tiles.osgeo.org/wms/vmap0",wmsParams,wmsLayerParams);
		return wmsLayer;
	}
	
	public static WMS getOpenLayersFlatBasicLayer() {
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("basic");
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
		WMS wmsLayer = new WMS(
				"Open Layers Maps (Basic)",
				"http://vmap0.tiles.osgeo.org/wms/vmap0",wmsParams,wmsLayerParams);
		return wmsLayer;
	}
	
	public static Google getGoogleLayer(Bounds bounds){
		GoogleOptions googleOptions = new GoogleOptions();
		googleOptions.setSphericalMercator(true);
		googleOptions.setType(GMapType.G_NORMAL_MAP);
		googleOptions.setMaxExtent(bounds);
		googleOptions.setNumZoomLevels(20);
		googleOptions.setMaxExtent(bounds);
		Google google = new Google("Google Maps", googleOptions);
		return google;
	}
	
}
