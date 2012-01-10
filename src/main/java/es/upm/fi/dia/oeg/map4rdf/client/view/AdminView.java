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

import com.anotherbigidea.flash.movie.Text;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Guice;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.presenter.AdminPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.FacetPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.services.IPropertiesService;
import es.upm.fi.dia.oeg.map4rdf.client.services.IPropertiesServiceAsync;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.widget.FacetWidget;
import es.upm.fi.dia.oeg.map4rdf.client.widget.event.FacetValueSelectionChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.widget.event.FacetValueSelectionChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.server.db.DbConfig;
import es.upm.fi.dia.oeg.map4rdf.server.db.SQLconnector;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetGroup;

/**
 * @author Alexander De Leon
 */
public class AdminView extends Composite implements AdminPresenter.Display {

	private FlowPanel panel;
        private Button loginButton;
        private Label loginLabel;
        private PasswordTextBox loginTextBox;
        private Label informationLoginLabel;
        private IPropertiesServiceAsync propertiesServiceAsync;
        
	@Inject
	public AdminView(BrowserResources resources) {
               
                propertiesServiceAsync = GWT.create(IPropertiesService.class);
                AsyncCallback<String> callback = new AsyncCallback<String>(){

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Something is wrong :(");
                    }

                    @Override
                    public void onSuccess(String result) {
                        Window.alert(result);
                    }
                };
                
                propertiesServiceAsync.getValue("klucz",callback);
                       
                initWidget(createUi());
                
                loginLabel = new Label("password:");
                panel.add(loginLabel);
                
                loginTextBox = new PasswordTextBox();
                panel.add(loginTextBox);
                
                informationLoginLabel = new Label("");
                
                loginButton = new Button("login");
                panel.add(loginButton);
                    
                
                loginButton.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                      if(true) {//  if (loginTextBox.getText().equals(dbConnector.decryptString(dbConnector.getProperties("admin")))) {
                            Window.alert("ok");
                        } else {
                            Window.alert(("fail"));
                        }
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
		panel = new FlowPanel();
		return panel;
	}

}
