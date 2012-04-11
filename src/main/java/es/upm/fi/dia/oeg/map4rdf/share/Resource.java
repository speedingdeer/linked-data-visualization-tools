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
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Alexander De Leon
 */
public class Resource implements HasUri, Serializable {

	private static Set<String> EMPTY_SET = Collections.emptySet();

	private String uri;
	private HashMap<String, String> labels;
	private Resource type;

	public Resource(String uri) {
		this.uri = uri;
	}

	Resource() {
		// for serialization
	}

	@Override
	public String getUri() {
		return uri;
	}

	public String getLabel(String language) {
		if (labels == null) {
			return getUri();
		}
		String label = labels.get(language);
		if (label == null) {
			return getUri();
		}
		return label;
	}

	public void addLabel(String language, String label) {
		if (labels == null) {
			labels = new HashMap<String, String>();
		}
		labels.put(language, label);
	}

	public String getDefaultLabel() {
		return getLabel("");
	}

	public Resource getType() {
		return type;
	}

	public void setType(Resource type) {
		this.type = type;
	}

	public void setType(String typeUri) {
		type = new Resource(typeUri);
	}

	public Set<String> getLangs() {
		if (labels == null) {
			return EMPTY_SET;
		}
		return labels.keySet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Resource other = (Resource) obj;
		if (uri == null) {
			if (other.uri != null) {
				return false;
			}
		} else if (!uri.equals(other.uri)) {
			return false;
		}
		return true;
	}

}
