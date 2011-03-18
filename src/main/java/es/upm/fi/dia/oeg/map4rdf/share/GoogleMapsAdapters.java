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
package es.upm.fi.dia.oeg.map4rdf.share;

import java.util.Collection;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;

/**
 * @author Alexander De Leon
 */
public class GoogleMapsAdapters {

	/* ------------- factory methods --- */
	public static TwoDimentionalCoordinate getTwoDimentionalCoordinate(LatLng latLng) {
		return new TwoDimentionalCoordinateBean(latLng.getLongitude(), latLng.getLatitude());
	}

	public static LatLng getLatLng(TwoDimentionalCoordinate coordinate) {
		return LatLng.newInstance(coordinate.getY(), coordinate.getX());
	}

	public static BoundingBox getBoundingBox(LatLngBounds bonds) {
		return new BoundingBoxBean(getTwoDimentionalCoordinate(bonds.getSouthWest()), getTwoDimentionalCoordinate(bonds
				.getNorthEast()));
	}

	public static LatLngBounds getLatLngBounds(BoundingBox boundingBox) {
		return LatLngBounds.newInstance(getLatLng(boundingBox.getBottomLeft()), getLatLng(boundingBox.getTopRight()));
	}

	public static <E extends TwoDimentionalCoordinate> LatLng[] getLatLngs(Collection<E> coordinates) {
		LatLng[] points = new LatLng[coordinates.size()];
		int i = 0;
		for (TwoDimentionalCoordinate c : coordinates) {
			points[i++] = getLatLng(c);
		}
		return points;
	}

}
