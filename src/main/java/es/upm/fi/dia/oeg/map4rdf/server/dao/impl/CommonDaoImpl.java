package es.upm.fi.dia.oeg.map4rdf.server.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.RDFS;

import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.util.DescriptionsFactory;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;

public class CommonDaoImpl {
	
	protected final String endpointUri;
	
	public CommonDaoImpl(String endpointUri) {
		this.endpointUri = endpointUri;
	}
	
	public List<SubjectDescription> getSubjectDescription(String subject)
			throws DaoException {
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetSubjectDescriptionString(subject));
		ArrayList<SubjectDescription> result = new ArrayList<SubjectDescription>();
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				result.add(DescriptionsFactory.getSubjectDescription(solution));
			}
			return result;
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}
	
	public String getLabel(String uri) throws DaoException {
		String result = "";
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetLabelQuery(uri));
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				Literal l = solution.getLiteral("?label");
				result = l.getLexicalForm();
			}
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
		return result;
	}
	
	public String createGetSubjectDescriptionString(String subject) {
		StringBuilder query = new StringBuilder("SELECT ?p ?o WHERE {");
		query.append("<" +subject+ ">");
		query.append(" ?p ?o .");
		query.append("}");
		return query.toString();
	}
	
	public String createGetLabelQuery(String uri) {
		StringBuilder query = new StringBuilder("SELECT ?label WHERE {");
		query.append(" <" + uri +"> <" + RDFS.label + "> ?label.");
		query.append("}");
		return query.toString();
	}
	
	protected StringBuilder addBoundingBoxFilter(StringBuilder query, BoundingBox boundingBox) {
		query.append(" FILTER(");
	    query.append("(");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getTop().getY() + ")" + "-" + "(" + boundingBox.getLeft().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getLeft().getX() + ")" + "-" + "(" + boundingBox.getTop().getX() + "))"+ "+");
		query.append("((" + boundingBox.getTop().getX() + ")*(" + boundingBox.getLeft().getY() + ") - (" + boundingBox.getTop().getY() + ")*(" + boundingBox.getLeft().getX() + "))");
		query.append(") >= 0");
		
		query.append("&&");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getLeft().getY() + ")" + "-" + "(" + boundingBox.getRight().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getRight().getX() + ")" + "-" + "(" + boundingBox.getLeft().getX() + "))"+ "+");
		query.append("((" + boundingBox.getLeft().getX() + ")*(" + boundingBox.getRight().getY() + ") - (" + boundingBox.getLeft().getY() + ")*(" + boundingBox.getRight().getX() + "))");
		query.append(") >= 0");
		
		query.append("&&");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getRight().getY() + ")" + "-" + "(" + boundingBox.getTop().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getTop().getX() + ")" + "-" + "(" + boundingBox.getRight().getX() + "))"+ "+");
		query.append("((" + boundingBox.getRight().getX() + ")*(" + boundingBox.getTop().getY() + ") - (" + boundingBox.getRight().getY() + ")*(" + boundingBox.getTop().getX() + "))");
		query.append(") >= 0");
		
		query.append(") || (");
				
		//d1 = px*(ay-by) + py*(bx-ax) + (ax*by-ay*bx);        
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getBottom().getY() + ")" + "-" + "(" + boundingBox.getLeft().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getLeft().getX() + ")" + "-" + "(" + boundingBox.getBottom().getX() + "))"+ "+");
		query.append("((" + boundingBox.getBottom().getX() + ")*(" + boundingBox.getLeft().getY() + ") - (" + boundingBox.getBottom().getY() + ")*(" + boundingBox.getLeft().getX() + "))");
		query.append(") >= 0");
		
		query.append("&&");
		//d2 = px*(by-cy) + py*(cx-bx) + (bx*cy-by*cx);
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getLeft().getY() + ")" + "-" + "(" + boundingBox.getRight().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getRight().getX() + ")" + "-" + "(" + boundingBox.getLeft().getX() + "))"+ "+");
		query.append("((" + boundingBox.getLeft().getX() + ")*(" + boundingBox.getRight().getY() + ") - (" + boundingBox.getLeft().getY() + ")*(" + boundingBox.getRight().getX() + "))");
		query.append(") >= 0");
		
		query.append("&&");
	    //d3 = px*(cy-ay) + py*(ax-cx) + (cx*ay-cy*ax);
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getRight().getY() + ")" + "-" + "(" + boundingBox.getBottom().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getBottom().getX() + ")" + "-" + "(" + boundingBox.getRight().getX() + "))"+ "+");
		query.append("((" + boundingBox.getRight().getX() + ")*(" + boundingBox.getBottom().getY() + ") - (" + boundingBox.getRight().getY() + ")*(" + boundingBox.getBottom().getX() + "))");
		query.append(") >= 0");
		
		query.append(") || (");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getTop().getY() + ")" + "-" + "(" + boundingBox.getLeft().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getLeft().getX() + ")" + "-" + "(" + boundingBox.getTop().getX() + "))"+ "+");
		query.append("((" + boundingBox.getTop().getX() + ")*(" + boundingBox.getLeft().getY() + ") - (" + boundingBox.getTop().getY() + ")*(" + boundingBox.getLeft().getX() + "))");
		query.append(") <= 0");
		
		query.append("&&");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getLeft().getY() + ")" + "-" + "(" + boundingBox.getRight().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getRight().getX() + ")" + "-" + "(" + boundingBox.getLeft().getX() + "))"+ "+");
		query.append("((" + boundingBox.getLeft().getX() + ")*(" + boundingBox.getRight().getY() + ") - (" + boundingBox.getLeft().getY() + ")*(" + boundingBox.getRight().getX() + "))");
		query.append(") <= 0");
		
		query.append("&&");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getRight().getY() + ")" + "-" + "(" + boundingBox.getTop().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getTop().getX() + ")" + "-" + "(" + boundingBox.getRight().getX() + "))"+ "+");
		query.append("((" + boundingBox.getRight().getX() + ")*(" + boundingBox.getTop().getY() + ") - (" + boundingBox.getRight().getY() + ")*(" + boundingBox.getTop().getX() + "))");
		query.append(") <= 0");
		
		query.append(") || (");
		
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getBottom().getY() + ")" + "-" + "(" + boundingBox.getLeft().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getLeft().getX() + ")" + "-" + "(" + boundingBox.getBottom().getX() + "))"+ "+");
		query.append("((" + boundingBox.getBottom().getX() + ")*(" + boundingBox.getLeft().getY() + ") - (" + boundingBox.getBottom().getY() + ")*(" + boundingBox.getLeft().getX() + "))");
		query.append(") <= 0");
		
		query.append("&&");
		//d2 = px*(by-cy) + py*(cx-bx) + (bx*cy-by*cx);
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getLeft().getY() + ")" + "-" + "(" + boundingBox.getRight().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getRight().getX() + ")" + "-" + "(" + boundingBox.getLeft().getX() + "))"+ "+");
		query.append("((" + boundingBox.getLeft().getX() + ")*(" + boundingBox.getRight().getY() + ") - (" + boundingBox.getLeft().getY() + ")*(" + boundingBox.getRight().getX() + "))");
		query.append(") <= 0");
		
		query.append("&&");
	    //d3 = px*(cy-ay) + py*(ax-cx) + (cx*ay-cy*ax);
		query.append("(");
		query.append("xsd:double(?lng) * " + "((" + boundingBox.getRight().getY() + ")" + "-" + "(" + boundingBox.getBottom().getY() + "))"+ "+");
		query.append("xsd:double(?lat) * " + "((" + boundingBox.getBottom().getX() + ")" + "-" + "(" + boundingBox.getRight().getX() + "))"+ "+");
		query.append("((" + boundingBox.getRight().getX() + ")*(" + boundingBox.getBottom().getY() + ") - (" + boundingBox.getRight().getY() + ")*(" + boundingBox.getBottom().getX() + "))");
		query.append(") <= 0");
		
		query.append(")");
		
		query.append(").");
		return query;
	}
}
