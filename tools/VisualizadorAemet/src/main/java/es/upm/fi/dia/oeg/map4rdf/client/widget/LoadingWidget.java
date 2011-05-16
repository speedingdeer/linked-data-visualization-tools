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

import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

/**
 * @author Alexander De Leon
 */
@Singleton
public class LoadingWidget extends PopupPanel {

	private final BrowserMessages messages;

	@Inject
	public LoadingWidget(BrowserResources resources, BrowserMessages messages) {
		this.messages = messages;
		setWidget(createUi(resources.loadingIcon()));
		addStyleName(resources.css().loadingWidget());
	}

	private Widget createUi(ImageResource loadingIconResource) {
		Image loadingIcon = new Image(loadingIconResource);
		Label label = new Label(messages.loading());
		LayoutPanel panel = new LayoutPanel();
		panel.add(loadingIcon);
		panel.add(label);
		panel.setWidgetHorizontalPosition(loadingIcon, Alignment.BEGIN);
		panel.setWidgetHorizontalPosition(label, Alignment.END);

		panel.setWidth("160px");
		panel.setHeight("32px");
		return panel;
	}

}
