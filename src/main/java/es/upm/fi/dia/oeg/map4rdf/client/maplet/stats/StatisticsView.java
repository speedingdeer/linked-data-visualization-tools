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
package es.upm.fi.dia.oeg.map4rdf.client.maplet.stats;

import java.util.List;

import name.alexdeleon.lib.gwtblocks.client.widget.loading.LoadingWidget;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.view.v2.MapLayer;
import es.upm.fi.dia.oeg.map4rdf.client.widget.Timeline;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.Year;

/**
 * @author Alexander De Leon
 */
public class StatisticsView extends SimplePanel implements StatisticsPresenter.Display {

	private final StatisticsSelectionDialog selectionDialog;
	private final LoadingWidget loadingWidget;
	private MapLayer mapLayer;
	private Timeline timeline;
	private WidgetFactory widgetFactory;
	private AbsolutePanel mapContainer;
	
	@Inject
	public StatisticsView(StatisticsSelectionDialog selectionDialog, WidgetFactory widgetFactory) {
		this.selectionDialog = selectionDialog;
		this.widgetFactory = widgetFactory;
		loadingWidget = widgetFactory.getLoadingWidget();
		timeline = widgetFactory.createTimeline();

	}

	@Override
	public Widget asWidget() {
		return this;
	}

	@Override
	public void startProcessing() {
		loadingWidget.center();
	}

	@Override
	public void stopProcessing() {
		loadingWidget.hide();

	}

	@Override
	public void setMapLayer(MapLayer mapLayer) {
		this.mapLayer = mapLayer;
		mapContainer = mapLayer.getMapView().getContainer();
	}

	@Override
	public void showSelectionDialog(List<Resource> datasets) {
		selectionDialog.show(datasets);
	}

	@Override
	public void addSelectionDialogHandler(SelectionHandler<StatisticDefinition> selectionHandler) {
		selectionDialog.addSelectionHandler(selectionHandler);
	}

	@Override
	public void closeSelectionDialogIfOpened() {
		if (selectionDialog.isVisible()) {
			selectionDialog.hide();
		}
	}

	@Override
	public void clear() {
		mapLayer.clear();
	}

	@Override
	public void setTimelineIndex(int index) {
		timeline.setCurrentValue(index, true);

	}

	@Override
	public void setTimelineValueChangeHandler(ValueChangeHandler<Year> changeHandler) {
		timeline.addValueChangeHandler(changeHandler);
	}

	@Override
	public void showTimeline(List<Year> years) {
		timeline.setYears(years);
	}

	@Override
	public void hideTimeline() {
		timeline.hide();
	}

	@Override
	public void refreshTimeline() {
		timeline = widgetFactory.createTimeline();
		mapContainer.add(timeline);
		Element h = timeline.getElement();
		DOM.setStyleAttribute(h, "position", "absolute");
		DOM.setStyleAttribute(h, "right", 22 + "px");
		DOM.setStyleAttribute(h, "bottom", 22 + "px");
		DOM.setStyleAttribute(h, "zIndex", "2024");
	}
}
