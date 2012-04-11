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

import name.alexdeleon.lib.gwtblocks.client.event.ToggleEvent;
import name.alexdeleon.lib.gwtblocks.client.event.ToggleHandler;
import name.alexdeleon.lib.gwtblocks.client.widget.togglebutton.ToggleButton;
import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetStatisticDatasets;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol.GeoResourcesMapControl;
import es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol.StatisticsMapControl;
import es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol.TimeLineFilterControl;
import es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol.TimelineControl;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.Year;
import java.util.ArrayList;

/**
 * @author Alexander De Leon
 * Clase readaptada por Daniel Garijo para que se ajuste a las necesidades de
 * WebNMasUno
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
	private final StatisticsMapControl mapControl;
	private final DispatchAsync dispatchAsync;
	private final BrowserMessages messages;
        private final TimeLineFilterControl timelineControl;

	@Inject
	public DataToolBar(BrowserResources resources,TimeLineFilterControl timeLine, StatisticsMapControl mapControl, DispatchAsync dispatchAsync, BrowserMessages messages) {
		this.mapControl = mapControl;
		this.dispatchAsync = dispatchAsync;
		this.messages = messages;
		stylesheet = resources.css();
                timelineControl = timeLine;

		initWidget(createUi(resources));
	}

	private void showSelectionDialog() {
           
		dispatchAsync.execute(new GetStatisticDatasets(), new AsyncCallback<ListResult<Resource>>() {

			@Override
			public void onSuccess(ListResult<Resource> result) {
				StatisticsSelectionDialog selectionDialog = new StatisticsSelectionDialog(result.asList(), messages);
				selectionDialog.addSelectionHandler(new SelectionHandler<StatisticDefinition>() {
					@Override
					public void onSelection(SelectionEvent<StatisticDefinition> event) {
						StatisticDefinition stat = event.getSelectedItem();
						mapControl.setStatistics(stat);
					}
				});
				selectionDialog.center();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error loading statistics datasets");

			}
		});
	}

	private Widget createUi(BrowserResources resources) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(stylesheet.toolbar());

//		panel.add(new DataToolBarButton(resources.statsButton(), this.messages.statistics(), resources.css(), new ToggleHandler() {
//			@Override
//			public void onToggle(ToggleEvent event) {
//				if (event.isPressed()) {
//
//					showSelectionDialog();
//				} else {
//					mapControl.disable();
//				}
//			}
//		}));
//                panel.add(new DataToolBarButton(resources.viajeroFiltroButton(), "Filtrar por fecha", resources.css(), new ToggleHandler() {
//			@Override
//			public void onToggle(ToggleEvent event) {
//                            //Window.alert("SE METE");
//				if (event.isPressed()) {
//                                    try{
//					timelineControl.enable();
//                                    }catch(Exception e){
//                                        //CHAPUZA. ASK ALEX.
//                                        timelineControl.enable();
//                                    }
//				} else {
//					timelineControl.disable();
//				}
//
//			}
//		}));
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
