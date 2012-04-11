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

import java.io.Serializable;

/**
 * @author Alexander De Leon
 */
public class BoundingBoxBean implements BoundingBox, Serializable {

	private TwoDimentionalCoordinate bottomLeft;
	private TwoDimentionalCoordinate topRight;
	private TwoDimentionalCoordinate centre;

	BoundingBoxBean() {
		// for serialization
	}

	public BoundingBoxBean(TwoDimentionalCoordinate bottomLeft, TwoDimentionalCoordinate topRight) {
		this.bottomLeft = bottomLeft;
		this.topRight = topRight;
	}

	@Override
	public TwoDimentionalCoordinate getBottomLeft() {
		return bottomLeft;
	}

	@Override
	public TwoDimentionalCoordinate getTopRight() {
		return topRight;
	}

	@Override
	public TwoDimentionalCoordinate getCenter() {
		if (centre == null) {
			centre = new TwoDimentionalCoordinateBean((topRight.getX() + bottomLeft.getX()) / 2d,
					(topRight.getY() + bottomLeft.getY()) / 2d);
		}
		return centre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bottomLeft == null) ? 0 : bottomLeft.hashCode());
		result = prime * result + ((topRight == null) ? 0 : topRight.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BoundingBoxBean other = (BoundingBoxBean) obj;
		if (bottomLeft == null) {
			if (other.bottomLeft != null) {
				return false;
			}
		} else if (!bottomLeft.equals(other.bottomLeft)) {
			return false;
		}
		if (topRight == null) {
			if (other.topRight != null) {
				return false;
			}
		} else if (!topRight.equals(other.topRight)) {
			return false;
		}
		return true;
	}

}
