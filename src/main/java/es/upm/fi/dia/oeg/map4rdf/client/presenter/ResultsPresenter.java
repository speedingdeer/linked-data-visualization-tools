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
package es.upm.fi.dia.oeg.map4rdf.client.presenter;

import java.util.List;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;

/**
 * @author Alexander De Leon
 */
@Singleton
public class ResultsPresenter extends ControlPresenter<ResultsPresenter.Display> {

	public interface Display extends WidgetDisplay {

		void clear();

		void addResourceLink(String name, String uri);

	}

	@Inject
	public ResultsPresenter(Display display, EventBus eventBus) {
		super(display, eventBus);
	}

	public void clear() {
		getDisplay().clear();
	}

	public void setResults(List<GeoResource> results) {
		for (GeoResource resource : results) {
			String label = resource.getLabel(LocaleUtil.getClientLanguage());
			if (label == null) {
				resource.getDefaultLabel();
			}
			for (String lang : LocaleUtil.getFallbackLanguages()) {
				label = resource.getLabel(lang);
				if (label != null) {
					break;
				}
			}
			getDisplay().addResourceLink(label == null ? resource.getUri() : label, resource.getUri());
		}
	}

	@Override
	protected void onBind() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onUnbind() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public void revealDisplay() {
		// TODO Auto-generated method stub

	}

}
