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
public class FacetConstraint implements Serializable {

	String facetId;
	String facetValueId;

	FacetConstraint() {
		// for serialization
	}

	public FacetConstraint(String facetId, String facetValueId) {
		this.facetId = facetId;
		this.facetValueId = facetValueId;
	}

	public String getFacetId() {
		return facetId;
	}

	public String getFacetValueId() {
		return facetValueId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((facetId == null) ? 0 : facetId.hashCode());
		result = prime * result + ((facetValueId == null) ? 0 : facetValueId.hashCode());
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
		FacetConstraint other = (FacetConstraint) obj;
		if (facetId == null) {
			if (other.facetId != null) {
				return false;
			}
		} else if (!facetId.equals(other.facetId)) {
			return false;
		}
		if (facetValueId == null) {
			if (other.facetValueId != null) {
				return false;
			}
		} else if (!facetValueId.equals(other.facetValueId)) {
			return false;
		}
		return true;
	}

}
