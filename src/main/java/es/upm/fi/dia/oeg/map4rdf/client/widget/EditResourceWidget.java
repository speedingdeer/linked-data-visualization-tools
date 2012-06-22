package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;
import java.util.Date;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigurationParameter;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectDescriptions;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectLabel;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SaveRdfFile;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.EditResourceCloseEvent;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter.Display;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

public class EditResourceWidget extends Composite{
	
    private FlowPanel mainPanel;
    private HorizontalPanel container;
    
    private Tree tree;
    private TreeItem root;
    private Integer maxDepth = 3;
    private BrowserResources resources;
    private PushButton saveButton;
    private PushButton backButton;
    private ArrayList<DescriptionTreeItem> descriptions = new ArrayList<DescriptionTreeItem>();
	private String resourceUrl;
	private String subjectLabel;
	private DispatchAsync dispatchAsync;
	private BrowserMessages messages;
	private FlowPanel panel;	
	private Display display;
	private EventBus eventBus;
	
	public EditResourceWidget(String resuorceUrl, DispatchAsync dispatchAsync,Display display, BrowserResources resources, BrowserMessages messages, EventBus eventBus) {
		this.resourceUrl=resuorceUrl;
		this.dispatchAsync=dispatchAsync;
		this.display = display;
		this.resources=resources;
		this.messages=messages;
		this.eventBus=eventBus;
		initWidget(createUi());
		this.setDepth();
		this.setLabel();
	}
	
	private Widget createUi() {
	 	mainPanel = new FlowPanel();    	
    	tree = new Tree();
    	root = new TreeItem("");
    	backButton = new PushButton(new Image(resources.backButton()));
    	saveButton = new PushButton(new Image(resources.saveButton()));
    	backButton.setSize("25px", "25px");
    	saveButton.setSize("25px", "25px");
    	tree.addItem(root);
    	root.setStyleName("treeRoot");
    	container = new HorizontalPanel();
    	VerticalPanel toolbar = new VerticalPanel();
    	toolbar.setSpacing(2);
    	container.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
    	toolbar.add(saveButton);
    	toolbar.add(backButton);
    	tree.addItem(root);
     	root.setStyleName("treeRoot");
     	root.addItem(new TreeItem(""));
     	mainPanel.add(container);
    	container.add(toolbar);
    	container.add(tree);
    	saveButton.addClickHandler(new ClickHandler() {
    		@Override
    		public void onClick(ClickEvent event) {
    			storeData();
    		}
    	});
    	backButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new EditResourceCloseEvent());
			}
		});
        return mainPanel;
	}
	
	
	
	
	
	
	private void setDepth() {
		dispatchAsync.execute(new GetConfigurationParameter(ParameterNames.EDIT_DEPTH), new AsyncCallback<SingletonResult<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				//parameter will stay default 
			}
			@Override
			public void onSuccess(SingletonResult<String> result) {
				maxDepth=new Integer(result.getValue());
			}
		});
	}
	
	private void setLabel() {
		GetSubjectLabel action = new GetSubjectLabel(resourceUrl);
		//collect all information
		display.startProcessing();
		dispatchAsync.execute(action, new AsyncCallback<SingletonResult<String>>(){
			@Override
			public void onFailure(Throwable caught) {
				display.stopProcessing();
			}
			@Override
			public void onSuccess(SingletonResult<String> result) {
				subjectLabel = result.getValue();
				display.stopProcessing();
				root.setText(subjectLabel + "("+ resourceUrl +")");
				fillUpContent();
			}
		});
	}
	
	private void fillUpContent() {
		GetSubjectDescriptions action = new GetSubjectDescriptions(resourceUrl);
		display.startProcessing();
        dispatchAsync.execute(action, new AsyncCallback<ListResult<SubjectDescription>>() {
		@Override
			public void onFailure(Throwable caught) {
				Window.alert(messages.canNotLoaddescription());
				display.stopProcessing();
			}
			@Override
			public void onSuccess(ListResult<SubjectDescription>result) {
				addDescriptions(result, null);
				display.stopProcessing();
			}
			
        });
        tree.addOpenHandler(new OpenHandler<TreeItem>() {
			
			@Override
			public void onOpen(final OpenEvent<TreeItem> event) {
				display.startProcessing();
				//if the node is not opened for the first time, ignore the action				
				if (! isEmpty(getDescription(event.getTarget())) || event.getTarget().equals(tree.getItem(0)) ) {
					display.stopProcessing();
					return;
				}
				
				for(DescriptionTreeItem d : descriptions)
				if(event.getTarget().getWidget() != null && event.getTarget().getWidget().equals(d.getWidget())) {
					GetSubjectDescriptions action = new GetSubjectDescriptions(d.getObjectText());
			        dispatchAsync.execute(action, new AsyncCallback<ListResult<SubjectDescription>>() {
			        
			        	@Override
						public void onFailure(Throwable caught) {
							Window.alert(messages.canNotLoaddescription());
							display.stopProcessing();
						}

						@Override
						public void onSuccess(ListResult<SubjectDescription>result) {
							addDescriptions(result,event.getTarget());
							display.stopProcessing();
						}
			        });
				}
			}
		});
	}
	
    private void addDescriptions(ListResult<SubjectDescription>result, TreeItem target) {
		for(SubjectDescription d : result) {	
			DescriptionTreeItem editableDescription = new DescriptionTreeItem(d,getDescription(target));
			descriptions.add(editableDescription);
			if (target == null) {
				target = root;
			}
			TreeItem leaf = new TreeItem(editableDescription.getWidget());
			if (editableDescription.getSubjectDescriptions().getObject().isResource() && editableDescription.getDepth() < maxDepth) {
				leaf.addItem("");
			}
			target.addItem(leaf);
		}
    }
    
    private DescriptionTreeItem getDescription(TreeItem treeItem) {
    	for (DescriptionTreeItem d : descriptions) {    		 
    		if(treeItem != null && treeItem.getWidget() != null && treeItem.getWidget().equals(d.getWidget())){
    			return d;
    		}
    	}
    	return null;
    }
    private Boolean isEmpty(DescriptionTreeItem parent) {
    	for (DescriptionTreeItem d: descriptions) {
    		if (d.getParent()!= null && d.getParent().equals(parent)) {
    			return false;
    		}
    	}
    	return true;
    }

    private void storeData(){
    	String fileName = resourceUrl.replaceAll("/","|");
    	Date rightNow =  new Date();
    	fileName+="--" + rightNow.toString().replaceAll(" ","_");
    	String fileContent = "";
    	for (DescriptionTreeItem d : descriptions) {
    		//if parent is a root of tree (subject)
    		if(d.getParent() == null) {
    			fileContent+="<"+resourceUrl+">";
    		}
    		else {
    			fileContent+="<" +d.getParent().getObjectText()+">";
    		}
    		fileContent+=" " + "<"+d.getPredicateText()+">" + " ";
			fileContent+="<"+d.getObjectText()+">" + " ";
			fileContent+=".\n";
    	}
    	
    	dispatchAsync.execute(new SaveRdfFile(fileName, fileContent), new AsyncCallback<SingletonResult<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Unexcepted error occured");
			}

			@Override
			public void onSuccess(SingletonResult<String> result) {
				Window.alert("Your changes were saved");
			}
		});
    }
	
	
}
