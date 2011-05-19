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
package es.upm.fi.dia.oeg.map4rdf.client.resource;

import name.alexdeleon.lib.gwtblocks.client.widget.togglebutton.ToggleButton;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

import es.upm.fi.dia.oeg.map4rdf.client.widget.DataToolBar;
import es.upm.fi.dia.oeg.map4rdf.client.widget.FacetWidget;
import es.upm.fi.dia.oeg.map4rdf.client.widget.Timeline;

/**
 * @author Alexander De Leon
 */
public interface BrowserResources extends ClientBundle {

	interface BrowserCss extends ToggleButton.Stylesheet, FacetWidget.Stylesheet, DataToolBar.Stylesheet,
			Timeline.Stylesheet, CssResource {
		String header();

		String footer();

		String facets();

		String leftMenu();

		String loadingWidget();
	}

	@Source("style.css")
	BrowserCss css();

	@Source("logo_map4rdfAemet.png")
	ImageResource logo();

	@Source("kml.png")
	ImageResource kmlButton();

	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("top_bg.gif")
	ImageResource topBackground();

	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("facet_bg.png")
	ImageResource facetBackground();

	@Source("ajax-loader.gif")
	ImageResource loadingIcon();

	@Source("beta-trans.png")
	ImageResource betaBadge();

	@Source("stats.png")
	ImageResource statsButton();

	@Source("ViajeroFiltro.png")
	ImageResource viajeroFiltroButton();

	@Source("RDF-mini.png")
	ImageResource rdfMiniLabel();
}
