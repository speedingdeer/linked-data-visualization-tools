package es.upm.fi.dia.oeg.map4rdf.client.widget;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectDescriptions;
import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;

public class DescriptionTreeItem {
	
	private Grid grid;
	private TextBox predicate;
	private TextBox object; 
	//@TODO restracture it
	private DescriptionTreeItem parent = null; //the value means that the parent is a root  
	private SubjectDescription subjectDescription;
	private Integer depth = 1;
	
	public DescriptionTreeItem(SubjectDescription subjectDescription, DescriptionTreeItem parent) {
		this.subjectDescription = subjectDescription;
		this.parent = parent;
		grid = new Grid(1, 2);
		this.predicate = new TextBox();
		this.predicate.setWidth("330px");
		this.predicate.setText(subjectDescription.getPredicate().getText());
		this.object = new TextBox();
		this.object.setWidth("330px");
		this.object.setText(subjectDescription.getObject().getText());
		grid.setWidget(0, 0, this.predicate);
		grid.setWidget(0, 1, this.object);
		
		if ( parent!= null ) {
			depth+=1;
		}
		
	}
	
	public Widget getWidget(){
		return grid;
	}
	
	public SubjectDescription getSubjectDescriptions(){
		return this.subjectDescription;
	}
	
	public String getPredicateText(){
		return subjectDescription.getPredicate().getText();
	}
	public String getObjectText(){
		return subjectDescription.getObject().getText();
	}

	public DescriptionTreeItem getParent() {
		return parent;
	}

	public void setParent(DescriptionTreeItem parent) {
		this.parent = parent;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	
}
