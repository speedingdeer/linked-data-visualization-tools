package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

public class RDFLiteral implements BasicRDFInformation{
	
	private URLSafety dataTypeURI;
	private String lexicalForm;
	private String fullName;
	private Boolean isLiteral;
	
	public RDFLiteral(){
		//for serialization
	}
	
	public RDFLiteral(String dataTypeURI, String lexicalForm, String fullName){
		this.setDataTypeURI(new URLSafety(dataTypeURI));
		this.setLexicalForm(lexicalForm);
		this.fullName = fullName;
	}

	public URLSafety getDataTypeURI() {
		return dataTypeURI;
	}

	public void setDataTypeURI(URLSafety dataTypeURI) {
		this.dataTypeURI = dataTypeURI;
	}

	public String getLexicalForm() {
		return lexicalForm;
	}

	public void setLexicalForm(String lexicalForm) {
		this.lexicalForm = lexicalForm;
	}

	public Boolean getIsLiteral() {
		return isLiteral;
	}

	public void setIsLiteral(Boolean isLiteral) {
		this.isLiteral = isLiteral;
	}

	@Override
	public String getText() {
		return fullName;
	}

	@Override
	public void seText(String text) {
		this.lexicalForm = text;
	}

	@Override
	public Boolean isResource() {
		return false;
	}

	@Override
	public Boolean isLiteral() {
		return true;
	}
}
