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

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gwtopenmaps.openlayers.client.layer.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.view.v2.MapLayer;
import es.upm.fi.dia.oeg.map4rdf.client.widget.GeoResourceSummary;
import es.upm.fi.dia.oeg.map4rdf.client.widget.MapShapeStyleFactory;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoResource;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoResourceContainer;

/**
 * @author Alexander De Leon
 */
public class OpenLayersMapView extends es.upm.fi.dia.oeg.map4rdf.client.view.v2.OpenLayersMapView implements
		MapPresenter.Display {

	private final Image kmlButton;
	private final GeoResourceSummary summary;
	private final MapLayer.PopupWindow window;
	private final DispatchAsync dispatchAsync;
	
	@Inject
	public OpenLayersMapView(WidgetFactory widgetFactory, DispatchAsync dispatchAsync,BrowserResources browserResources) {
		super(widgetFactory, dispatchAsync,browserResources);
		kmlButton = createKMLButton();
		this.dispatchAsync = dispatchAsync;
		summary = widgetFactory.createGeoResourceSummary();
		window = getDefaultLayer().createPopupWindow();
		window.add(summary);
	}

	@Override
	public void drawGeoResouces(List<GeoResource> resources) {
		for (GeoResource resource : resources) {
			drawGeoResource(resource);
		}
	}

	@Override
	public void clear() {
		getDefaultLayer().clear();
		window.close();
	}

	@Override
	public HasClickHandlers getKmlButton() {
		return kmlButton;
	}

	/* --------------- helper methods -- */

	private void drawGeoResource(final GeoResource resource) {
		for (Geometry geometry : resource.getGeometries()) {
			switch (geometry.getType()) {
			case POINT:
				final Point point = (Point) geometry;
				getDefaultLayer().draw(point).addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						//collect data for guias and viajes
						GetGeoResource action = new GetGeoResource(resource.getUri());
						dispatchAsync.execute(action, new AsyncCallback<SingletonResult<GeoResource>>() {
							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
							}
							@Override
				            public void onSuccess(SingletonResult<GeoResource> result) {
								summary.setGeoResource(result.getValue(), point);
								window.open(point);
							}
						});
					}
				});
				break;
			case POLYLINE:
				final PolyLine line = (PolyLine) geometry;
				getDefaultLayer().drawPolyline(MapShapeStyleFactory.createStyle(line)).addClickHandler(
						new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								summary.setGeoResource(resource, line);
								window.open(line.getPoints().get(0));
							}
						});
				break;
			case POLYGON:
				final Polygon polygon = (Polygon) geometry;
				getDefaultLayer().drawPolygon(MapShapeStyleFactory.createStyle(polygon)).addClickHandler(
						new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								summary.setGeoResource(resource, polygon);
								window.open(polygon.getPoints().get(0));

							}
						});
				break;
			}

		}

	}

	private Image createKMLButton() {
		Image button = new Image();
		return button;
	}
	
	@Override
	public void closeWindow() {
		window.close();
	}
}
