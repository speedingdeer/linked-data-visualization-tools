package es.upm.fi.dia.oeg.map4rdf.client.widget;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditableDescription {
	
	private Grid grid;
	private TextBox predicate;
	private TextBox object; 
	//@TODO restracture it
	private EditableDescription parent = null; //the value means that the parent is a root  
	
	
	public EditableDescription(String prodicate, String object, EditableDescription parent) {
		grid = new Grid(1, 2);
		this.predicate = new TextBox();
		this.predicate.setText(prodicate);
		this.object = new TextBox();
		this.object.setText(object);
		grid.setWidget(0, 0, this.predicate);
		grid.setWidget(0, 1, this.object);
		this.parent = parent;
	}
	
	public Widget getWidget(){
		return grid;
	}
	
	public String getPredicateText(){
		return predicate.getText();
	}
	public String getObjectText(){
		return object.getText();
	}

	public EditableDescription getParent() {
		return parent;
	}

	public void setParent(EditableDescription parent) {
		this.parent = parent;
	}
	
}
