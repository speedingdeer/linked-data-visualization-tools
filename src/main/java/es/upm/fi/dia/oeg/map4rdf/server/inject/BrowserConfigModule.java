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
package es.upm.fi.dia.oeg.map4rdf.server.inject;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import es.upm.fi.dia.oeg.map4rdf.server.conf.Configuration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import es.upm.fi.dia.oeg.map4rdf.server.conf.FacetedBrowserConfiguration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.ParameterDefaults;
import es.upm.fi.dia.oeg.map4rdf.server.conf.ParameterNames;

/**
 * @author Alexander De Leon
 */
public class BrowserConfigModule extends AbstractModule {

	private final Configuration config;
	private final FacetedBrowserConfiguration facetedBrowserConfiguration;

	public BrowserConfigModule(Configuration config, FacetedBrowserConfiguration facetedBrowserConfiguration) {
		this.config = config;
		this.facetedBrowserConfiguration = facetedBrowserConfiguration;
	}

	@Override
	protected void configure() {
		bind(Configuration.class).toInstance(config);
		bind(FacetedBrowserConfiguration.class).toInstance(facetedBrowserConfiguration);

		bindConstant().annotatedWith(Names.named(ParameterNames.ENDPOINT_URL)).to(
				config.getConfigurationParamValue(ParameterNames.ENDPOINT_URL));

		bindConstant().annotatedWith(Names.named(ParameterNames.GEOMETRY_MODEL)).to(
				Constants.GeometryModel.valueOf(config.getConfigurationParamValue(ParameterNames.GEOMETRY_MODEL)));

		if (config.containsConfigurationParam(ParameterNames.FACETS_AUTO)) {
			bindConstant().annotatedWith(Names.named(ParameterNames.FACETS_AUTO)).to(
					Boolean.parseBoolean(config.getConfigurationParamValue(ParameterNames.FACETS_AUTO)));
		} else {
			bindConstant().annotatedWith(Names.named(ParameterNames.FACETS_AUTO)).to(
					ParameterDefaults.FACETS_AUTO_DEFAULT);
		}

	}
}
