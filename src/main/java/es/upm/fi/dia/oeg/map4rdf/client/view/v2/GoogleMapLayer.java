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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapPane;
import com.google.gwt.maps.client.MapPaneType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.PolygonClickHandler;
import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.style.StyleMapShape;
import es.upm.fi.dia.oeg.map4rdf.share.Circle;
import es.upm.fi.dia.oeg.map4rdf.share.GoogleMapsAdapters;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;

/**
 * @author Alexander De Leon
 */
public class GoogleMapLayer implements MapLayer {

	private static final int CIRCLE_NUMBER_OF_POINTS = 20;
	private final List<Overlay> overlays = new ArrayList<Overlay>();
	private final MapWidget map;
	private final MapView mapView;

	public GoogleMapLayer(MapWidget map, MapView mapView) {
		this.map = map;
		this.mapView = mapView;
	}

	@Override
	public MapView getMapView() {
		return mapView;
	}

	@Override
	public HasClickHandlers draw(Point point) {
		Marker marker = new Marker(GoogleMapsAdapters.getLatLng(point));
		addOverlay(marker);
		return new MarkerClickHandlerWrapper(marker);
	}

	@Override
	public HasClickHandlers drawPolygon(StyleMapShape<Polygon> geometry) {
		com.google.gwt.maps.client.overlay.Polygon polygon = new com.google.gwt.maps.client.overlay.Polygon(
				GoogleMapsAdapters.getLatLngs((geometry.getMapShape()).getPoints()), geometry.getStrokeColor(),
				geometry.getStrokeWidth(), 1, geometry.getFillColor(), 1);
		addOverlay(polygon);
		return new PolygonClickHandlerWrapper(polygon);
	}

	@Override
	public HasClickHandlers drawPolyline(StyleMapShape<PolyLine> geometry) {
		Polyline line = new Polyline(GoogleMapsAdapters.getLatLngs((geometry.getMapShape()).getPoints()),
				geometry.getStrokeColor(), geometry.getStrokeWidth());
		addOverlay(line);
		return new PolylineClickHandlerWrapper(line);
	}

	@Override
	public HasClickHandlers drawCircle(StyleMapShape<Circle> circle, String text) {
		drawCircle(circle);
		return draw(text, circle.getMapShape().getCenter());
	}

	public HasClickHandlers draw(String text, TwoDimentionalCoordinate point) {
		TextOverlay textOverlay = new TextOverlay(GoogleMapsAdapters.getLatLng(point), text);
		addOverlay(textOverlay);
		return textOverlay.getTextWidget();
	}

	@Override
	public HasClickHandlers drawCircle(StyleMapShape<Circle> circle) {
		LatLngBounds bounds = LatLngBounds.newInstance();
		LatLng[] circlePoints = new LatLng[CIRCLE_NUMBER_OF_POINTS];

		double EARTH_RADIUS = 6371000;
		double d = circle.getMapShape().getRadius() / EARTH_RADIUS;
		double lat1 = Math.toRadians(circle.getMapShape().getCenter().getY());
		double lng1 = Math.toRadians(circle.getMapShape().getCenter().getX());

		double a = 0;
		double step = 360.0 / CIRCLE_NUMBER_OF_POINTS;
		for (int i = 0; i < CIRCLE_NUMBER_OF_POINTS; i++) {
			double tc = Math.toRadians(a);
			double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d) + Math.cos(lat1) * Math.sin(d) * Math.cos(tc));
			double lng2 = lng1
					+ Math.atan2(Math.sin(tc) * Math.sin(d) * Math.cos(lat1),
							Math.cos(d) - Math.sin(lat1) * Math.sin(lat2));
			LatLng point = LatLng.newInstance(Math.toDegrees(lat2), Math.toDegrees(lng2));
			circlePoints[i] = point;
			bounds.extend(point);
			a += step;
		}
		com.google.gwt.maps.client.overlay.Polygon polygon = new com.google.gwt.maps.client.overlay.Polygon(
				circlePoints, circle.getStrokeColor(), circle.getStrokeWidth(), circle.getStrokeOpacity(),
				circle.getFillColor(), circle.getFillOpacity());

		addOverlay(polygon);
		return new PolygonClickHandlerWrapper(polygon);
	}

	@Override
	public PopupWindow createPopupWindow() {
		return new PopupWindow() {

			private final FlowPanel infoWindowPanel = new FlowPanel();
			private final InfoWindowContent infoWindowContent = new InfoWindowContent(infoWindowPanel);

			@Override
			public boolean remove(Widget w) {
				return infoWindowPanel.remove(w);
			}

			@Override
			public Iterator<Widget> iterator() {
				return infoWindowPanel.iterator();
			}

			@Override
			public void clear() {
				infoWindowPanel.clear();
			}

			@Override
			public void add(Widget w) {
				infoWindowPanel.add(w);
			}

			@Override
			public void open(Point point) {
				map.getInfoWindow().open(GoogleMapsAdapters.getLatLng(point), infoWindowContent);
			}

			@Override
			public void close() {
				map.getInfoWindow().close();
			}
		};
	}

	@Override
	public void clear() {

		for (Overlay o : overlays) {
			map.removeOverlay(o);
		}
		overlays.clear();

		if (map.getInfoWindow().isVisible()) {
			map.getInfoWindow().setVisible(false);
		}

	}

	protected void addOverlay(Overlay overlay) {
		overlays.add(overlay);
		map.addOverlay(overlay);
	}

    @Override
    public HasClickHandlers drawFlat(Point point) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	public class MarkerClickHandlerWrapper implements HasClickHandlers {

		private final Marker marker;

		private MarkerClickHandlerWrapper(Marker marker) {
			this.marker = marker;
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {
			// ignore
		}

		@Override
		public HandlerRegistration addClickHandler(final ClickHandler handler) {
			// TODO: how to access the native event in the google maps api???
			marker.addMarkerClickHandler(new MarkerClickHandler() {

				@Override
				public void onClick(MarkerClickEvent event) {
					// TODO: This should return something
					handler.onClick(null);
				}

			});
			return null;
		}

	}

	public class PolygonClickHandlerWrapper implements HasClickHandlers {

		private final com.google.gwt.maps.client.overlay.Polygon polygon;

		private PolygonClickHandlerWrapper(com.google.gwt.maps.client.overlay.Polygon polygon) {
			this.polygon = polygon;
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {
			// ignore
		}

		@Override
		public HandlerRegistration addClickHandler(final ClickHandler handler) {
			// TODO: how to access the native event in the google maps api???
			polygon.addPolygonClickHandler(new PolygonClickHandler() {

				@Override
				public void onClick(PolygonClickEvent event) {
					// TODO: This should return something
					handler.onClick(null);
				}

			});
			return null;
		}

	}

	public class PolylineClickHandlerWrapper implements HasClickHandlers {

		private final Polyline polyline;

		private PolylineClickHandlerWrapper(Polyline polyline) {
			this.polyline = polyline;
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {
			// ignore
		}

		@Override
		public HandlerRegistration addClickHandler(final ClickHandler handler) {
			// TODO: how to access the native event in the google maps api???
			polyline.addPolylineClickHandler(new PolylineClickHandler() {

				@Override
				public void onClick(PolylineClickEvent event) {
					handler.onClick(null);
				}

			});
			return null;
		}

	}

	private class TextOverlay extends Overlay {

		private final LatLng pos;
		private final String text;
		private MapWidget parentMap;
		private MapPane pane;
		private final SimplePanel textPanel;
		private final Anchor link;

		public TextOverlay(LatLng pos, String text) {
			textPanel = new SimplePanel();
			DOM.setStyleAttribute(textPanel.getElement(), "backgroundColor", "#ffffff");
			link = new Anchor(text);
			textPanel.setWidget(link);
			this.pos = pos;
			this.text = text;
		}

		public HasClickHandlers getTextWidget() {
			return link;
		}

		@Override
		protected Overlay copy() {
			return new TextOverlay(pos, text);
		}

		@Override
		protected void initialize(MapWidget map) {
			/* Save a handle to the parent map widget */
			parentMap = map; // If we need to do redraws we'll need this

			/* Add our textPanel to the main map pane */
			pane = map.getPane(MapPaneType.MARKER_PANE);
			pane.add(textPanel);

			/* Place the textPanel on the pane in the correct spot */
			com.google.gwt.maps.client.geom.Point locationPoint = parentMap.convertLatLngToDivPixel(pos);
			pane.setWidgetPosition(textPanel, locationPoint.getX(), locationPoint.getY());

		}

		@Override
		protected void redraw(boolean force) {
			com.google.gwt.maps.client.geom.Point locationPoint = parentMap.convertLatLngToDivPixel(pos);
			pane.setWidgetPosition(textPanel, locationPoint.getX(), locationPoint.getY());
		}

		@Override
		protected void remove() {
			textPanel.removeFromParent();
		}

	}

}
