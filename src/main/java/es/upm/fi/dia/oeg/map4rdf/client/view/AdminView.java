/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informetica, Universidad 
 * Politecnica de Madrid, Spain
 */
package es.upm.fi.dia.oeg.map4rdf.client.view;

import com.google.gwt.user.client.ui.Button;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.presenter.AdminPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip
 */
public class AdminView extends Composite implements AdminPresenter.Display {

    private FlowPanel mainPanel;
    private PasswordTextBox loginTextBox;
    private Button saveButton;
    private Button logoutButton;
    private Label enpointLabel;
    private TextBox endpointTextBox;
    private Label geometryLabel;
    private TextBox geometryTextBox;
    private Label apikKeyLabel;
    private TextBox apiKeyTextBox;
    private Label facetConfLabel;
    private TextBox facetConfTextBox;
    private Label newPasswordLabel;
    private PasswordTextBox newPasswordTextBox;
    private Label confirmPasswordLabel;
    private PasswordTextBox confirmPasswordTextBox;
    private Label editDepthLabel;
    private TextBox editDepthTextBox;
    private Label rdfPathLabel;
    private TextBox rdfPathTextBox;
    private Label sphericalMercatorLabel;
    private TextBox sphericalMercatorTextBox;
    
    @Inject
    public AdminView(BrowserResources resources) {

        initWidget(createUi());
        buildWidgets();
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
        return mainPanel;
    }

    private void buildWidgets() {

        enpointLabel = new Label("endpoint_url:");
        mainPanel.add(enpointLabel);
        endpointTextBox = new TextBox();
        endpointTextBox.setWidth("250px");
        mainPanel.add(endpointTextBox);
        
        
        geometryLabel = new Label("geometry:");
        mainPanel.add(geometryLabel);
        geometryTextBox = new TextBox();
        geometryTextBox.setWidth("250px");
        mainPanel.add(geometryTextBox);

        apikKeyLabel = new Label("google api key:");
        mainPanel.add(apikKeyLabel);
        apiKeyTextBox = new TextBox();
        apiKeyTextBox.setWidth("250px");
        mainPanel.add(apiKeyTextBox);

        facetConfLabel = new Label("facet automatic (true/false):");
        mainPanel.add(facetConfLabel);
        facetConfTextBox = new TextBox();
        facetConfTextBox.setWidth("250px");
        mainPanel.add(facetConfTextBox);

        editDepthLabel = new Label("edit tree depth (integer):");
        mainPanel.add(editDepthLabel);
        editDepthTextBox = new TextBox();
        editDepthTextBox.setWidth("250px");
        mainPanel.add(editDepthTextBox);
        
        rdfPathLabel = new Label("rdf storing path:");
        mainPanel.add(rdfPathLabel);
        rdfPathTextBox = new TextBox();
        rdfPathTextBox.setWidth("250px");
        mainPanel.add(rdfPathTextBox);

        sphericalMercatorLabel = new Label("spherical_mercator (true/false):");
        mainPanel.add(sphericalMercatorLabel);
        sphericalMercatorTextBox = new TextBox();
        sphericalMercatorTextBox.setWidth("250px");
        mainPanel.add(sphericalMercatorTextBox);
        
        newPasswordLabel = new Label("new password:");
        mainPanel.add(newPasswordLabel);
        newPasswordTextBox = new PasswordTextBox();
        newPasswordTextBox.setWidth("250px");
        mainPanel.add(newPasswordTextBox);
        confirmPasswordLabel = new Label("confirm password:");
        mainPanel.add(confirmPasswordLabel);
        confirmPasswordTextBox = new PasswordTextBox();
        confirmPasswordTextBox.setWidth("250px");
        mainPanel.add(confirmPasswordTextBox);

        mainPanel.add(new Label(""));
        
        saveButton = new Button("save");
        //mainPanel.add(saveButton);

        logoutButton = new Button("logout");
        //mainPanel.add(logoutButton);
        
        Grid grid = new Grid(1, 2);
        grid.setWidget(0,0,saveButton);
        grid.setWidget(0,1,logoutButton);
        grid.setCellSpacing(3);
        mainPanel.add(grid);
        mainPanel.setVisible(true);
    }

    @Override
    public void clear() {
        this.newPasswordTextBox.setText("");
        this.confirmPasswordTextBox.setText("");
    }

    @Override
    public Button getLogoutButton() {
        return logoutButton;
    }

    @Override
    public void setVisibility(Boolean visibility) {
        mainPanel.setVisible(visibility);
    }

    @Override
    public void fullfilForm(List<ConfigPropertie> result) {
        for(ConfigPropertie prop : result) {
            if(prop.getKey().equals(ParameterNames.ENDPOINT_URL)) {
                endpointTextBox.setText(prop.getValue());
            } else if (prop.getKey().equals(ParameterNames.FACETS_AUTO)) {
                facetConfTextBox.setText(prop.getValue());
            } else if (prop.getKey().equals(ParameterNames.GEOMETRY_MODEL)) {
                geometryTextBox.setText(prop.getValue());
            } else if (prop.getKey().equals(ParameterNames.GOOGLE_MAPS_API_KEY)) {
                apiKeyTextBox.setText(prop.getValue());
            } else if (prop.getKey().equals(ParameterNames.EDIT_DEPTH)) {
                editDepthTextBox.setText(prop.getValue());
            } else if (prop.getKey().equals(ParameterNames.RDF_STORE_PATH)) {
            	rdfPathTextBox.setText(prop.getValue());
            } else if (prop.getKey().equals(ParameterNames.SPHERICAL_MERCATOR)) {
            	sphericalMercatorTextBox.setText(prop.getValue());
            }
        }
        clear();
    }

    @Override
    public Button getSaveButton() {
        return saveButton;
    }

    @Override
    public Boolean checkAdminPassword() {
        return newPasswordTextBox.getText().equals(confirmPasswordTextBox.getText());
    }

    @Override
    public List<ConfigPropertie> getPropertieMap() {
        ArrayList<ConfigPropertie> resultList = new ArrayList<ConfigPropertie>();
        if(isAdminPassordIntroduced()) {
            resultList.add(new ConfigPropertie(ParameterNames.ADMIN, newPasswordTextBox.getText()));
        }
        resultList.add(new ConfigPropertie(ParameterNames.ENDPOINT_URL,endpointTextBox.getText()));
        resultList.add(new ConfigPropertie(ParameterNames.FACETS_AUTO, facetConfTextBox.getText()));
        resultList.add(new ConfigPropertie(ParameterNames.GEOMETRY_MODEL, geometryTextBox.getText()));
        resultList.add(new ConfigPropertie(ParameterNames.GOOGLE_MAPS_API_KEY, apiKeyTextBox.getText()));
        resultList.add(new ConfigPropertie(ParameterNames.EDIT_DEPTH, editDepthTextBox.getText()));
        resultList.add(new ConfigPropertie(ParameterNames.RDF_STORE_PATH, rdfPathTextBox.getText()));
        resultList.add(new ConfigPropertie(ParameterNames.SPHERICAL_MERCATOR, sphericalMercatorTextBox.getText()));
        return  resultList;
    }

    public Boolean isAdminPassordIntroduced() {
        return (! (newPasswordTextBox.getText().equals("") && confirmPasswordTextBox.getText().equals("") ));
    }


}
