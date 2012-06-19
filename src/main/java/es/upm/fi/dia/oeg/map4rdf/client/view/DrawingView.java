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


import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.drawing.DrawingType;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.DrawingPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

/**
 * @author Filip
 */
public class DrawingView extends Composite implements DrawingPresenter.Display {

	private final BrowserMessages messages;
	private final BrowserResources resources;
	
	private FlowPanel panel;
	private FlowPanel toolbarPanel;
	private PushButton clearButton;
	private ToggleButton drawButton;
	private RadioButton lineButton;
	private RadioButton polygonButton;
	private RadioButton pointButton;
	
	@Inject
	public DrawingView(BrowserMessages messages, BrowserResources resources) {
		this.resources = resources;
		this.messages = messages;
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
		panel = new FlowPanel();
		Grid grid = new Grid(1, 3);
		toolbarPanel = new FlowPanel();
		drawButton = new ToggleButton(new Image(resources.pencilIcon()));
		drawButton.setSize("20px","20px");
		clearButton = new PushButton(new Image(resources.eraserIcon()));
		clearButton.setSize("20px","20px");
		
		grid.setWidget(0, 1, drawButton);
		grid.setWidget(0, 2, clearButton);
		grid.setWidget(0, 0, new Label(messages.draw()+": "));
		
		pointButton = new RadioButton("polygon-type",messages.point());
		lineButton = new RadioButton("polygon-type",messages.line());
		polygonButton = new RadioButton("polygon-type",messages.polygon());
		
		panel.add(grid);
		toolbarPanel.add(pointButton);
		toolbarPanel.add(lineButton);
		toolbarPanel.add(polygonButton);
		panel.add(toolbarPanel);
		toolbarPanel.setVisible(false);
		return panel;
	}

    @Override
    public void clear() {
        panel.clear();
    }


	@Override
	public ToggleButton getDrawButton() {
		return this.drawButton;
	}


	@Override
	public PushButton getClearButton() {
		return this.clearButton;
	}


	@Override
	public void setToolbarVisibility(Boolean visible) {
		toolbarPanel.setVisible(visible);
		pointButton.setChecked(false);
		polygonButton.setChecked(false);
		lineButton.setChecked(false);
		
	}


	@Override
	public RadioButton getPointButton() {
		return pointButton;
	}


	@Override
	public RadioButton getPolygonButton() {
		return polygonButton;
	}


	@Override
	public RadioButton getLineButton() {
		return lineButton;
	}

}
