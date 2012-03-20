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
package es.upm.fi.dia.oeg.map4rdf.client.event;

/**
 * @author Alexander De Leon
 */
public abstract class MyMapletEventHandler implements MapletEventHandler {

	private final String mapletId;

	public MyMapletEventHandler(String mapletId) {
		this.mapletId = mapletId;
	}

	@Override
	public void mapletActivated(MapletEvent event) {
		if (event.getMapletId().equals(mapletId)) {
			myMapletActivated(event);
		}
	}

	@Override
	public void mapletDeactivated(MapletEvent event) {
		if (event.getMapletId().equals(mapletId)) {
			myMapletDeactivated(event);
		}
	}

	protected abstract void myMapletActivated(MapletEvent event);

	protected abstract void myMapletDeactivated(MapletEvent event);

}
