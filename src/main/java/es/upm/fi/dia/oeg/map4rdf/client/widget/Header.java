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
package es.upm.fi.dia.oeg.map4rdf.client.widget;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

/**
 * @author Alexander De Leon
 */
public class Header extends Composite {

	public Header(BrowserResources resources) {
		initWidget(createUi(resources));
		addStyleName(resources.css().header());
	}

	private Widget createUi(BrowserResources resources) {
		LayoutPanel panel = new LayoutPanel();
		Image logo = new Image(resources.logo());
		Image betaBadge = new Image(resources.betaBadge());

		// Add the logo to the root panel
		RootPanel.get().add(logo);
		RootPanel.get().setWidgetPosition(logo, 4, 0);
		DOM.setStyleAttribute(logo.getElement(), "zIndex", "3");

		panel.add(betaBadge);
		panel.setWidgetRightWidth(betaBadge, 2, Unit.EM, 48, Unit.PX);
		panel.setWidgetTopHeight(betaBadge, 2, Unit.EM, 48, Unit.PX);

		return panel;
	}

}
