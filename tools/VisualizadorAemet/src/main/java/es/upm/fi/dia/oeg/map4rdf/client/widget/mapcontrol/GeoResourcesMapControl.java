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

import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.PolygonClickHandler;
import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.event.PolylineMouseOutHandler;
import com.google.gwt.maps.client.event.PolylineMouseOverHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.GoogleMapsAdapters;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;

/**
 * @author Alexander De Leon. Extended by Daniel Garijo for the Web n+1 Project
 *         viewer
 */
public class GeoResourcesMapControl extends LayerControl {

	private LatLng latLong;
	private final BrowserResources bResources;
	private final DispatchAsync dispatchAsync;

	@Inject
	public GeoResourcesMapControl(BrowserResources resources, DispatchAsync dispatchAsync) {
		bResources = resources;
		this.dispatchAsync = dispatchAsync;
	}

	public void drawGeoResouces(List<GeoResource> resources, final BrowserMessages geoMessages) {
		final GeoResourcesMapControl refThis = this;
		for (final GeoResource resource : resources) {
			for (Geometry geometry : resource.getGeometries()) {
				drawGeometry(geometry, new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						String uri = resource.getUri();
						// al construirlo ya asocia el ejemplo
						new DrawAemetResourcesOnClick(uri, getDisplay(), refThis, latLong, dispatchAsync);
						// new DrawWebNMasUnoResourcesOnClick(bResources,uri,
						// getDisplay(), refThis, latLong);
					}
				});
			}
		}
		// PointBean p = new PointBean("http://uri1", -3.72952,40.585082);
		// PointBean p1 = new PointBean("http://uri2", -4.0, 40.704423 );
		// PointBean p2 = new PointBean("http://uri3", -4.028034, 40.679206 );
		// PointBean p3 = new PointBean("http://uri4", -4.112631, 40.928359 );
		// ArrayList points = new ArrayList();
		// points.add(p);points.add(p1);points.add(p2);points.add(p3);
		// PolyLineBean lin = new
		// PolyLineBean("http://webenemasuno.linkeddata.es/resource/Itinerary/ITINERARY12341",
		// points);
		// drawGeometry(lin, new ClickHandler() {
		// @Override
		// public void onClick(ClickEvent event) {
		// }
		// });
	}

	public void drawGeometry(Geometry geometry, final ClickHandler clickHandler) {
		if (geometry == null) {
			return;
		}
		switch (geometry.getType()) {
		case POINT:
			final Marker marker = new Marker(GoogleMapsAdapters.getLatLng((Point) geometry));
			marker.addMarkerClickHandler(new MarkerClickHandler() {
				@Override
				public void onClick(MarkerClickEvent event) {
					latLong = marker.getLatLng();
					clickHandler.onClick(null); // TODO: null???
				}
			});
			addOverlay(marker);
			break;
		case POLYLINE:// 84002E rojo FF5B00 naranja
			// Polyline line = new
			// Polyline(GoogleMapsAdapters.getLatLngs(((PolyLine)
			// geometry).getPoints()), "#0000ff", 2);
			// colores:
			// Verde-viajero:#088A08
			// azul marino oscuro: #08088A
			// rojo oscuro: #B40404
			// morado: #8A0886
			Polyline line = new Polyline(GoogleMapsAdapters.getLatLngs(((PolyLine) geometry).getPoints()), "#088A08",
					4, 0.9);
			line.addPolylineClickHandler(new PolylineClickHandler() {
				@Override
				public void onClick(PolylineClickEvent event) {
					latLong = event.getLatLng();
					// solo para webNmasuno resources!
					try {
						// enviamos la polyline para poder cambiar el color.
						((WebNMasUnoClickHandler) clickHandler).onClick(null, event.getSender());
					} catch (Exception e) {
						// class cast exception: no hacemos nada.
						// significa que no es un webnmasuno resource.
					}
					clickHandler.onClick(null); // TODO: null???
				}
			});
			// Anado estos dos eventos vacios para que cambie el raton
			// automaticamente. Y si en el futuro se quiere hacer algo
			line.addPolylineMouseOverHandler(new PolylineMouseOverHandler() {
				@Override
				public void onMouseOver(PolylineMouseOverEvent event) {

				}
			});
			line.addPolylineMouseOutHandler(new PolylineMouseOutHandler() {
				@Override
				public void onMouseOut(PolylineMouseOutEvent event) {

				}
			});
			getMap().setCenter(line.getBounds().getCenter(), getMap().getBoundsZoomLevel(line.getBounds()));// line.getBounds().toSpan()
			addOverlay(line);
			break;
		case POLYGON:
			Polygon polygon = new Polygon(
					GoogleMapsAdapters.getLatLngs(((es.upm.fi.dia.oeg.map4rdf.share.Polygon) geometry).getPoints()));
			polygon.addPolygonClickHandler(new PolygonClickHandler() {
				@Override
				public void onClick(PolygonClickEvent event) {
					latLong = event.getLatLng();
					clickHandler.onClick(null); // TODO: null???
				}
			});
			addOverlay(polygon);
			break;
		}

	}

	public LatLng getLatLng() {
		return latLong;
	}
}
