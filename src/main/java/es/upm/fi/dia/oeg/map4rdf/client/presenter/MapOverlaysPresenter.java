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
package es.upm.fi.dia.oeg.map4rdf.client.presenter;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapOverlaysPresenter.Display.OverlaySelectionHandler;

/**
 * @author Alexander De Leon
 */
@Singleton
public class MapOverlaysPresenter extends ControlPresenter<MapOverlaysPresenter.Display> {

	private final MapPresenter mapPresenter;

	@Inject
	public MapOverlaysPresenter(Display display, EventBus eventBus, MapPresenter mapPresenter) {
		super(display, eventBus);
		this.mapPresenter = mapPresenter;
	}

	public interface Display extends WidgetDisplay {

		interface OverlaySelectionHandler {
			void onOverlaySectionChanged(String overlayId);
		}

		void setOverlaySectionHandler(OverlaySelectionHandler handler);
	}

	@Override
	protected void onBind() {
		getDisplay().setOverlaySectionHandler(new OverlaySelectionHandler() {

			@Override
			public void onOverlaySectionChanged(String overlayId) {

			}
		});

	}

	@Override
	protected void onUnbind() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public void revealDisplay() {
		// TODO Auto-generated method stub

	}

}
