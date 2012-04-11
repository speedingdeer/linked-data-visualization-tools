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
package es.upm.fi.dia.oeg.map4rdf.client.action;

import net.customware.gwt.dispatch.shared.Action;
import es.upm.fi.dia.oeg.map4rdf.share.AemetObs;
import es.upm.fi.dia.oeg.map4rdf.share.Intervalo;

/**
 * @author Alexander De Leon
 */
public class GetAemetObsForProperty implements Action<ListResult<AemetObs>> {

	private String stationUri;
	private String propertyUri;
	private Intervalo start;
	private Intervalo end;

	public GetAemetObsForProperty() {
		// empty
	}

	public GetAemetObsForProperty(String stationUri, String propertyUri, Intervalo start, Intervalo end) {
		super();
		this.stationUri = stationUri;
		this.propertyUri = propertyUri;
		this.start = start;
		this.end = end;
	}

	public String getStationUri() {
		return stationUri;
	}

	public void setStationUri(String stationUri) {
		this.stationUri = stationUri;
	}

	public String getPropertyUri() {
		return propertyUri;
	}

	public void setPropertyUri(String propertyUri) {
		this.propertyUri = propertyUri;
	}

	public Intervalo getStart() {
		return start;
	}

	public void setStart(Intervalo start) {
		this.start = start;
	}

	public Intervalo getEnd() {
		return end;
	}

	public void setEnd(Intervalo end) {
		this.end = end;
	}

}
