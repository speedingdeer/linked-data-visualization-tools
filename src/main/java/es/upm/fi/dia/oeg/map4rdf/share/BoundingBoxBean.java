/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informetica, Universidad 
 * Politecnica de Madrid, Spain
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
import org.gwtopenmaps.openlayers.client.LonLat;

/**
 * @author Alexander De Leon
 */
public class BoundingBoxBean implements BoundingBox, Serializable {

	private TwoDimentionalCoordinate bottomLeft;
	private TwoDimentionalCoordinate topRight;
	private TwoDimentionalCoordinate centre;

	private TwoDimentionalCoordinate top;
	private TwoDimentionalCoordinate bottom;
	private TwoDimentionalCoordinate right;
	private TwoDimentionalCoordinate left;
	
	BoundingBoxBean() {
		// for serialization
	}

	public BoundingBoxBean(TwoDimentionalCoordinate bottomLeft, TwoDimentionalCoordinate topRight) {
		this.bottomLeft = bottomLeft;
		this.topRight = topRight;
		this.top = topRight;
		this.bottomLeft = bottomLeft;
		this.left = bottomLeft;
		this.right = topRight;
	}

	public BoundingBoxBean(TwoDimentionalCoordinate dim1, TwoDimentionalCoordinate dim2, 
								TwoDimentionalCoordinate dim3, TwoDimentionalCoordinate dim4) {
		TwoDimentionalCoordinate[] cor = { dim1, dim2, dim3, dim4 };
		
		//get top
		for (TwoDimentionalCoordinate c : cor) {
			if (this.top == null) {
				this.top = c;
			} else {
				if(this.top.getY() < c.getY()) {
					this.top = c;
				}
			}
		}
		//get bottom
		for (TwoDimentionalCoordinate c : cor) {
			if (this.bottom == null) {
				this.bottom = c;
			} else {
				if(this.bottom.getY() > c.getY()) {
					this.bottom = c;
				}
			}
		}
		//get left
		for (TwoDimentionalCoordinate c : cor) {
			if (this.left == null) {
				this.left = c;
			} else {
				if(this.left.getX() > c.getX()) {
					this.left = c;
				}
			}
		}
		
		//get right
		for (TwoDimentionalCoordinate c : cor) {
			if (this.right == null) {
				this.right = c;
			} else {
				if(this.right.getX() < c.getX()) {
					this.right = c;
				}
			}
		}
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
		//@TODO develop for four coordinates
		if (centre == null) {
			centre = new TwoDimentionalCoordinateBean((topRight.getX() + bottomLeft.getX()) / 2d,
					(topRight.getY() + bottomLeft.getY()) / 2d);
		}
		return centre;
	}

	@Override
	public int hashCode() {
		//@TODO develop for four coordinates
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

	@Override
	public TwoDimentionalCoordinate getTop() {
		return top;
	}

	@Override
	public TwoDimentionalCoordinate getBottom() {
		return bottom;
	}

	@Override
	public TwoDimentionalCoordinate getLeft() {
		return left;
	}

	@Override
	public TwoDimentionalCoordinate getRight() {
		return right;
	}

	@Override
	public void transform(String from, String to) {
		
		if (this.topRight != null) {
			LonLat tmp = new LonLat(topRight.getX(), topRight.getY());	
			tmp.transform(from ,to);
			this.topRight = new TwoDimentionalCoordinateBean(tmp.lon(), tmp.lat());
		}
		if (this.bottomLeft != null) {
			LonLat tmp = new LonLat(bottomLeft.getX(), bottomLeft.getY());	
			tmp.transform(from ,to);
			this.bottomLeft = new TwoDimentionalCoordinateBean(tmp.lon(), tmp.lat());
		}
		if (this.bottom !=  null) {
			LonLat tmp = new LonLat(bottom.getX(), bottom.getY());	
			tmp.transform(from ,to);
			this.bottom = new TwoDimentionalCoordinateBean(tmp.lon(), tmp.lat());
		}
		if (this.top != null) {
			LonLat tmp = new LonLat(top.getX(), top.getY());	
			tmp.transform(from ,to);
			this.top = new TwoDimentionalCoordinateBean(tmp.lon(), tmp.lat());
		}
		if (this.left != null) {
			LonLat tmp = new LonLat(left.getX(), left.getY());	
			tmp.transform(from ,to);
			this.left = new TwoDimentionalCoordinateBean(tmp.lon(), tmp.lat());
		}
		if (this.right != null) {
			LonLat tmp = new LonLat(right.getX(), right.getY());	
			tmp.transform(from ,to);
			this.right = new TwoDimentionalCoordinateBean(tmp.lon(), tmp.lat());
		}
	}

}
