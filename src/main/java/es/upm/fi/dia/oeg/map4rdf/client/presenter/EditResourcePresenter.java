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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceChangedEvent;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.place.PlaceRequestEvent;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigurationParameter;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectDescriptions;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectLabel;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SaveRdfFile;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.UrlParametersChangeEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.UrlParametersChangeEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.widget.DescriptionTreeItem;

import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;
import es.upm.fi.dia.oeg.map4rdf.share.URLSafety;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.share.conf.UrlParamtersDict;
import name.alexdeleon.lib.gwtblocks.client.PagePresenter;

/**
 * @author Filip
 */
@Singleton
public class EditResourcePresenter extends  PagePresenter<EditResourcePresenter.Display> implements UrlParametersChangeEventHandler{

	private HashMap<String, String> parameters;
	private URLSafety subjectUrl;
	private String subjectLabel;
    private ArrayList<DescriptionTreeItem> descriptions = new ArrayList<DescriptionTreeItem>();
    private BrowserMessages browserMessages;
    
    public interface Display extends WidgetDisplay {
        public void clear();
        public void addDescription(DescriptionTreeItem description);
        public void addDescription(DescriptionTreeItem description, TreeItem treeItem);
        public Tree getTree();
        public PushButton getBackButton();
        public PushButton getSaveButon();
        public void setCore(String core);
        public void setDepth(Integer depth);
    }
    
	private final DispatchAsync dispatchAsync;

	@Inject
	public EditResourcePresenter(Display display, final EventBus eventBus, final DispatchAsync dispatchAsync, final BrowserMessages browserMessages) {
		
		super(display, eventBus);
		this.dispatchAsync = dispatchAsync;
		this.browserMessages = browserMessages;
		
		eventBus.addHandler(UrlParametersChangeEvent.getType(), this);
		//set max  depth parameter
		dispatchAsync.execute(new GetConfigurationParameter(ParameterNames.EDIT_DEPTH), new AsyncCallback<SingletonResult<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				//parameter will stay default 
			}
			@Override
			public void onSuccess(SingletonResult<String> result) {
				getDisplay().setDepth(new Integer(result.getValue()));
			}
		});
    }

	/* -------------- Presenter callbacks -- */
	@Override
	protected void onBind() {
		getDisplay().getBackButton().addClickHandler(new ClickHandler() {
			//bind buttons
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new PlaceChangedEvent(Places.DEFAULT.request()));
				eventBus.fireEvent(new PlaceRequestEvent(new PlaceRequest(Places.DEFAULT)));
			}
		});
		getDisplay().getSaveButon().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				storeData();
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
    	clear();
    }

    @Override
    public Place getPlace() {
         return Places.EDIT_RESOURCE;
    }

    @Override
    protected void onPlaceRequest(PlaceRequest request) {
    }

    
	@Override 
	//set all parameters, collect all data
	public void onParametersChange(UrlParametersChangeEvent event) {
		clear();
		parameters = event.getParamaters();
		if (parameters.containsKey(UrlParamtersDict.RESOURCE_EDIT_PARAMTERES)) { 
			getDisplay().startProcessing();
			subjectUrl = new URLSafety((parameters.get(UrlParamtersDict.RESOURCE_EDIT_PARAMTERES)));
			GetSubjectLabel action = new GetSubjectLabel(subjectUrl.getUrlSafty());
			//collect all information
			dispatchAsync.execute(action, new AsyncCallback<SingletonResult<String>>(){
				@Override
				public void onFailure(Throwable caught) {
					getDisplay().stopProcessing();
				}
				@Override
				public void onSuccess(SingletonResult<String> result) {
					subjectLabel = result.getValue();
					fillUpContent();
				}
			});
		}
		
	}
	
	private void fillUpContent() {
		
		getDisplay().setCore(subjectLabel+" "+ "("+subjectUrl.getUrl()+")");
		
		GetSubjectDescriptions action = new GetSubjectDescriptions(subjectUrl.getUrlSafty());
        dispatchAsync.execute(action, new AsyncCallback<ListResult<SubjectDescription>>() {
		@Override
			public void onFailure(Throwable caught) {
				Window.alert(browserMessages.canNotLoaddescription());
				getDisplay().stopProcessing();
			}
			@Override
			public void onSuccess(ListResult<SubjectDescription>result) {
				addDescriptions(result, null);
				getDisplay().stopProcessing();
			}
			
        });
        addOpenHandler();
        
    }
    
    private void clear(){
        descriptions.clear();
        getDisplay().clear();
    }
    
    private void addOpenHandler() {
        getDisplay().getTree().addOpenHandler(new OpenHandler<TreeItem>() {
			
			@Override
			public void onOpen(final OpenEvent<TreeItem> event) {
				getDisplay().startProcessing();
				//if the node is not opened for the first time, ignore the action				
				if (! isEmpty(getDescription(event.getTarget())) || event.getTarget().equals(getDisplay().getTree().getItem(0)) ) {
					getDisplay().stopProcessing();
					return;
				}
				
				for(DescriptionTreeItem d : descriptions)
				if(event.getTarget().getWidget() != null && event.getTarget().getWidget().equals(d.getWidget())) {
					GetSubjectDescriptions action = new GetSubjectDescriptions(d.getObjectText());
			        dispatchAsync.execute(action, new AsyncCallback<ListResult<SubjectDescription>>() {
			        
			        	@Override
						public void onFailure(Throwable caught) {
							Window.alert(browserMessages.canNotLoaddescription());
							getDisplay().stopProcessing();
						}

						@Override
						public void onSuccess(ListResult<SubjectDescription>result) {
							addDescriptions(result,event.getTarget());
							getDisplay().stopProcessing();
						}
			        });
				}
			}
		});
    }
    
    private void storeData(){
    	
    	String fileName = subjectUrl.getUrl().replaceAll("/","|");
    	Date rightNow =  new Date();
    	fileName+="--" + rightNow.toString().replaceAll(" ","_");
    	String fileContent = "";
    	for (DescriptionTreeItem d : descriptions) {
    		//if parent is a root of tree (subject)
    		if(d.getParent() == null) {
    			fileContent+="<"+subjectUrl.getUrl()+">";
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
    private void addDescriptions(ListResult<SubjectDescription>result, TreeItem target) {
		for(SubjectDescription d : result) {	
			DescriptionTreeItem editableDescription = new DescriptionTreeItem(d,getDescription(target));
			descriptions.add(editableDescription);
			getDisplay().addDescription(editableDescription,target);
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
}