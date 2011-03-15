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
package es.upm.fi.dia.oeg.map4rdf.server.conf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.Map4rdfVocabulary;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetGroup;

/**
 * @author Alexander De Leon
 */
public class FacetedBrowserConfiguration {

	private final Model model;
	private List<FacetGroup> facetGroups;

	public FacetedBrowserConfiguration(Model configModel) {
		model = configModel;
	}

	public FacetedBrowserConfiguration(InputStream configIn) {
		model = ModelFactory.createDefaultModel();
		model.read(configIn, null, "TURTLE");
	}

	public List<FacetGroup> getFacetGroups() throws ConfigurationException {
		if (facetGroups == null) {
			facetGroups = loadFacetGroups();
		}
		return facetGroups;
	}

	private List<FacetGroup> loadFacetGroups() throws ConfigurationException {

		List<FacetGroup> facetGroups = new ArrayList<FacetGroup>();
		ResIterator iterator = model.listResourcesWithProperty(RDF.type, Map4rdfVocabulary.FacetGroup);
		while (iterator.hasNext()) {
			Resource facetGroupResource = iterator.next();
			try {
				Resource predicate = facetGroupResource.getRequiredProperty(Map4rdfVocabulary.facetPredicate)
						.getResource();
				FacetGroup facetGroup = new FacetGroup(predicate.getURI());
				addLabels(facetGroupResource, facetGroup);

				Statement orderStmt = facetGroupResource.getProperty(Map4rdfVocabulary.orden);
				if (orderStmt != null) {
					facetGroup.setOrder(orderStmt.getInt());
				}

				StmtIterator facetsIterator = facetGroupResource.listProperties(Map4rdfVocabulary.facet);
				while (facetsIterator.hasNext()) {
					Resource facetResource = facetsIterator.next().getResource();
					facetGroup.addFacet(createFacetFromResource(facetResource));
				}
				facetGroups.add(facetGroup);
			} catch (Exception e) {
				throw new ConfigurationException("Error in facets configuration", e);
			}

		}
		return sort(facetGroups);

	}

	/**
	 * @param facetGroups
	 * @return
	 */
	private List<FacetGroup> sort(List<FacetGroup> facetGroups) {
		Collections.sort(facetGroups, new Comparator<FacetGroup>() {
			@Override
			public int compare(FacetGroup o1, FacetGroup o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		return facetGroups;
	}

	/**
	 * @param facetResource
	 * @return
	 */
	private Facet createFacetFromResource(Resource facetResource) {
		Facet facet = new Facet(facetResource.getURI());
		addLabels(facetResource, facet);
		return facet;
	}

	private void addLabels(Resource rdfResource, es.upm.fi.dia.oeg.map4rdf.share.Resource map4rdfResource) {
		StmtIterator labels = rdfResource.listProperties(RDFS.label);
		while (labels.hasNext()) {
			Literal label = labels.next().getLiteral();
			map4rdfResource.addLabel(label.getLanguage(), label.getString());
		}
	}
}
