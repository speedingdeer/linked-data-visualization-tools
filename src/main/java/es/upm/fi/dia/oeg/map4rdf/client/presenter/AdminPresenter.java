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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.db.GetValues;
import es.upm.fi.dia.oeg.map4rdf.client.action.db.IsLogged;
import es.upm.fi.dia.oeg.map4rdf.client.action.db.Logout;
import es.upm.fi.dia.oeg.map4rdf.client.action.db.SaveValues;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
//import es.upm.fi.dia.oeg.map4rdf.client.services.IDBService;
//import es.upm.fi.dia.oeg.map4rdf.client.services.IDBServiceAsync;
//import es.upm.fi.dia.oeg.map4rdf.client.services.ISessionsService;
//import es.upm.fi.dia.oeg.map4rdf.client.services.ISessionsServiceAsync;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import java.util.ArrayList;
import java.util.List;
import name.alexdeleon.lib.gwtblocks.client.PagePresenter;


/**
 * @author Alexander De Leon
 */
@Singleton
public class AdminPresenter extends  PagePresenter<AdminPresenter.Display> {

	public interface Display extends WidgetDisplay {

        public void clear();
        public Button getLogoutButton();
        public void setVisibility(Boolean visibility);
        public void fullfilForm(List<ConfigPropertie> result);
        public Button getSaveButton();
        public Boolean checkAdminPassword();
        public List<ConfigPropertie> getPropertieMap();

	}
   //  private ISessionsServiceAsync sessionsService;
   // private IDBServiceAsync dbService;
    
	private final DispatchAsync dispatchAsync;
	private final BrowserMessages messages;
    //private IDBServiceAsync propertiesServiceAsync;
	@Inject
	public AdminPresenter(Display display, EventBus eventBus, DispatchAsync dispatchAsync, BrowserMessages messages) {
		super(display, eventBus);
		this.dispatchAsync = dispatchAsync;
		this.messages = messages;
        //sessionsService = GWT.create(ISessionsService.class);
        //dbService = GWT.create(IDBService.class);
    }

	/* -------------- Presenter callbacks -- */
	@Override
	protected void onBind() {
		display.getLogoutButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
            	dispatchAsync.execute(new Logout(), new AsyncCallback<SingletonResult<Boolean>>() {
					@Override
					public void onFailure(Throwable caught) {
					}
					@Override
					public void onSuccess(SingletonResult<Boolean> result) {
						Window.Location.assign('#'+Places.DEFAULT.toString());
					}
            	});
            }
        });
        display.getSaveButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
             if (display.checkAdminPassword()) {
            	 dispatchAsync.execute(new SaveValues(getDisplay().getPropertieMap()), new AsyncCallback<SingletonResult<Boolean>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(messages.databaseConenctionProblem());
					}

					@Override
					public void onSuccess(SingletonResult<Boolean> result) {
						if (result.getValue()) {
							Window.alert("Changes were saved");
						} else {
							Window.alert("Session expired");
							Window.Location.assign("#"+Places.LOGIN);
						}
					}
				});
             } else {
                 Window.alert(messages.passwordsDoesNotMatch());
             }
            }
        });
        
	}

	@Override
	protected void onUnbind() {
	}
	
    @Override
    protected void onRefreshDisplay() {
    }

    @Override
    protected void onRevealDisplay() {        
    	getDisplay().clear();
    }

    @Override
    public Place getPlace() {
         return Places.ADMIN;
    }

    @Override
    protected void onPlaceRequest(PlaceRequest request) {
        display.setVisibility(Boolean.FALSE);
        dispatchAsync.execute(new IsLogged(), new AsyncCallback<SingletonResult<Boolean>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(messages.databaseConenctionProblem());
			}
			@Override
			public void onSuccess(SingletonResult<Boolean> result) {
				if (result.getValue()) {
					fillUpContent();
				} else {
					Window.Location.assign('#'+Places.LOGIN.toString());
				}
			}
		});
    }
    private void fillUpContent(){
        display.setVisibility(Boolean.TRUE);
        ArrayList<String> paramNames = new ArrayList<String>();
        paramNames.add(ParameterNames.ENDPOINT_URL);
        paramNames.add(ParameterNames.FACETS_AUTO);
        paramNames.add(ParameterNames.GEOMETRY_MODEL);
        paramNames.add(ParameterNames.GOOGLE_MAPS_API_KEY);
        paramNames.add(ParameterNames.EDIT_DEPTH);
        paramNames.add(ParameterNames.RDF_STORE_PATH);
        dispatchAsync.execute(new GetValues(paramNames),new AsyncCallback<ListResult<ConfigPropertie>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onSuccess(ListResult<ConfigPropertie> result) {
					getDisplay().fullfilForm(result.asList());
			}
				
		});
    }

}
