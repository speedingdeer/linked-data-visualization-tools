/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informática, Universidad 
 * Politécnica de Madrid, Spain
 * 
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
package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.maps.client.MapPane;
import com.google.gwt.maps.client.MapPaneType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResourceOverlays;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetStatisticYears;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.widget.Timeline;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResourceOverlay;
import es.upm.fi.dia.oeg.map4rdf.share.GoogleMapsAdapters;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.Year;
import java.util.ArrayList;

/**
 * @author Alexander De Leon
 */
@Singleton
public class StatisticsMapControl extends LayerControl implements MapControl {

	private final DispatchAsync dispatchAsync;
	private StatisticDefinition statistic;
	private TimelineControl timelineControl;
	private final BrowserResources resources;

	@Inject
	public StatisticsMapControl(DispatchAsync dispatchAsync, BrowserResources resources) {
		this.dispatchAsync = dispatchAsync;
		this.resources = resources;
	}

	@Override
	public void disable() {
		super.disable();
		getMap().removeControl(timelineControl);
	}

        //extension para webNMasUno. Deberia ir en otro mapControl
        public void showWebNMasUnoTimeLine(ArrayList<Year> annos){
            Timeline timeline = new Timeline(annos, resources.css());
            timeline.addValueChangeHandler(new ValueChangeHandler<Year>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Year> event) {
                            Window.alert("Agno elegido: "+event.getValue().getValue());
                            //timeline.setCurrentValue(event.getValue().getValue(), true);
                    }
            });
            timelineControl = new TimelineControl(timeline);
            getMap().addControl(timelineControl);
            getDisplay().stopProcessing();

            timeline.setCurrentValue(0, true);

        }
	public void setStatistics(StatisticDefinition statistic) {
		this.statistic = statistic;
		GetStatisticYears action = new GetStatisticYears(statistic.getDataset());
		getDisplay().startProcessing();
		dispatchAsync.execute(action, new AsyncCallback<ListResult<Year>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				getDisplay().stopProcessing();
			}

			public void onSuccess(ListResult<Year> result) {
				Timeline timeline = new Timeline(result.asList(), resources.css());
				timeline.addValueChangeHandler(new ValueChangeHandler<Year>() {

					@Override
					public void onValueChange(ValueChangeEvent<Year> event) {
						StatisticsMapControl.this.statistic.getDimensions().clear();
						StatisticsMapControl.this.statistic.getDimensions().add(event.getValue().getUri());
						clear();
						drawStatistics(StatisticsMapControl.this.statistic);
					}
				});
				timelineControl = new TimelineControl(timeline);
				getMap().addControl(timelineControl);

				getDisplay().stopProcessing();

				timeline.setCurrentValue(0, true);
			};
		});
	}

	private void drawStatistics(StatisticDefinition statistic) {
		GetGeoResourceOverlays action = new GetGeoResourceOverlays(null);
		action.setStatisticDefinition(statistic);
		getDisplay().startProcessing();
		dispatchAsync.execute(action, new AsyncCallback<ListResult<GeoResourceOverlay>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				getDisplay().stopProcessing();
			}

			public void onSuccess(ListResult<GeoResourceOverlay> result) {
				double max = 0;
				for (GeoResourceOverlay overlay : result) {
					double value = overlay.getValue();
					if (value > max) {
						max = value;
					}
				}
				for (GeoResourceOverlay overlay : result) {
					double radius = (100000 * overlay.getValue()) / max;
					drawCircleFromRadius(
							GoogleMapsAdapters.getLatLng((PointBean) overlay.getEntity().getFirstGeometry()), radius, 20);

					addOverlay(new StatisticTextOverlay(GoogleMapsAdapters.getLatLng((PointBean) overlay.getEntity()
							.getFirstGeometry()), Double.toString(overlay.getValue()), overlay.getUri()));
				}
				getDisplay().stopProcessing();
			};
		});
	}

	private void drawCircleFromRadius(LatLng center, double radius, int nbOfPoints) {

		LatLngBounds bounds = LatLngBounds.newInstance();
		LatLng[] circlePoints = new LatLng[nbOfPoints];

		double EARTH_RADIUS = 6371000;
		double d = radius / EARTH_RADIUS;
		double lat1 = Math.toRadians(center.getLatitude());
		double lng1 = Math.toRadians(center.getLongitude());

		double a = 0;
		double step = 360.0 / nbOfPoints;
		for (int i = 0; i < nbOfPoints; i++) {
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

		Polygon circle = new Polygon(circlePoints, "white", 0, 0, "green", 0.4);

		addOverlay(circle);
	}

	private class StatisticTextOverlay extends Overlay {

		private final LatLng pos;
		private final String text;
		private final String url;
		private MapWidget parentMap;
		private MapPane pane;
		private final SimplePanel textPanel;

		public StatisticTextOverlay(LatLng pos, String text, String url) {
			textPanel = new SimplePanel();
			DOM.setStyleAttribute(textPanel.getElement(), "backgroundColor", "#ffffff");
			Anchor link = new Anchor(text, false, url, "_blank");
			textPanel.setWidget(link);
			this.pos = pos;
			this.text = text;
			this.url = url;
		}

		@Override
		protected Overlay copy() {
			return new StatisticTextOverlay(pos, text, url);
		}

		@Override
		protected void initialize(MapWidget map) {
			/* Save a handle to the parent map widget */
			parentMap = map; // If we need to do redraws we'll need this

			/* Add our textPanel to the main map pane */
			pane = map.getPane(MapPaneType.MARKER_PANE);
			pane.add(textPanel);

			/* Place the textPanel on the pane in the correct spot */
			Point locationPoint = parentMap.convertLatLngToDivPixel(pos);
			pane.setWidgetPosition(textPanel, locationPoint.getX(), locationPoint.getY());

		}

		@Override
		protected void redraw(boolean force) {
			Point locationPoint = parentMap.convertLatLngToDivPixel(pos);
			pane.setWidgetPosition(textPanel, locationPoint.getX(), locationPoint.getY());
		}

		@Override
		protected void remove() {
			textPanel.removeFromParent();
		}

	}

}
