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


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.services.ISessionsService;
import es.upm.fi.dia.oeg.map4rdf.client.services.ISessionsServiceAsync;
import name.alexdeleon.lib.gwtblocks.client.PagePresenter;

/**
 * @author Alexander De Leon
 */
@Singleton
public class LoginPresenter extends  PagePresenter<LoginPresenter.Display> {

   private ISessionsServiceAsync sessionsService;
    
	public interface Display extends WidgetDisplay {

        public void clear();
        public Button getLoginActionButton();
        public String getPassword();
    }
	private final DispatchAsync dispatchAsync;

	@Inject
	public LoginPresenter(Display display, EventBus eventBus, final DispatchAsync dispatchAsync) {
		super(display, eventBus);
		this.dispatchAsync = dispatchAsync;
        sessionsService = GWT.create(ISessionsService.class);
    }

	/* -------------- Presenter callbacks -- */
	@Override
	protected void onBind() {
		display.getLoginActionButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                sessionsService.login(display.getPassword(), new AsyncCallback<Boolean>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Check your database connection");
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(result) {
                            Cookies.setCookie("admin", "true");
                            Window.Location.assign("#" + Places.ADMIN);
                        } else {
                            Window.alert("Incorrect password");
                        }
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
    protected void onRefreshDisplay() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onRevealDisplay() {
        getDisplay().clear();
    }

    @Override
    public Place getPlace() {
         return Places.LOGIN;
    }

    @Override
    protected void onPlaceRequest(PlaceRequest request) {
    }

}
