/**
 * Copyright (c) 2010 Alexander De Leon Battista
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
package es.upm.fi.dia.oeg.map4rdf.client.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.place.PlaceRequestEvent;
import net.customware.gwt.presenter.client.place.PlaceRequestHandler;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.event.UrlParametersChangeEvent;

/**
 * @author Filip
 */
public class AppController extends WidgetPresenter<AppController.Display> implements PlaceRequestHandler {

	public interface Display extends WidgetDisplay {
		void setContent(Widget widget);
	}

	private final Set<WidgetPresenter<?>> presenters;

	public AppController(Display display, EventBus eventBus) {
		super(display, eventBus);
		presenters = new HashSet<WidgetPresenter<?>>();
		eventBus.addHandler(PlaceRequestEvent.getType(), this);
	}

	public AppController(Display display, EventBus eventBus, WidgetPresenter<?>... presenters) {
		this(display, eventBus);
		Collections.addAll(this.presenters, presenters);
	}
    

	public void addPresenter(WidgetPresenter<?> presenter) {
		presenters.add(presenter);
	}

	public void removePresenter(WidgetPresenter<?> presenter) {
		presenters.remove(presenter);
	}

    @Override
	public void onPlaceRequest(PlaceRequestEvent event) {
    	Place place = getPlaceFromQueryString(event);
    	HashMap<String, String> myMap = getParamtersMap(event);
		if (place == null) {
			return;
		}
        for (WidgetPresenter<?> presenter : presenters) {
			if (place.equals(presenter.getPlace())) {
				if (myMap != null) {
					UrlParametersChangeEvent parametersChangeEvent = new UrlParametersChangeEvent(myMap);
					eventBus.fireEvent(parametersChangeEvent);
				}
                getDisplay().setContent(presenter.getDisplay().asWidget());
				break;
			}
		}
	}

	/* ----- Presenter API -- */
	@Override
	public Place getPlace() {
		// This is the default place
		return null;
	}

	@Override
	protected void onBind() {
		// bind children
		for (WidgetPresenter<?> presenter : presenters) {
			presenter.bind();
		}
	}

	@Override
	protected void onPlaceRequest(PlaceRequest request) {
		// empty
	}

	@Override
	protected void onUnbind() {
		// unbind children
		for (WidgetPresenter<?> presenter : presenters) {
			presenter.unbind();
		}
	}

	public void refreshDisplay() {
		// empty
	}

	public void revealDisplay() {
		// empty
	}
	private Place getPlaceFromQueryString(PlaceRequestEvent event){
		PlaceRequest placeRequest = event.getRequest();
		String originRequestAddress = placeRequest.toString();
		String address = "";
		
		if(originRequestAddress.contains("?")) {
			address = originRequestAddress.split("\\?")[0];
			return new Place(address);
		}
		
		return placeRequest.getPlace();
	}
	private HashMap<String, String> getParamtersMap(PlaceRequestEvent event){
		PlaceRequest placeRequest = event.getRequest();
		String originRequestAddress = placeRequest.toString();
		HashMap<String,String> paramsMap= new HashMap<String, String>();
		try {
			if(originRequestAddress.contains("?")) {
				String paramsString = originRequestAddress.split("\\?")[1];
				String[] map = paramsString.split(";");
				for(String pair : map) {
					paramsMap.put(pair.split("=")[0], pair.split("=")[1]);
				}
				return paramsMap;
			}
		} catch (IndexOutOfBoundsException e) {
			
		}
		return null;
	}
}
