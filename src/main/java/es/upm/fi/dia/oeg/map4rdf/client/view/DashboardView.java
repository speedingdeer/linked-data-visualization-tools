/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informática, Universidad 
 * Politécnica de Madrid, Spain
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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.presenter.DashboardPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

/**
 * @author Alexander De Leon
 */
public class DashboardView extends ResizeComposite implements DashboardPresenter.Display {

	private final SplitLayoutPanel splitPanel;
	private final LayoutPanel centerPanel;
	private final StackLayoutPanel leftPanel;
	private final LayoutPanel mapPanel;

	@Inject
	public DashboardView(BrowserResources resources) {
		mapPanel = new LayoutPanel();
		splitPanel = new SplitLayoutPanel();
		leftPanel = new StackLayoutPanel(Unit.EM);
		centerPanel = new LayoutPanel();
		initWidget(createUi(resources));
	}

	/* ---------------- Dashboard API -- */
	@Override
	public void addWestWidget(Widget widget, String header) {
		leftPanel.add(widget, header, 3);
	}

	@Override
	public HasWidgets getMapPanel() {
		return mapPanel;
	}

	/* ----------------- Display API -- */
	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void startProcessing() {
		// empty

	}

	@Override
	public void stopProcessing() {
		// empty
	}

	/* ------------------- Helper methods -- */
	private Widget createUi(BrowserResources resources) {
		centerPanel.add(mapPanel);

		centerPanel.setWidgetTopHeight(mapPanel, 0, Unit.EM, 100, Unit.PCT);
		centerPanel.setWidgetLeftWidth(mapPanel, 0, Unit.EM, 100, Unit.PCT);

		splitPanel.addWest(leftPanel, 200);
		splitPanel.add(centerPanel);
		leftPanel.addStyleName(resources.css().leftMenu());
		return splitPanel;
	}
}
