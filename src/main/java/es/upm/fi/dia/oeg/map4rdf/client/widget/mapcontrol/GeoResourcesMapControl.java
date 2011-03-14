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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.PolygonClickHandler;
import com.google.gwt.maps.client.event.PolylineClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry.Type;
import es.upm.fi.dia.oeg.map4rdf.share.GoogleMapsAdapters;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;

/**
 * @author Alexander De Leon
 */
public class GeoResourcesMapControl extends LayerControl {

	private LatLng latLong;

	public void drawGeoResouces(List<GeoResource> resources, final BrowserMessages geoMessages) {
		for (final GeoResource resource : resources) {
			for (Geometry geometry : resource.getGeometries()) {
				drawGeometry(geometry, new ClickHandler() {

					/*
					 * @Override public void onClick(ClickEvent event) {
					 * Window.open(resource.getUri(), resource.getUri(), null);
					 * 
					 * }
					 */
					@Override
					public void onClick(ClickEvent event) {
						// Window.open(resource.getUri(), resource.getUri(),
						// null);
						InfoWindowContent iwc;
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
											labelText = labelText + "<b>" + resource.getLabel(newLang) + " (" + newLang
													+ ")</b><br>";
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
						 * String label =
						 * resource.getLabel(LocaleUtil.getClientLanguage());
						 * 
						 * if (label==null) label=resource.getLabel("es"); if
						 * (label==null) label=resource.getLabel("ca"); if
						 * (label==null) label=resource.getLabel("gl"); if
						 * (label==null) label=resource.getLabel("eu");
						 */

						String uri = resource.getUri();
						String text = (labelText.length() == 0 ? uri : labelText) + "<br>";
						if (resource.getFirstGeometry().getType() == Type.POINT) {
							String latPoint = String.valueOf(latLong.getLatitude());
							String longPoint = String.valueOf(latLong.getLongitude());

							text = text + "<b>" + geoMessages.latitude() + "</b>" + latPoint + "<br>";
							text = text + "<b>" + geoMessages.longitude() + "</b>" + longPoint + "<br>";
						}
						iwc = new InfoWindowContent(text + "<br>" + geoMessages.information() + " <a href='"
								+ resource.getUri() + "' target='_blank'>" + geoMessages.here() + "</a>");
						getWindow().open(latLong, iwc);
					}
				});
			}
		}
	}

	private void drawGeometry(Geometry geometry, final ClickHandler clickHandler) {
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
			Polyline line = new Polyline(GoogleMapsAdapters.getLatLngs(((PolyLine) geometry).getPoints()), "#FF5B00", 3);
			line.addPolylineClickHandler(new PolylineClickHandler() {
				@Override
				public void onClick(PolylineClickEvent event) {
					latLong = event.getLatLng();
					clickHandler.onClick(null); // TODO: null???
				}
			});
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

}
