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

import java.util.List;
import java.util.Set;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResourcesAsKmlUrl;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.GoogleMapsAdapters;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;

/**
 * @author Alexander De Leon
 */
@Singleton
public class MapPresenter extends ControlPresenter<MapPresenter.Display> implements FacetConstraintsChangedHandler {

	private Set<FacetConstraint> facetConstraints;
	private final DispatchAsync dispatchAsync;

	public interface Display extends WidgetDisplay {
		MapWidget getMap();

		void drawGeoResouces(List<GeoResource> resources);

		void clear();

		HasClickHandlers getKmlButton();
	}

	@Inject
	public MapPresenter(Display display, EventBus eventBus, DispatchAsync dispatchAsync) {
		super(display, eventBus);
		this.dispatchAsync = dispatchAsync;
		eventBus.addHandler(FacetConstraintsChangedEvent.getType(), this);
	}

	public TwoDimentionalCoordinate getCurrentCenter() {
		LatLng googleLatLng = getDisplay().getMap().getCenter();
		return GoogleMapsAdapters.getTwoDimentionalCoordinate(googleLatLng);
	}

	public BoundingBox getVisibleBox() {
		return GoogleMapsAdapters.getBoundingBox(getDisplay().getMap().getBounds());
	}

	public void setVisibleBox(BoundingBox boundingBox) {
		int zoomLevel = getDisplay().getMap().getBoundsZoomLevel(GoogleMapsAdapters.getLatLngBounds(boundingBox));
		getDisplay().getMap().setCenter(GoogleMapsAdapters.getLatLng(boundingBox.getCenter()));
		getDisplay().getMap().setZoomLevel(zoomLevel);
	}

	public void drawGeoResouces(List<GeoResource> resources) {
		getDisplay().drawGeoResouces(resources);
	}

	public void clear() {
		getDisplay().clear();
	}

	@Override
	public void onFacetConstraintsChanged(FacetConstraintsChangedEvent event) {
		facetConstraints = event.getConstraints();
	}

	/* ----------- presenter callbacks -- */
	@Override
	protected void onBind() {
		getDisplay().getKmlButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				GetGeoResourcesAsKmlUrl action = new GetGeoResourcesAsKmlUrl(getVisibleBox());
				action.setFacetConstraints(facetConstraints);
				dispatchAsync.execute(action, new AsyncCallback<SingletonResult<String>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(SingletonResult<String> result) {
						Window.open(GWT.getModuleBaseURL() + result.getValue(), "kml", null);
					}
				});
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
