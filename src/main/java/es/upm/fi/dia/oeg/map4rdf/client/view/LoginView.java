/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.LoginPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

/**
 *
 * @author filip
 */
public class LoginView extends Composite implements LoginPresenter.Display {
    
    private FlowPanel mainPanel;
    private Button loginButton;
    private Label loginLabel;
    private PasswordTextBox loginTextBox;
    
    @Inject
    public LoginView(BrowserResources resources) {
        
        initWidget(createUi());
        loginButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                
            }
        });
    }
    
    @Override
    public void clear() {
        loginTextBox.setText("");
    }
    
    @Override
    public Widget asWidget() {
        return this;
    }
    
    @Override
    public void startProcessing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void stopProcessing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private Widget createUi() {
        mainPanel = new FlowPanel();
        loginLabel = new Label("password:");
        mainPanel.add(loginLabel);
        
        loginTextBox = new PasswordTextBox();
        mainPanel.add(loginTextBox);
        
        loginButton = new Button("login");
        mainPanel.add(loginButton);
        
        return mainPanel;
    }

    @Override
    public Button getLoginActionButton() {
        return loginButton;
    }

    @Override
    public String getPassword() {
        return loginTextBox.getText();
    }

}
