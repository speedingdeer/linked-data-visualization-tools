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
package es.upm.fi.dia.oeg.map4rdf.client.inject;

import net.customware.gwt.presenter.client.DefaultEventBus;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.gin.AbstractPresenterModule;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.DashboardPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.FacetPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapOverlaysPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.ResultsPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.view.DashboardView;
import es.upm.fi.dia.oeg.map4rdf.client.view.FacetView;
import es.upm.fi.dia.oeg.map4rdf.client.view.MapOverlaysView;
import es.upm.fi.dia.oeg.map4rdf.client.view.MapView;
import es.upm.fi.dia.oeg.map4rdf.client.view.ResultsView;

/**
 * @author Alexander De Leon
 */
public class InjectorModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

		// Events
		bind(EventBus.class).to(DefaultEventBus.class);

		// MVP
		bindDisplay(DashboardPresenter.Display.class, DashboardView.class);
		bindDisplay(FacetPresenter.Display.class, FacetView.class);
		bindDisplay(MapPresenter.Display.class, MapView.class);
		bindDisplay(ResultsPresenter.Display.class, ResultsView.class);
		bindDisplay(MapOverlaysPresenter.Display.class, MapOverlaysView.class);

	}
}
