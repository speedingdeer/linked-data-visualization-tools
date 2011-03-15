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

import java.util.ArrayList;
import java.util.List;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetFacetDefinitions;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetFacetDefinitionsResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.FacetPresenter.Display.FacetSelectionHandler;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.FacetGroup;

/**
 * @author Alexander De Leon
 */
@Singleton
public class FacetPresenter extends ControlPresenter<FacetPresenter.Display> {

	public interface Display extends WidgetDisplay {

		interface FacetSelectionHandler {
			void onFacetSelectionChanged(String facetId, String facetValueId, boolean selected);
		}

		// TODO this should be decoupled from the model
		void setFacets(List<FacetGroup> facets);

		void setFacetSelectionChangedHandler(FacetSelectionHandler handler);
	}

	private final DispatchAsync dispatchAsync;
	private final List<FacetConstraint> constraints = new ArrayList<FacetConstraint>();

	@Inject
	public FacetPresenter(Display display, EventBus eventBus, DispatchAsync dispatchAsync) {
		super(display, eventBus);
		this.dispatchAsync = dispatchAsync;
	}

	/* -------------- Presenter callbacks -- */
	@Override
	protected void onBind() {
		getDisplay().setFacetSelectionChangedHandler(new FacetSelectionHandler() {
			@Override
			public void onFacetSelectionChanged(String facetId, String facetValueId, boolean selected) {
				if (selected) {
					constraints.add(new FacetConstraint(facetId, facetValueId));
				} else {
					constraints.remove(new FacetConstraint(facetId, facetValueId));
				}
				fireFacetConstrainsChanged();
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
		loadFacets();
	}

	void loadFacets() {
		dispatchAsync.execute(new GetFacetDefinitions(), new AsyncCallback<GetFacetDefinitionsResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.toString());
			}

			@Override
			public void onSuccess(GetFacetDefinitionsResult result) {
				getDisplay().setFacets(result.getFacetDefinitions());
			}
		});
	}

	private void fireFacetConstrainsChanged() {
		eventBus.fireEvent(new FacetConstraintsChangedEvent(constraints));
	}
}
