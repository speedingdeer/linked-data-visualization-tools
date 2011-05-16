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
package es.upm.fi.dia.oeg.map4rdf.client.event;

import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Alexander De Leon
 */
public class LoadResourceEvent extends GwtEvent<LoadResourceEventHandler> {

	private static GwtEvent.Type<LoadResourceEventHandler> TYPE;

	public static void fire(String uri, EventBus bus) {
		LoadResourceEvent event = new LoadResourceEvent();
		event.uri = uri;
		bus.fireEvent(event);
	}

	public static Type<LoadResourceEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<LoadResourceEventHandler>();
		}
		return TYPE;
	}

	private String uri;

	private LoadResourceEvent() {
	}

	public String getResourceUri() {
		return uri;
	}

	@Override
	protected void dispatch(LoadResourceEventHandler handler) {
		handler.onLoadResource(this);
	}

	@Override
	public Type<LoadResourceEventHandler> getAssociatedType() {
		return getType();
	}

}
