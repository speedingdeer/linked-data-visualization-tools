package es.upm.fi.dia.oeg.map4rdf.server.util;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;

import es.upm.fi.dia.oeg.map4rdf.share.BasicRDFInformation;
import es.upm.fi.dia.oeg.map4rdf.share.RDFLiteral;
import es.upm.fi.dia.oeg.map4rdf.share.RDFResource;
import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;

/**
 * @author Filip
 */
public class DescriptionsFactory {
	
	public static SubjectDescription getSubjectDescription(QuerySolution querySolution){
		SubjectDescription subjectDescription = new  SubjectDescription();
		if(querySolution.get("p").isLiteral()){
			subjectDescription.setPredicate(
					new RDFLiteral(querySolution.getLiteral("p").getDatatypeURI(),
							querySolution.getLiteral("p").getLexicalForm(),querySolution.getLiteral("o").toString()));
		} else {
			subjectDescription.setPredicate(
					new RDFResource(querySolution.getResource("p").getLocalName(),
							querySolution.getResource("p").getNameSpace(),
							querySolution.getResource("p").getURI()));
		}
		if(querySolution.get("o").isLiteral()){
			subjectDescription.setObject(
					new RDFLiteral(querySolution.getLiteral("o").getDatatypeURI(),
							querySolution.getLiteral("o").getLexicalForm(),querySolution.getLiteral("o").toString()));
		} else {
			subjectDescription.setObject(
					new RDFResource(querySolution.getResource("o").getLocalName(),
							querySolution.getResource("o").getNameSpace(),
							querySolution.getResource("o").getURI()));
		}
		return subjectDescription;
	}
}
