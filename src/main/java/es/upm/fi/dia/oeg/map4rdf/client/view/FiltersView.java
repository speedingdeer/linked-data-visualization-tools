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


import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.presenter.FiltersPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.widget.YearsSelector;

/**
 * @author Alexander De Leon
 */
public class FiltersView extends Composite implements FiltersPresenter.Display {

	private final BrowserMessages messages;
	private final BrowserResources resources;
	private final EventBus eventBus;
	
	private FlowPanel mainPanel;
	private FlowPanel drawingPanel;
	private FlowPanel yearsPanel;
	private PushButton clearButton;
	private ToggleButton drawButton;
	
	@Inject
	public FiltersView(BrowserMessages messages, BrowserResources resources, EventBus eventBus) {
		this.resources = resources;
		this.messages = messages;
		this.eventBus = eventBus;
		initWidget(createUi());
		//addStyleName(resources.css().facets());
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
		drawingPanel = new FlowPanel();
		Grid grid = new Grid(1, 3);

		drawButton = new ToggleButton(new Image(resources.pencilIcon()));
		drawButton.setSize("20px","20px");
		clearButton = new PushButton(new Image(resources.eraserIcon()));
		clearButton.setSize("20px","20px");
		
		grid.setWidget(0, 1, drawButton);
		grid.setWidget(0, 2, clearButton);
		grid.setWidget(0, 0, new Label(messages.draw()+": "));
		
		drawingPanel.add(grid);
		
		mainPanel = new FlowPanel();
		
		mainPanel.add(drawingPanel);
		mainPanel.add(new Label());
		
		Grid gridDate = new Grid(1, 3);
		
		yearsPanel = new FlowPanel();
		yearsPanel.add(gridDate);
		gridDate.setWidget(0,0,new Label("Filtrar por fecha: "));
		gridDate.setWidget(0, 2,new YearsSelector(eventBus));
		mainPanel.add(yearsPanel);
		return mainPanel;
	}

    @Override
    public void clear() {
        drawingPanel.clear();
    }


	@Override
	public ToggleButton getDrawButton() {
		return this.drawButton;
	}


	@Override
	public PushButton getClearButton() {
		return this.clearButton;
	}



}
