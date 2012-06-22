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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.override.client.Grid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.view.v2.OpenLayersMapView;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.MapShape;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.conf.UrlParamtersDict;

/**
 * @author Alexander De Leon
 */
public class GeoResourceSummary extends Composite {

	public interface Stylesheet {
		String summaryLabelStyle();
		String summaryPropertyName();
		String summaryPropertyValue();
	}
	private FlowPanel panel;
	private Stylesheet style;
	private BrowserMessages messages;
	private Label longitude;
	private Label label;
	private Label latitude;
	private Anchor link;
	private Panel locationPanel;
	private Hyperlink editLink;
	InlineLabel moreInfo;
	InlineLabel editInfo;
	
	public GeoResourceSummary(BrowserMessages messages, BrowserResources appResources) {
		this.messages = messages;
		style = appResources.css();
		initWidget(createUi());
	}

	public GeoResourceSummary() {
		initWidget(createUi());
	}

	public void setGeoResource(GeoResource resource, Geometry geometry, OpenLayersMapView display) {
		label.setText(LocaleUtil.getBestLabel(resource, true));
		panel.remove(editLink);
		editLink = new InlineHyperlink(messages.here(),Places.DASHBOARD.toString() );
		panel.add(editLink);
		if (geometry.getType() == MapShape.Type.POINT) {
			locationPanel.setVisible(true);
			Point point = (Point) geometry;
			latitude.setText(Double.toString(point.getY()));
			longitude.setText(Double.toString(point.getX()));
		} else {
			locationPanel.setVisible(false);
		}
		if (resource.getUri()!=null) {
			showMoreInfo();
			link.setHref(resource.getUri());
			
		} else {
			hideMoreInfo();
		}
	}

	private void hideMoreInfo() {
		editInfo.setVisible(false);
		editLink.setVisible(false);
		link.setVisible(false);
		moreInfo.setVisible(false);
	}
	private void showMoreInfo() {
		editInfo.setVisible(true);
		editLink.setVisible(true);
		link.setVisible(true);
		moreInfo.setVisible(true);
	}
	private Widget createUi() {
		panel = new FlowPanel();
		label = new Label();
		label.addStyleName(style.summaryLabelStyle());
		panel.add(label);
		panel.add(new InlineHTML("<br />"));

		locationPanel = new FlowPanel();

		Grid grid = new Grid(2, 2);
		Label latitudeLabel = new Label(messages.latitude() + ": ");
		latitudeLabel.setStyleName(style.summaryPropertyName());
		grid.setWidget(0, 0, latitudeLabel);
		latitude = new Label();
		latitude.setStyleName(style.summaryPropertyValue());
		grid.setWidget(0, 1, latitude);
		Label longitudLabel = new Label(messages.longitude() + ": ");
		longitudLabel.setStyleName(style.summaryPropertyName());
		grid.setWidget(1, 0, longitudLabel);
		longitude = new Label();
		longitude.setStyleName(style.summaryPropertyValue());
		grid.setWidget(1, 1, longitude);
		locationPanel.add(grid);
		locationPanel.add(new InlineHTML("<br />"));

		panel.add(locationPanel);

		moreInfo = new InlineLabel(messages.information() + " ");
		panel.add(moreInfo);

		link = new Anchor(messages.here(), "", "_blank");
		panel.add(link);
		
		editInfo = new InlineLabel(messages.edit() + " ");	
		editLink = new InlineHyperlink(messages.here(),Places.DASHBOARD.toString() );
		panel.add(new InlineHTML("<br />"));		
		panel.add(editInfo);
		panel.add(editLink);
		
		return panel;
	}
	
	public Hyperlink getEditLink() {
		return editLink;
	}
}
