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
package es.upm.fi.dia.oeg.map4rdf.client.widget;

import name.alexdeleon.lib.gwtblocks.client.event.ToggleEvent;
import name.alexdeleon.lib.gwtblocks.client.event.ToggleHandler;
import name.alexdeleon.lib.gwtblocks.client.widget.togglebutton.ToggleButton;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.event.MapletActivatedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.MapletDeactivatedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.maplet.stats.StatisticsMaplet;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

/**
 * @author Alexander De Leon
 */
public class DataToolBar extends Composite {

	/**
	 * Stylesheet contract
	 */
	public static interface Stylesheet {
		String toolbar();

	}

	private Image statsButton;
	private final Stylesheet stylesheet;
	private final MapPresenter.Display mapView;
	private final DispatchAsync dispatchAsync;
	private final BrowserMessages messages;
	private final EventBus eventBus;

	@Inject
	public DataToolBar(BrowserResources resources, MapPresenter.Display mapView, DispatchAsync dispatchAsync,
			BrowserMessages messages, EventBus eventBus) {
		this.eventBus = eventBus;
		this.mapView = mapView;
		this.dispatchAsync = dispatchAsync;
		this.messages = messages;
		stylesheet = resources.css();
		initWidget(createUi(resources));

	}

	private void showSelectionDialog() {

	}

	private Widget createUi(BrowserResources resources) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(stylesheet.toolbar());

		panel.add(new DataToolBarButton(resources.statsButton(), messages.statistics(), resources.css(),
				new ToggleHandler() {
					@Override
					public void onToggle(ToggleEvent event) {
						if (event.isPressed()) {
							eventBus.fireEvent(new MapletActivatedEvent(StatisticsMaplet.getMapletId()));
						} else {
							eventBus.fireEvent(new MapletDeactivatedEvent(StatisticsMaplet.getMapletId()));
						}
					}
				}));

		return panel;
	}

	class DataToolBarButton extends ToggleButton {

		public DataToolBarButton(ImageResource imageResource, String name, Stylesheet style, ToggleHandler handler) {
			this(imageResource, name, style);
			addToggleHandler(handler);
		}

		public DataToolBarButton(ImageResource imageResource, String name, Stylesheet style) {
			super(style);
			FlowPanel button = new FlowPanel();
			Image icon = new Image(imageResource);
			button.add(icon);
			button.add(new Label(name));
			setWidget(button);
			DOM.setStyleAttribute(getElement(), "float", "left");
		}

	}

}
