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
package es.upm.fi.dia.oeg.map4rdf.share;

import java.util.Collection;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.geometry.Polygon;

/**
 * @author Alexander De Leon
 */
public class OpenLayersAdapter {

	/* ------------- factory methods --- */
	public static TwoDimentionalCoordinate getTwoDimentionalCoordinate(LonLat latLng) {
		return new TwoDimentionalCoordinateBean(latLng.lon(), latLng.lat());
	}

	public static LonLat getLatLng(TwoDimentionalCoordinate coordinate) {
		return new LonLat(coordinate.getX(), coordinate.getY());
	}

	public static BoundingBox getBoundingBox(Bounds bounds) {
		return new BoundingBoxBean(new TwoDimentionalCoordinateBean(bounds.getLowerLeftX(), bounds.getLowerLeftY()),
				new TwoDimentionalCoordinateBean(bounds.getUpperRightX(), bounds.getLowerLeftY()));
	}

	public static Bounds getLatLngBounds(BoundingBox boundingBox) {
		return new Bounds(boundingBox.getBottomLeft().getX(), boundingBox.getBottomLeft().getY(), boundingBox
				.getTopRight().getX(), boundingBox.getTopRight().getY());
	}

	public static <E extends TwoDimentionalCoordinate> LonLat[] getLatLngs(Collection<E> coordinates) {
		LonLat[] points = new LonLat[coordinates.size()];
		int i = 0;
		for (TwoDimentionalCoordinate c : coordinates) {
			points[i++] = getLatLng(c);
		}
		return points;
	}
	
	public static BoundingBox getBoudingBox(Polygon p) {

		int i = p.getComponents().length;
		double[][] dimTable = p.getComponents()[0].getCoordinateArray();
		if (dimTable.length != 5) {
			return null;
		}
		TwoDimentionalCoordinateBean dim1 = new TwoDimentionalCoordinateBean(dimTable[0][0],dimTable[0][1]);
		TwoDimentionalCoordinateBean dim2 = new TwoDimentionalCoordinateBean(dimTable[1][0],dimTable[1][1]);
		TwoDimentionalCoordinateBean dim3 = new TwoDimentionalCoordinateBean(dimTable[2][0],dimTable[2][1]);
		TwoDimentionalCoordinateBean dim4 = new TwoDimentionalCoordinateBean(dimTable[3][0],dimTable[3][1]);
		
		return new BoundingBoxBean(dim1,dim2,dim3,dim4);
	}

}
