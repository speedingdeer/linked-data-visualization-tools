/**
 * Copyright (c) 2012 Ontology Engineering Group,
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

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.share.ShapeFileDefinition;

/**
 * @author Jonathan Gonzalez
 */
@Singleton
public class ShapeFileMapControl extends LayerControl implements MapControl {

	private final DispatchAsync dispatchAsync;
	private ShapeFileDefinition shapeFile;
	private TimelineControl timelineControl;
	private final BrowserResources resources;

	@Inject
	public ShapeFileMapControl(DispatchAsync dispatchAsync, BrowserResources resources) {
		this.dispatchAsync = dispatchAsync;
		this.resources = resources;
	}

	@Override
	public void disable() {
		super.disable();
		getMap().removeControl(timelineControl);
	}

	public void setShapeFile(ShapeFileDefinition shapeFile) {
		this.shapeFile = shapeFile;
        // TODO(jonathangsc): To be implemented.
	}

	private void drawShapeFile(ShapeFileDefinition shapeFileDefininition) {
	    // TODO(jonathangsc): To be implemented.
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

}