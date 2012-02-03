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


import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import name.alexdeleon.lib.gwtblocks.client.PagePresenter;

/**
 * @author Alexander De Leon
 */
@Singleton
public class EditResourcePresenter extends  PagePresenter<EditResourcePresenter.Display> {

	public interface Display extends WidgetDisplay {
        public void clear();
    }

	private final DispatchAsync dispatchAsync;

	@Inject
	public EditResourcePresenter(Display display, EventBus eventBus, final DispatchAsync dispatchAsync) {
		super(display, eventBus);
		this.dispatchAsync = dispatchAsync;
    }

	/* -------------- Presenter callbacks -- */
	@Override
	protected void onBind() {
	}

	@Override
	protected void onUnbind() {
		// TODO Auto-generated method stub

	}
    
        @Override
    protected void onRefreshDisplay() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onRevealDisplay() {
        getDisplay().clear();
    }

    @Override
    public Place getPlace() {
         return Places.EDIT_RESOURCE;
    }

    @Override
    protected void onPlaceRequest(PlaceRequest request) {
    }

}
