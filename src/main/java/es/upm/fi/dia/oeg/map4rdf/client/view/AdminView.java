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
package es.upm.fi.dia.oeg.map4rdf.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.AdminPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.services.IPropertiesService;
import es.upm.fi.dia.oeg.map4rdf.client.services.IPropertiesServiceAsync;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.share.db.StringProcessor;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander De Leon
 */
public class AdminView extends Composite implements AdminPresenter.Display {

        private FlowPanel mainPanel;
        private FlowPanel loginPanel;
        private FlowPanel adminPanel;
        
        private Button loginButton;
        private Button backButton;
        private Label loginLabel;
        private PasswordTextBox loginTextBox;
        
        private Button saveButton;
        private Button canelBackButton;
        
        private Label enpointLabel;
        private TextBox endpointTextBox ;
        private Label geometryLabel;
        private TextBox geometryTextBox ;
        private Label apikKeyLabel;
        private TextBox apiKeyTextBox ;
        private Label facetConfLabel;
        private TextBox facetConfTextBox ;
        
        private IPropertiesServiceAsync propertiesServiceAsync;
        
        
        
        
	@Inject
	public AdminView(BrowserResources resources) {
               
                propertiesServiceAsync = GWT.create(IPropertiesService.class);
                final AsyncCallback<List<ConfigPropertie>> valuesLoadCallback = new AsyncCallback<List<ConfigPropertie>>(){

                    @Override
                    public void onFailure(Throwable caught) {
                       Window.alert("Check your database connection");
                    }

                    @Override
                    public void onSuccess(List<ConfigPropertie> result) {
                        for(ConfigPropertie p : result) {
                            if (p.getKey().equals(ParameterNames.ENDPOINT_URL)){
                                endpointTextBox.setValue(p.getValue());
                            }
                            else if (p.getKey().equals(ParameterNames.GOOGLE_MAPS_API_KEY)){
                                apiKeyTextBox.setValue(p.getValue());     
                            }
                            else if (p.getKey().equals(ParameterNames.GEOMETRY_MODEL)){
                                geometryTextBox.setValue(p.getValue());    
                            }
                            else if (p.getKey().equals(ParameterNames.FACETS_AUTO)){
                                facetConfTextBox.setValue(p.getValue());    
                            }
                        }
                    }
                };
                    
                final AsyncCallback<String> loginCallback = new AsyncCallback<String>(){

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Check your database connection");
                    }

                    @Override
                    public void onSuccess(String result) {
                        if(StringProcessor.decrypt(result).equals(loginTextBox.getValue())) {
                            //in case your password is correct
                            loginPanel.setVisible(false);
                            adminPanel.setVisible(true);
                            List<String> keysList = new ArrayList<String>();
                            keysList.add(ParameterNames.ENDPOINT_URL);
                            keysList.add(ParameterNames.GOOGLE_MAPS_API_KEY);
                            keysList.add(ParameterNames.GEOMETRY_MODEL);
                            keysList.add(ParameterNames.FACETS_AUTO);
                            
                            propertiesServiceAsync.getValues(keysList,valuesLoadCallback);
                            
                        } else {
                            Window.alert("Your password is incorrect");
                        }
                    }
                };
                
                final AsyncCallback<Boolean> saveCallback = new AsyncCallback<Boolean>(){

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Unexpected exception");
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        Window.Location.assign("#" + Places.DASHBOARD);
                        Window.Location.reload();
                    }

        
                };
                
                initWidget(createUi());
                
                loginLabel = new Label("password:");
                loginPanel.add(loginLabel);
                
                loginTextBox = new PasswordTextBox();
                loginPanel.add(loginTextBox);
                
                
                loginButton = new Button("login");
                loginPanel.add(loginButton);
                    
                
                loginButton.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        propertiesServiceAsync.getValue("admin",loginCallback);
                    }
                });
                
                backButton = new Button("back");
                loginPanel.add(backButton);
                
                backButton.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        Window.Location.assign("#" + Places.DASHBOARD);
                        Window.Location.reload();
                    }
                });
                
                enpointLabel = new Label("endpoint_url:");
                adminPanel.add(enpointLabel);
                endpointTextBox = new TextBox();
                adminPanel.add(endpointTextBox);
                
                geometryLabel = new Label("geometry:");
                adminPanel.add(geometryLabel);
                geometryTextBox = new TextBox();
                adminPanel.add(geometryTextBox);
                
                apikKeyLabel = new Label("google api key:");
                adminPanel.add(apikKeyLabel);
                apiKeyTextBox = new TextBox();
                adminPanel.add(apiKeyTextBox);
                
                facetConfLabel = new Label("facet automatic:");
                adminPanel.add(facetConfLabel);
                facetConfTextBox = new TextBox();
                adminPanel.add(facetConfTextBox);
                
                
                saveButton = new Button("save");
                
                saveButton.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        List<ConfigPropertie> list = new ArrayList<ConfigPropertie>();
                        list.add(new ConfigPropertie(ParameterNames.ENDPOINT_URL,endpointTextBox.getValue()));
                        list.add(new ConfigPropertie(ParameterNames.GOOGLE_MAPS_API_KEY,apiKeyTextBox.getValue()));
                        list.add(new ConfigPropertie(ParameterNames.GEOMETRY_MODEL,geometryTextBox.getValue()));
                        list.add(new ConfigPropertie(ParameterNames.FACETS_AUTO,facetConfTextBox.getValue()));
                        propertiesServiceAsync.setValues(list, saveCallback);
                    }
                });
                
                adminPanel.add(saveButton);
                
                canelBackButton = new Button("cancel/back");
                adminPanel.add(canelBackButton);
                
                canelBackButton.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        Window.Location.assign("#" + Places.DASHBOARD);
                        Window.Location.reload();
                    }
                });
            }

	/* ------------- Display API -- */
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void startProcessing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopProcessing() {
		// TODO Auto-generated method stub

	}

	/* ---------------- helper methods -- */
	private Widget createUi() {
                mainPanel = new FlowPanel();
		loginPanel = new FlowPanel();
                adminPanel = new FlowPanel();
                loginPanel.setVisible(true);
                adminPanel.setVisible(false);
		mainPanel.add(adminPanel);
                mainPanel.add(loginPanel);
                return mainPanel;
	}

}
