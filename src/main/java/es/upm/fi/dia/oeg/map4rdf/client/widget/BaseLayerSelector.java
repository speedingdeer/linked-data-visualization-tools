/**
 * Copyright (c) 2011 Alexander De Leon Battista
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

import java.util.HashMap;

import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.layer.Layer;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Alexander De Leon
 */
public class BaseLayerSelector extends Composite {

	private final Map map;
	private final java.util.Map<String, SelectableLayer> layers;
	private ListBox list;

	public BaseLayerSelector(Map map) {
		this.map = map;
		layers = new HashMap<String, SelectableLayer>();
		initWidget(createUi());
	}

	public void addLayer(String name, Layer layer) {
		SelectableLayer selectableLayer = new SelectableLayer();
		selectableLayer.layer = layer;
		selectableLayer.index = addItemToList(name, name);
		layers.put(name, selectableLayer);
	}

	public void removeLayer(String name) {
		SelectableLayer selectableLayer = layers.get(name);
		layers.remove(name);
		list.removeItem(selectableLayer.index);
	}

	public boolean setBaseLayer(String selectedLayerName) {
		SelectableLayer selectableLayer = layers.get(selectedLayerName);
		if (selectableLayer == null) {
			return false;
		}
		map.setBaseLayer(selectableLayer.layer);
		return true;
	}

	/* ----------------- helper methods and classes -- */
	private int addItemToList(String name, String value) {
		list.addItem(name, value);
		return list.getItemCount() - 1;
	}

	private Widget createUi() {
		FlowPanel panel = new FlowPanel();
		list = new ListBox();
		list.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String selectedLayerName = list.getValue(list.getSelectedIndex());
				setBaseLayer(selectedLayerName);
			}
		});
		panel.add(list);
		return panel;
	}

	private static class SelectableLayer {
		Layer layer;
		int index;
	}
}
