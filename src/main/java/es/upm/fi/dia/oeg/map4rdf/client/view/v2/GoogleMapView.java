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

import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl3D;
import com.google.gwt.maps.client.control.MenuMapTypeControl;
import com.google.gwt.maps.client.control.ScaleControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.GoogleMapsAdapters;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;

/**
 * @author Alexander De Leon
 */
public class GoogleMapView implements MapView {

	/**
	 * By default the map is centered in Puerta del Sol, Madrid
	 */
	private static final LatLng DEFAULT_CENTER = LatLng.newInstance(40.416645, -3.703637);
	private static final int DEFAULT_ZOOM_LEVEL = 7;

	private AbsolutePanel panel;
	private LayoutPanel container;
	private MapWidget map;
	private final MapLayer defaultLayer;

	private final LoadingWidget loadingWidget;

	@Inject
	public GoogleMapView(WidgetFactory widgetFactory) {
		loadingWidget = widgetFactory.getLoadingWidget();
		createUi();

		defaultLayer = createLayer("default");
	}

	@Override
	public TwoDimentionalCoordinate getCurrentCenter() {
		LatLng googleLatLng = map.getCenter();
		return GoogleMapsAdapters.getTwoDimentionalCoordinate(googleLatLng);
	}

	@Override
	public BoundingBox getVisibleBox() {
		return GoogleMapsAdapters.getBoundingBox(map.getBounds());
	}

	@Override
	public void setVisibleBox(BoundingBox boundingBox) {
		int zoomLevel = map.getBoundsZoomLevel(GoogleMapsAdapters.getLatLngBounds(boundingBox));
		map.setCenter(GoogleMapsAdapters.getLatLng(boundingBox.getCenter()));
		map.setZoomLevel(zoomLevel);
	}

	@Override
	public MapLayer getDefaultLayer() {
		return defaultLayer;
	}

	@Override
	public MapLayer createLayer(String name) {
		// TODO save layer in a map
		return new GoogleMapLayer(map, this);
	}

	@Override
	public AbsolutePanel getContainer() {
		return panel;
	}

	@Override
	public Widget asWidget() {
		return panel;
	}

	@Override
	public void startProcessing() {
		loadingWidget.center();
	}

	@Override
	public void stopProcessing() {
		loadingWidget.hide();
	}

	/* ----------------------- helper methods -- */
	private void createUi() {
		panel = new AbsolutePanel() {
			@Override
			protected void onLoad() {
				super.onLoad();
				/*
				 * This is woraround for issue with the GoogleMaps and the new
				 * GWT Layout panels. More info:
				 * http://code.google.com/p/gwt-google-apis/issues/detail?id=366
				 */
				new Timer() {

					@Override
					public void run() {
						map.checkResizeAndCenter();

					}
				}.schedule(1);

			};
		};
		map = new MapWidget(DEFAULT_CENTER, DEFAULT_ZOOM_LEVEL);
		map.setSize("100%", "100%");

		map.addControl(new LargeMapControl3D());
		map.addControl(new MenuMapTypeControl());
		map.addControl(new ScaleControl());

		map.setScrollWheelZoomEnabled(false);
		map.setCurrentMapType(MapType.getPhysicalMap());
		panel.add(map);
		panel.setWidgetPosition(map, 0, 0);
	}

	@Override
	public void closeWindow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showWidgetPanel() {
		// TODO Auto-generated method stub
		
	}

}
