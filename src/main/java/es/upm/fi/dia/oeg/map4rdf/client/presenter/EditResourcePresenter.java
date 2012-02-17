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
import java.util.HashMap;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.Place;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectDescriptions;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectLabel;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.UrlParametersChangeEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.UrlParametersChangeEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.widget.DescriptionTreeItem;

import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;
import es.upm.fi.dia.oeg.map4rdf.share.URLSafety;
import es.upm.fi.dia.oeg.map4rdf.share.conf.UrlParamtersDict;
import name.alexdeleon.lib.gwtblocks.client.PagePresenter;

/**
 * @author Alexander De Leon
 */
@Singleton
public class EditResourcePresenter extends  PagePresenter<EditResourcePresenter.Display> implements UrlParametersChangeEventHandler{

	private HashMap<String, String> parameters;
	private URLSafety subjectUrl;
	private String subjectLabel;
    private ArrayList<DescriptionTreeItem> descriptions = new ArrayList<DescriptionTreeItem>();

    public interface Display extends WidgetDisplay {
        public void clear();
        public void setCore(String core);
        public void addDescription(DescriptionTreeItem description);
        public void addDescription(TreeItem treeItem, DescriptionTreeItem description);
        public Tree getTree();
    }
    
	private final DispatchAsync dispatchAsync;

	@Inject
	public EditResourcePresenter(Display display, EventBus eventBus, final DispatchAsync dispatchAsync) {
		super(display, eventBus);
		this.dispatchAsync = dispatchAsync;
		eventBus.addHandler(UrlParametersChangeEvent.getType(), this);
    }

	/* -------------- Presenter callbacks -- */
	@Override
	protected void onBind() {
	}

	@Override
	protected void onUnbind() {
		// TODO Auto-generated method stub

	}
    
        @Override
    protected void onRefreshDisplay() {
        //throw new UnsupportedOperationException("Not supported yet.");
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
	public void onParametersChange(UrlParametersChangeEvent event) {
		clear();
		parameters = event.getParamaters();
		if (parameters.containsKey(UrlParamtersDict.RESOURCE_EDIT_PARAMTERES)) { 
			//geoResouceUri = URLSafty.encode((parameters.get(UrlParamtersDict.RESOURCE_EDIT_PARAMTERES)));
			subjectUrl = new URLSafety((parameters.get(UrlParamtersDict.RESOURCE_EDIT_PARAMTERES)));
			
			GetSubjectLabel action = new GetSubjectLabel(subjectUrl.getUrlSafty());
			dispatchAsync.execute(action, new AsyncCallback<SingletonResult<String>>(){

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(SingletonResult<String> result) {
					subjectLabel = result.getValue();
					fullfilContent();
				}
				
			});
		}
	}
	
	private void fullfilContent() {
		
		getDisplay().setCore(subjectLabel+" "+ "("+subjectUrl.getUrl()+")");
		
		GetSubjectDescriptions action = new GetSubjectDescriptions(subjectUrl.getUrlSafty());
        dispatchAsync.execute(action, new AsyncCallback<ListResult<SubjectDescription>>() {
        
		@Override
			public void onFailure(Throwable caught) {
				Window.alert("Url parameter is not valid");
			}

			@Override
			public void onSuccess(ListResult<SubjectDescription>result) {
				for(SubjectDescription d : result) {	
					DescriptionTreeItem editableDescription = new DescriptionTreeItem(d,null);
					descriptions.add(editableDescription);
					getDisplay().addDescription(editableDescription);
				}
			}

			
        });
        
        getDisplay().getTree().addOpenHandler(new OpenHandler<TreeItem>() {
			
			@Override
			public void onOpen(final OpenEvent<TreeItem> event) {
				
				//if the node is not opened for the first time, ignore the action				
				if (! isEmpty(getDescription(event.getTarget()))) {
					return;
				}
				
				for(DescriptionTreeItem d : descriptions)
				if(event.getTarget().getWidget() != null && event.getTarget().getWidget().equals(d.getWidget())) {
					GetSubjectDescriptions action = new GetSubjectDescriptions(d.getObjectText());
			        dispatchAsync.execute(action, new AsyncCallback<ListResult<SubjectDescription>>() {
			        
			        	@Override
						public void onFailure(Throwable caught) {
							Window.alert("Url parameter is not valid");
						}

						@Override
						public void onSuccess(ListResult<SubjectDescription>result) {
							for(SubjectDescription d : result) {	
								
								DescriptionTreeItem editableDescription = new DescriptionTreeItem(d,getDescription(event.getTarget()));
								descriptions.add(editableDescription);
								getDisplay().addDescription(event.getTarget(), editableDescription);
							}
						}
			        });
				}
			}
		});
    }
	
    private DescriptionTreeItem getDescription(TreeItem treeItem) {
    
    	for (DescriptionTreeItem d : descriptions) {    		 
    		 
    		if(treeItem.getWidget() != null && treeItem.getWidget().equals(d.getWidget())){
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
    
    private void clear(){
        descriptions.clear();
        getDisplay().clear();
    }

}