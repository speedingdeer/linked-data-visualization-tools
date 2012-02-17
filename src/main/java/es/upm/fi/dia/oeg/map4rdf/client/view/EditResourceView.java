/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.client.view;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.EditResourcePresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.widget.DescriptionTreeItem;

/**
 *
 * @author filip
 */
public class EditResourceView extends Composite implements EditResourcePresenter.Display {
    
    private ScrollPanel mainPanel;
    private Tree tree;
    private TreeItem root;
    //@TODO refactor as a db parameter
    private Integer maxDepth = 3; 
    
    @Inject
    public EditResourceView(BrowserResources resources) {
        
        initWidget(createUi());
    }
    
    @Override
    public void clear() {
    	mainPanel.remove(tree);
    	tree = new Tree();
    	root = new TreeItem("");
    	root.setSelected(false);
    	tree.addItem(root);
    	mainPanel.add(tree);
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
    	mainPanel = new ScrollPanel();
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
	public void addDescription(DescriptionTreeItem description) {
		TreeItem treeItem = new TreeItem(description.getWidget());
		if (description.getSubjectDescriptions().getObject().isResource() && description.getDepth() < maxDepth) {
			treeItem.addItem("");
		}
		root.addItem(treeItem);
	}

	@Override
	public Tree getTree() {
		return tree;
	}

	@Override
	public void addDescription(TreeItem treeItem, DescriptionTreeItem description ) {
		TreeItem leaf = new TreeItem(description.getWidget());
		if (description.getSubjectDescriptions().getObject().isResource() && description.getDepth() < maxDepth) {
			leaf.addItem("");
		}
		treeItem.addItem(leaf);
	}
}
