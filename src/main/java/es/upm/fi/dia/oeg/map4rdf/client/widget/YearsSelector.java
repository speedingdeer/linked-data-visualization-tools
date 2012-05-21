package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.Arrays;
import java.util.List;

import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

import es.upm.fi.dia.oeg.map4rdf.client.event.FilterYearChangeEvent;

public class YearsSelector extends ListBox {
	
	List<String> years = Arrays.asList("" +
		("----"),
		("2000"),
		("2001"),
		("2002"),
		("2003"),
		("2004"),
		("2005"),
		("2006"),
		("2007"),
		("2008"),
		("2009"),
		("2010"),
		("2011"),
		("2012")
	);
	
	
	public YearsSelector(final EventBus eventBus){
		super();
		for (String year : years ) {
			this.addItem(year);
		}
		this.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {

				String year = years.get(getSelectedIndex());
				FilterYearChangeEvent mapEvent = new FilterYearChangeEvent(year);
				eventBus.fireEvent(mapEvent);
			}
		});
	}
	
}
