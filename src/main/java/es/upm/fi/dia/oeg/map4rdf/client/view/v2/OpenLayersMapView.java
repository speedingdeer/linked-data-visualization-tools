/**
 * Copyright (c) 2011 Alexander De Leon Battista
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
package es.upm.fi.dia.oeg.map4rdf.client.view.v2;

import name.alexdeleon.lib.gwtblocks.client.widget.loading.LoadingWidget;
import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.event.VectorBeforeFeatureAddedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.layer.Google;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.geometry.Geometry;
import org.gwtopenmaps.openlayers.client.geometry.Polygon;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigurationParameter;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.OpenLayersAdapter;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;

import org.gwtopenmaps.openlayers.client.control.DrawFeature;
import org.gwtopenmaps.openlayers.client.control.DrawFeatureOptions;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.handler.RegularPolygonHandler;

/**
 * @author Alexander De Leon
 */
// TODO : Remove hard coded values!!!
public class OpenLayersMapView implements MapView {

	/**
	 * By default the map is centered in Puerta del Sol, Madrid
	 */
	private static LonLat DEFAULT_CENTER = new LonLat(-3.703637, 40.416645);
	private static final int DEFAULT_ZOOM_LEVEL = 6;

	private final LoadingWidget loadingWidget;
	private final BrowserResources browserResources;
	private Map map;
	private MapWidget mapWidget;
	private final OpenLayersMapLayer defaultLayer;
	private AbsolutePanel panel;
	private LayerSwitcher layerSwitcher;

	// drawing
	private Vector filterVector;
	private RegularPolygonHandler regularPolygonHandler;
	private VectorFeature feature;
	private DrawFeature df;
	
	public OpenLayersMapView(WidgetFactory widgetFactory, DispatchAsync dispatchAsync, BrowserResources browserResources) {
		this.browserResources=browserResources;
		loadingWidget = widgetFactory.getLoadingWidget();
		createUi();
		defaultLayer = (OpenLayersMapLayer) createLayer("default");
		addNotice();
		addDrawingTools();
		
		GetConfigurationParameter action = new GetConfigurationParameter("spherical_mercator");
		dispatchAsync.execute(action, new AsyncCallback<SingletonResult<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("check Spherical Mercator parameter");
			}

			@Override
			public void onSuccess(SingletonResult<String> result) {
				createAsyncUi(result.getValue());
			}
		
		});
		
		
	}

	private void addNotice() {
	}

	@Override
	public Widget asWidget() {
		return panel;
	}

	@Override
	public void startProcessing() {
		loadingWidget.center();
	}

	// presenter
	public Vector getFilterVector() {
		return this.filterVector;
	}

	public void setDrawing(Boolean value) {
		if (value) {
			df.activate();
		} else {
			df.deactivate();
		}
	}

	public void clearDrawing() {
		filterVector.destroyFeatures();
	}

	@Override
	public void stopProcessing() {
		loadingWidget.hide();
	}

	@Override
	public TwoDimentionalCoordinate getCurrentCenter() {
		return OpenLayersAdapter.getTwoDimentionalCoordinate(map.getCenter());
	}

	@Override
	public BoundingBox getVisibleBox() {
		if (filterVector != null) {
			if (filterVector.getNumberOfFeatures() > 0) {

				feature = filterVector.getFeatures()[0];
				Geometry g = feature.getGeometry();
				if (g.getClassName().equals(Geometry.POLYGON_CLASS_NAME)) {
					Polygon p = Polygon.narrowToPolygon(g.getJSObject());
					BoundingBox b = OpenLayersAdapter.getBoudingBox(p);
					b.transform(map.getProjection(), "EPSG:4326");
					return b;
				}
			}
		}
		return null;
		// why??
		// return OpenLayersAdapter.getBoundingBox(map.getExtent());
	}

	@Override
	public void setVisibleBox(BoundingBox boundingBox) {
		map.zoomToExtent(OpenLayersAdapter.getLatLngBounds(boundingBox));
	}

	@Override
	public MapLayer getDefaultLayer() {
		return defaultLayer;
	}

	@Override
	public MapLayer createLayer(String name) {
		// TODO save layer
		return new OpenLayersMapLayer(this, map, name,browserResources);
	}

	@Override
	public AbsolutePanel getContainer() {
		return panel;
	}

	/* ----------------------------- helper methods -- */
	
	private void createUi() {
		panel = new AbsolutePanel() {

			@Override
			protected void onLoad() {
				defaultLayer.bind();
			};
		};
		mapWidget = new MapWidget("100%", "100%", new MapOptions());
		map = mapWidget.getMap();
		layerSwitcher = new LayerSwitcher();		
		map.addControl(layerSwitcher);
		panel.add(mapWidget);
		DOM.setStyleAttribute(panel.getElement(), "zIndex", "0");
	}
	
	private void createAsyncUi(String spherical_mercator){
		if(spherical_mercator.equals("true")) {
			addSphericalMaps();
		} else {
			addFlatMaps();
		}
		
	}
	
	private void addSphericalMaps(){

		//needed constants
		Bounds bounds = new Bounds(-20037508.34, -20037508.34, 20037508.34,
				20037508.34);
		MapOptions options = new MapOptions();
		options.setMaxExtent(bounds);
		options.setProjection("EPSG:900913");
		options.setUnits("m");
		options.setMaxResolution((float) 156543.0339);
	
		//buliding maps
		map.setOptions(options);
		
		//building layers
		WMS openLayersLayer = LayersMenager.getOpenLayersSphericalLayer();
		Google googleLayer = LayersMenager.getGoogleLayer(bounds);
		OSM openStreetMapsLayer = LayersMenager.getOpenStreetMapsLayer();
		
		map.addLayers(new Layer[] { openStreetMapsLayer, googleLayer, openLayersLayer });
		DEFAULT_CENTER.transform("EPSG:4326", map.getProjection());
		map.setCenter(DEFAULT_CENTER, DEFAULT_ZOOM_LEVEL);
	}
	
	private void addFlatMaps(){
		//needed constants
		double[] resolutions = new double[] { 0.703125, 0.3515625, 0.17578125,
				0.087890625, 0.0439453125, 0.02197265625, 0.010986328125,
				0.0054931640625, 0.00274658203125, 0.001373291015625,
				0.0006866455078125, 0.00034332275390625, 0.000171661376953125,
				8.58306884765625e-005, 4.291534423828125e-005,
				2.1457672119140625e-005, 1.0728836059570313e-005,
				5.3644180297851563e-006, 2.6822090148925781e-006,
				1.3411045074462891e-006 };
		
		MapOptions options = new MapOptions();
		options.setProjection("EPSG:4326");
		options.setResolutions(resolutions);
		options.setUnits("degrees");
		options.setMaxExtent(new Bounds(-180, -90, 180, 90));
		options.setMinExtent(new Bounds(-1, -1, 1, 1));
		options.setNumZoomLevels(5);
		//buliding maps
		map.setOptions(options);
		
		//building layers
		WMS otalexLayer = LayersMenager.newIDEE(resolutions);
		WMS olLayer = LayersMenager.getOpenLayersFlatLayer();
		//WMS olBasicLayer = LayersMenager.getOpenLayersFlatBasicLayer(); 
		
		map.addLayers(new Layer[] {otalexLayer,  olLayer});
		DEFAULT_CENTER.transform("EPSG:4326", map.getProjection());
		map.setCenter(DEFAULT_CENTER, DEFAULT_ZOOM_LEVEL);
	}
	
	private void addDrawingTools(){
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

	@Override
	public void closeWindow() {
	}
	
}	
