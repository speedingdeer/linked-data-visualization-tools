/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
<<<<<<< HEAD
 * Facultad de Inform‡tica, Universidad 
 * PolitŽcnica de Madrid, Spain
=======
 * Facultad de Informetica, Universidad 
 * Politecnica de Madrid, Spain
>>>>>>> master
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

<<<<<<< HEAD
import java.util.List;
import java.util.Set;

=======
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.gwtopenmaps.openlayers.client.event.VectorFeatureAddedListener;
import org.gwtopenmaps.openlayers.client.layer.Vector;

>>>>>>> master
import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
<<<<<<< HEAD
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geom.LatLng;
=======
>>>>>>> master
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResourcesAsKmlUrl;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
<<<<<<< HEAD
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.GoogleMapsAdapters;
=======
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterClearEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterClearHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.DrawingModeChangeEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.DrawingModeChangeHandler;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.client.view.v2.MapView;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
>>>>>>> master
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;

/**
 * @author Alexander De Leon
 */
@Singleton
<<<<<<< HEAD
public class MapPresenter extends ControlPresenter<MapPresenter.Display> implements FacetConstraintsChangedHandler {

	private Set<FacetConstraint> facetConstraints;
	private final DispatchAsync dispatchAsync;

	public interface Display extends WidgetDisplay {
		MapWidget getMap();
=======
public class MapPresenter extends ControlPresenter<MapPresenter.Display> implements FacetConstraintsChangedHandler, DrawingModeChangeHandler, AreaFilterClearHandler {

	private Set<FacetConstraint> facetConstraints;
	private final DispatchAsync dispatchAsync;
	private StatisticDefinition statisticDefinition;
	
	public interface Display extends WidgetDisplay, MapView {

		TwoDimentionalCoordinate getCurrentCenter();

		BoundingBox getVisibleBox();

		void setVisibleBox(BoundingBox boundingBox);
>>>>>>> master

		void drawGeoResouces(List<GeoResource> resources);

		void clear();
<<<<<<< HEAD

=======
		
		void setDrawing(Boolean value);
		
		void clearDrawing();
		
		Vector getDrawingVector();
		
>>>>>>> master
		HasClickHandlers getKmlButton();
	}

	@Inject
	public MapPresenter(Display display, EventBus eventBus, DispatchAsync dispatchAsync) {
		super(display, eventBus);
		this.dispatchAsync = dispatchAsync;
		eventBus.addHandler(FacetConstraintsChangedEvent.getType(), this);
<<<<<<< HEAD
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
=======
		eventBus.addHandler(DrawingModeChangeEvent.getType(), this);
		eventBus.addHandler(AreaFilterClearEvent.getType(), this);
	}

	public TwoDimentionalCoordinate getCurrentCenter() {
		return getDisplay().getCurrentCenter();
	}

	public BoundingBox getVisibleBox() {
		return getDisplay().getVisibleBox();
	}

	public void setVisibleBox(BoundingBox boundingBox) {
		getDisplay().setVisibleBox(boundingBox);
>>>>>>> master
	}

	public void drawGeoResouces(List<GeoResource> resources) {
		getDisplay().drawGeoResouces(resources);
	}

	public void clear() {
		getDisplay().clear();
<<<<<<< HEAD
=======
		facetConstraints = null;
	}
	
	public void clearDrawing(){
		getDisplay().clearDrawing();
>>>>>>> master
	}

	@Override
	public void onFacetConstraintsChanged(FacetConstraintsChangedEvent event) {
		facetConstraints = event.getConstraints();
	}

	/* ----------- presenter callbacks -- */
<<<<<<< HEAD
	@Override
	protected void onBind() {
		getDisplay().getKmlButton().addClickHandler(new ClickHandler() {

=======
	@Override 
	protected void onBind() {
		getDisplay().getKmlButton().addClickHandler(new ClickHandler() {
>>>>>>> master
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
<<<<<<< HEAD
=======
		
		getDisplay().getDrawingVector().addVectorFeatureAddedListener(new VectorFeatureAddedListener(){

			@Override
			public void onFeatureAdded(FeatureAddedEvent eventObject) {
				eventBus.fireEvent(new AreaFilterChangedEvent());
			}
			
		});
>>>>>>> master
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
<<<<<<< HEAD

=======
	}

	@Override
	public void onDrawingStart(DrawingModeChangeEvent drawingStartEvent) {
		getDisplay().setDrawing(drawingStartEvent.getDrawingMode());
	}

	@Override
	public void onAreaFilterClear(AreaFilterClearEvent areaFilterClearEvent) {
		if(getDisplay().getDrawingVector() != null && getDisplay().getDrawingVector().getFeatures() != null && getDisplay().getDrawingVector().getFeatures().length > 0) {
			getDisplay().getDrawingVector().destroyFeatures();
			eventBus.fireEvent(new AreaFilterChangedEvent());		
		}
>>>>>>> master
	}

}
