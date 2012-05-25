package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

public class PaginationPanel extends  Composite{
	
	private FlowPanel panel;
	
	private HorizontalPanel widgetsPanel;
	private FlowPanel toolbarPanel;
	
	private VerticalPanel verticalPanel;
	private ArrayList<Widget> widgets;
	
	private InlineLabel counterLabel;

	Anchor prev;
    Anchor next;
	private Integer currentWidgetIndex;

	public PaginationPanel(BrowserResources resources){
		verticalPanel = new VerticalPanel();
		panel = new FlowPanel();
		panel.add(verticalPanel);
		widgetsPanel = new HorizontalPanel();
		toolbarPanel = new FlowPanel();
		verticalPanel.add(widgetsPanel);
		verticalPanel.add(toolbarPanel);
		currentWidgetIndex = 0;
		
		counterLabel = new InlineLabel();
		prev = new Anchor("anterior");
        next = new Anchor("siguiente");
        FlowPanel toolbarContainer = new FlowPanel();
        toolbarPanel.add(prev);
		toolbarPanel.add(new InlineLabel(" "));
		toolbarPanel.add(next);
		toolbarPanel.add(new InlineLabel(" "));
		toolbarPanel.add(counterLabel);
		prev.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			prev();	
			}
		});

		next.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			next();	
			}
		});
		widgets = new ArrayList<Widget>();
		toolbarPanel.setVisible(false);
	}

	public void clear() {
		widgets.clear();
		widgetsPanel.clear();
		currentWidgetIndex = 0;
	}
	

	public void add(Widget w) {
		if (widgets != null) {
			if (widgets.size()==0) {
				widgetsPanel.add(w);
			}	
		}
		else {
			widgets = new ArrayList<Widget>();
		}
		currentWidgetIndex=0;
		widgets.add(w);
		refreshLabel();
		if (widgets.size()>1) {
			toolbarPanel.setVisible(true);
		}
		
	}
	
	private void refreshLabel(){
		Integer first = currentWidgetIndex + 1;
		Integer secund = widgets.size();
		if (counterLabel != null) {
			counterLabel.setText(first+"/"+secund);
		} else {
			counterLabel = new InlineLabel(+first+"/"+secund);
		}
	}
	
	private void prev(){
		if (currentWidgetIndex == 0)
			return;
		currentWidgetIndex-=1;
		refreshLabel();
		widgetsPanel.clear();
		widgetsPanel.add(widgets.get(currentWidgetIndex));
	}
	
	private void next(){
		if (currentWidgetIndex == widgets.size()-1)
			return;
		currentWidgetIndex+=1;
		refreshLabel();
		widgetsPanel.clear();
		widgetsPanel.add(widgets.get(currentWidgetIndex));
	}
	
	public FlowPanel getMainPanel(){
		return panel;
	}
	
	public Integer getWidgetCount() {
		return widgets.size();
	}
}
