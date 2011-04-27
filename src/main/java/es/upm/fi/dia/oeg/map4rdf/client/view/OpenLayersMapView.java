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
package es.upm.fi.dia.oeg.map4rdf.client.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Marker;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.event.MarkerBrowserEventListener;
import org.gwtopenmaps.openlayers.client.layer.Google;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.Markers;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.popup.Popup;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.widget.LoadingWidget;
import es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol.MapControl;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry.Type;
import es.upm.fi.dia.oeg.map4rdf.share.OpenLayersAdapter;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;

/**
 * @author Alexander De Leon
 */
public class OpenLayersMapView extends Composite implements MapPresenter.Display {

	/**
	 * By default the map is centered in Puerta del Sol, Madrid
	 */
	private static final LonLat DEFAULT_CENTER = new LonLat(-3.703637, 40.416645);
	private static final int DEFAULT_ZOOM_LEVEL = 5;
	public static final String WMS_URL = "http://www.idee.es/wms-c/IDEE-Base/IDEE-Base";

	private final LoadingWidget loadingWidget;
	private final Image kmlButton;
	private Map map;
	private Markers markers;
	private Panel panel;
	private final BrowserMessages messages;

	@Inject
	public OpenLayersMapView(LoadingWidget loadingWidget, BrowserResources browserResources, BrowserMessages messages) {
		this.loadingWidget = loadingWidget;
		this.messages = messages;
		kmlButton = createKMLButton(browserResources);
		initWidget(createUi());
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public TwoDimentionalCoordinate getCurrentCenter() {
		return OpenLayersAdapter.getTwoDimentionalCoordinate(map.getCenter());
	}

	@Override
	public BoundingBox getVisibleBox() {
		return OpenLayersAdapter.getBoundingBox(map.getExtent());
	}

	@Override
	public void setVisibleBox(BoundingBox boundingBox) {
		map.zoomToExtent(OpenLayersAdapter.getLatLngBounds(boundingBox));

	}

	@Override
	public void startProcessing() {
		loadingWidget.center();
	}

	@Override
	public void stopProcessing() {
		loadingWidget.hide();
	}

	@Override
	public void drawGeoResouces(List<GeoResource> resources) {
		for (final GeoResource resource : resources) {
			for (Geometry geometry : resource.getGeometries()) {
				switch (geometry.getType()) {
				case POINT:
					final Point point = (Point) geometry;
					Marker marker = new org.gwtopenmaps.openlayers.client.Marker(OpenLayersAdapter.getLatLng(point));
					marker.addBrowserEventListener("mousedown", new MarkerBrowserEventListener() {

						@Override
						public void onBrowserEvent(MarkerBrowserEvent markerBrowserEvent) {
							Popup popup = new Popup(resource.getUri(), OpenLayersAdapter.getLatLng(point),
									new org.gwtopenmaps.openlayers.client.Size(200, 150), getPopupHtml(resource), true);
							map.addPopupExclusive(popup);
						}

					});
					markers.addMarker(marker);
				}
			}
		}
	}

	@Override
	public void clear() {
		// TODO:

	}

	@Override
	public HasClickHandlers getKmlButton() {
		return kmlButton;
	}

	/* --------------- helper methods -- */
	protected void addMapControl(MapControl mapControl) {
		mapControl.setDisplay(this);
		// map.addControl(mapControl.getCustomControl());
	}

	private Widget createUi() {
		panel = new FlowPanel();
		MapOptions options = new MapOptions();
		double[] resolutions = new double[] { 0.703125, 0.3515625, 0.17578125, 0.087890625, 0.0439453125,
				0.02197265625, 0.010986328125, 0.0054931640625, 0.00274658203125, 0.001373291015625,
				0.0006866455078125, 0.00034332275390625, 0.000171661376953125, 8.58306884765625e-005,
				4.291534423828125e-005, 2.1457672119140625e-005, 1.0728836059570313e-005, 5.3644180297851563e-006,
				2.6822090148925781e-006, 1.3411045074462891e-006 };
		options.setProjection("EPSG:4326");
		options.setResolutions(resolutions);

		MapWidget mapWidget = new MapWidget("100%", "100%", options);
		map = mapWidget.getMap();

		// Defining a WMSLayer and adding it to a Map
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("Todas");

		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);

		WMS wmsLayer = new WMS("IDEE", WMS_URL, wmsParams, wmsLayerParams);

		markers = new Markers("Markers");

		map.addLayers(new Layer[] { wmsLayer, new Google("Google Maps"), markers });
		map.addControl(new LayerSwitcher());
		map.setCenter(DEFAULT_CENTER, DEFAULT_ZOOM_LEVEL);

		panel.add(mapWidget);
		return panel;
	}

	private Image createKMLButton(BrowserResources browserResources) {
		Image button = new Image(browserResources.kmlButton());
		return button;
	}

	private String getPopupHtml(GeoResource resource) {
		String labelText = "";
		Set<String> langs = resource.getLangs();
		Set<String> usedLangs = new HashSet<String>();
		String userLang = LocaleUtil.getClientLanguage();

		int count = 0;
		if (langs.contains(userLang)) {
			labelText = "<b>" + resource.getLabel(userLang) + " (" + userLang + ")</b><br>";
			usedLangs.add(userLang);
			count++;
		}

		if (userLang != "es" && langs.contains("es") && !usedLangs.contains("es")) {
			labelText = labelText + "<b>" + resource.getLabel("es") + " (es)</b><br>";
			usedLangs.add("es");
			count++;
		}

		if (userLang != "en" && langs.contains("en") && !usedLangs.contains("en")) {
			labelText = labelText + "<b>" + resource.getLabel("en") + " (en)</b><br>";
			usedLangs.add("en");
			count++;
		}

		if (count < 3 && langs.size() != 0) {
			if (count < 3 && langs.size() != 0) {
				Iterator<String> iterator = langs.iterator();
				while (iterator.hasNext()) {
					String newLang = iterator.next();
					if (!usedLangs.contains(newLang)) {
						if (newLang != "" && newLang.length() > 0) {
							labelText = labelText + "<b>" + resource.getLabel(newLang) + " (" + newLang + ")</b><br>";
						} else {
							labelText = labelText + "<b>" + resource.getLabel(newLang) + "</b><br>";
						}

						usedLangs.add(newLang);
						count++;
						if (count == 3) {
							break;
						}
					}
				}
			}
		}
		/*
		 * String label = resource.getLabel(LocaleUtil.getClientLanguage());
		 * 
		 * if (label==null) label=resource.getLabel("es"); if (label==null)
		 * label=resource.getLabel("ca"); if (label==null)
		 * label=resource.getLabel("gl"); if (label==null)
		 * label=resource.getLabel("eu");
		 */

		String uri = resource.getUri();
		String text = (labelText.length() == 0 ? uri : labelText) + "<br>";
		if (resource.getFirstGeometry().getType() == Type.POINT) {
			Point point = (Point) resource.getFirstGeometry();
			String latPoint = String.valueOf(point.getY());
			String longPoint = String.valueOf(point.getX());

			text = text + "<b>" + messages.latitude() + "</b>" + latPoint + "<br>";
			text = text + "<b>" + messages.longitude() + "</b>" + longPoint + "<br>";
		}
		return text + "<br>" + messages.information() + " <a href='" + resource.getUri() + "' target='_blank'>"
				+ messages.here() + "</a>";
	}
}
