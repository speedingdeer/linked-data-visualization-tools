/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informetica, Universidad 
 * Politecnica de Madrid, Spain
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
import es.upm.fi.dia.oeg.map4rdf.client.services.IDBService;
import es.upm.fi.dia.oeg.map4rdf.client.services.IDBServiceAsync;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip
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
        private Label newPasswordLabel;
        private PasswordTextBox newPasswordTextBox ;
        private Label confirmPasswordLabel;
        private PasswordTextBox confirmPasswordTextBox ;
        
        
        private IDBServiceAsync propertiesServiceAsync;
        
        
        
        
	@Inject
	public AdminView(BrowserResources resources) {
               
                propertiesServiceAsync = GWT.create(IDBService.class);
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
                    
                final AsyncCallback<Boolean> loginCallback = new AsyncCallback<Boolean>(){

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Check your database connection");
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        if(result) {
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
                        backToMainPage();
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
                        propertiesServiceAsync.login(loginTextBox.getValue(),loginCallback);
                    }
                });
                
                backButton = new Button("back");
                loginPanel.add(backButton);
                
                backButton.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        backToMainPage();
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
                
                newPasswordLabel = new Label("new password:");
                adminPanel.add(newPasswordLabel);
                newPasswordTextBox = new PasswordTextBox();
                adminPanel.add(newPasswordTextBox);
                confirmPasswordLabel = new Label("confirm password:");
                adminPanel.add(confirmPasswordLabel);
                confirmPasswordTextBox = new PasswordTextBox();
                adminPanel.add(confirmPasswordTextBox);
                
                saveButton = new Button("save");
                
                saveButton.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        
                        if(newPasswordTextBox.getValue().equals(confirmPasswordTextBox.getValue())) { 
                            List<ConfigPropertie> list = new ArrayList<ConfigPropertie>();
                            list.add(new ConfigPropertie(ParameterNames.ENDPOINT_URL,endpointTextBox.getValue()));
                            list.add(new ConfigPropertie(ParameterNames.GOOGLE_MAPS_API_KEY,apiKeyTextBox.getValue()));
                            list.add(new ConfigPropertie(ParameterNames.GEOMETRY_MODEL,geometryTextBox.getValue()));
                            list.add(new ConfigPropertie(ParameterNames.FACETS_AUTO,facetConfTextBox.getValue()));
                            if(!newPasswordTextBox.getValue().equals("")) {
                                 list.add(new ConfigPropertie(ParameterNames.ADMIN,newPasswordTextBox.getValue()));
                            }
                            propertiesServiceAsync.setValues(list, saveCallback);
                        } else {
                            Window.alert("Password does not match the confirm password");
                        }
                    }
                });
                
                adminPanel.add(saveButton);
                
                canelBackButton = new Button("cancel/back");
                adminPanel.add(canelBackButton);
                
                canelBackButton.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        backToMainPage();
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
        
        private void backToMainPage(){
            propertiesServiceAsync.logout(null);
            Window.Location.assign("#" + Places.DASHBOARD);
            Window.Location.reload();
        }

}
