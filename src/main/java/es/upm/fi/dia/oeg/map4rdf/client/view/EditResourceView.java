/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.client.view;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.EditResourcePresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;

/**
 *
 * @author filip
 */
public class EditResourceView extends Composite implements EditResourcePresenter.Display {
    
    private FlowPanel mainPanel;
    private Tree tree;
    private TreeItem root;
    @Inject
    public EditResourceView(BrowserResources resources) {
        
        initWidget(createUi());
    }
    
    @Override
    public void clear() {
        
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
    	tree = new Tree();
    	root = new TreeItem("");
    	
    	tree.addItem(root);
        mainPanel.add(tree);
        return mainPanel;
    }

	@Override
	public void setCore(String core) {
		root.setText(core);
	}

	@Override
	public void addDescription(SubjectDescription description) {
		Window.alert(description.getObject());
	}
}
