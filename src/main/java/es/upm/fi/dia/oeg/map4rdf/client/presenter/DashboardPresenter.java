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
package es.upm.fi.dia.oeg.map4rdf.client.presenter;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import java.util.Collections;
import java.util.Set;

import name.alexdeleon.lib.gwtblocks.client.PagePresenter;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResources;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.LoadResourceEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.LoadResourceEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.util.GeoUtils;
import es.upm.fi.dia.oeg.map4rdf.client.widget.DataToolBar;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;

/**
 * @author Alexander De Leon
 */
@Singleton
public class DashboardPresenter extends PagePresenter<DashboardPresenter.Display> implements
		FacetConstraintsChangedHandler, LoadResourceEventHandler, ValueChangeHandler {

	public interface Display extends WidgetDisplay {
                
                void addWestWidget(Widget widget, String header);
		HasWidgets getMapPanel();
                HasWidgets getAdminPanel();
		void showAdminView();
                void showMainView();
	
        }

	private final ResultsPresenter resultsPresenter;
	private final MapPresenter mapPresenter;
	private final FacetPresenter facetPresenter;
	private final AdminPresenter adminPresenter;
        private final DispatchAsync dispatchAsync;
	private final DataToolBar dataToolBar;
	private final BrowserMessages messages;

        @Override
        public void onValueChange(ValueChangeEvent event) {
            
            if((event.getValue().toString()).equals("dashboard")) {
                mapSite();
            }
            if((event.getValue().toString()).equals("admin")) {
                adminSite();
                facetPresenter.getDisplay().clearFacets();
            }
            refreshDisplay();
        }
        
        private void mapSite() {
            getDisplay().showMainView();
        }
        private void adminSite() {
            getDisplay().showAdminView();
        }
        
	@Inject
	public DashboardPresenter(Display display, EventBus eventBus, FacetPresenter facetPresenter,
			MapPresenter mapPresenter, ResultsPresenter resultsPresenter,AdminPresenter adminPresenter, DispatchAsync dispatchAsync,
			DataToolBar dataToolBar, BrowserMessages messages) {
		super(display, eventBus);
		this.messages = messages;
		this.mapPresenter = mapPresenter;
		this.facetPresenter = facetPresenter;
		this.resultsPresenter = resultsPresenter;
		this.adminPresenter = adminPresenter;
                this.dispatchAsync = dispatchAsync;
		this.dataToolBar = dataToolBar;

		addControl(mapPresenter);
		addControl(facetPresenter);

		// registered for app-level events
		eventBus.addHandler(FacetConstraintsChangedEvent.getType(), this);
		eventBus.addHandler(LoadResourceEvent.getType(), this);
	}

	@Override
	public void onFacetConstraintsChanged(FacetConstraintsChangedEvent event) {
		// TODO: add constraints to the Place
		mapPresenter.clear();
		resultsPresenter.clear();
		loadResources(mapPresenter.getVisibleBox(), event.getConstraints());
	}

	@Override
	public void onLoadResource(LoadResourceEvent event) {
		mapPresenter.clear();
		resultsPresenter.clear();
		loadResource(event.getResourceUri());
	}

	/* -------------- Presenter callbacks -- */
	@Override
	public Place getPlace() {
		return Places.DASHBOARD;
	}

	@Override
	protected void onBind() {
		// attach children
		getDisplay().addWestWidget(facetPresenter.getDisplay().asWidget(), "Facets");

		getDisplay().addWestWidget(dataToolBar, messages.overlays());
		getDisplay().addWestWidget(resultsPresenter.getDisplay().asWidget(), messages.results());

		//getDisplay().getOverlayPanel().add(overlayPresenter.getDisplay().asWidget());
		getDisplay().getMapPanel().add(mapPresenter.getDisplay().asWidget());
                getDisplay().getAdminPanel().add(adminPresenter.getDisplay().asWidget());
	}

	@Override
	protected void onPlaceRequest(PlaceRequest request) {

	}

	@Override
	protected void onUnbind() {
		// empty
	}

	@Override
	protected void onRefreshDisplay() {

	}

	@Override
	protected void onRevealDisplay() {
		mapPresenter.clear();
		resultsPresenter.clear();
		loadResources(mapPresenter.getVisibleBox(), null);
	}

	/* --------------- helper methods --- */
	void loadResources(BoundingBox boundingBox, Set<FacetConstraint> constraints) {
		GetGeoResources action = new GetGeoResources(boundingBox);
		if (constraints != null) {
			action.setFacetConstraints(constraints);
		}
		mapPresenter.getDisplay().startProcessing();
		dispatchAsync.execute(action, new AsyncCallback<ListResult<GeoResource>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				mapPresenter.getDisplay().stopProcessing();
			}

			@Override
			public void onSuccess(ListResult<GeoResource> result) {
				mapPresenter.drawGeoResouces(result.asList());
				resultsPresenter.setResults(result.asList());
				mapPresenter.getDisplay().stopProcessing();
			}
		});
	}

	void loadResource(String uri) {
		GetGeoResource action = new GetGeoResource(uri);
		mapPresenter.getDisplay().startProcessing();
		dispatchAsync.execute(action, new AsyncCallback<SingletonResult<GeoResource>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				mapPresenter.getDisplay().stopProcessing();
			}

			@Override
			public void onSuccess(SingletonResult<GeoResource> result) {
				mapPresenter.drawGeoResouces(Collections.singletonList(result.getValue()));
				mapPresenter.setVisibleBox(GeoUtils.computeBoundingBoxFromGeometries(result.getValue().getGeometries()));
				mapPresenter.getDisplay().stopProcessing();
			}
		});
	}
}