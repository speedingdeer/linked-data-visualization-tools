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
package es.upm.fi.dia.oeg.map4rdf.client;

import name.alexdeleon.lib.gwtblocks.client.AppController;
import net.customware.gwt.presenter.client.place.PlaceChangedEvent;
import net.customware.gwt.presenter.client.place.PlaceManager;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import es.upm.fi.dia.oeg.map4rdf.client.event.LoadResourceEvent;
import es.upm.fi.dia.oeg.map4rdf.client.inject.Injector;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;

/**
 * Entry point for the Browser UI
 * 
 * @author Alexander De Leon
 */
public class Browser implements EntryPoint {

	@Override
	public void onModuleLoad() {
		Injector injector = GWT.create(Injector.class);

		AppController controller = new AppController(injector.getBrowserUi(), injector.getEventBus(), injector
				.getDashboard());
		controller.bind();

		RootLayoutPanel.get().add(controller.getDisplay().asWidget());

		PlaceManager placeManager = new PlaceManager(injector.getEventBus());
		if (History.getToken() == null || History.getToken().length() == 0) {
			// Go to the default place
			injector.getEventBus().fireEvent(new PlaceChangedEvent(Places.DEFAULT.request()));
		}
		// Trigger history tokens.
		placeManager.fireCurrentPlace();

		String parameters[] = Window.Location.getQueryString().substring(1).split("&");
		for (String param : parameters) {
			String[] parts = param.split("=");
			if (parts[0].equals("uri")) {
				LoadResourceEvent.fire(parts[1], injector.getEventBus());
			}
		}

	}
}
