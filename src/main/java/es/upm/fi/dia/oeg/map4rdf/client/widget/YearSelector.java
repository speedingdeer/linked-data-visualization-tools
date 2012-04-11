/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;

/**
 *
 * @author Daniel Garijo
 */
public class YearSelector extends Composite{
    
    private final HorizontalPanel panel = new HorizontalPanel();
    private ListBox yearList;

    //nota: esto no se deberia hacer asi. Deberia hacerse con un singleton
    //de la clase
    public static String valorElegido = "2000";
    public static boolean enabled = false;

    public YearSelector(ArrayList<String> years) {        
        initWidget(createUi(years));
        //addStyleName(stylesheet.timeline());
        bindEvents();      
    }

    @Override
	protected void onLoad() {
		super.onLoad();
		//SliderBar.injectDefaultCss();
	}
    public void enable(){
        enabled = true;
    }
    public void disable(){
        enabled = false;
    }

    private void bindEvents() {
        yearList.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    valorElegido = yearList.getItemText(yearList.getSelectedIndex());
//                    ListBox l = (ListBox)event.getSource();
//                    Window.alert("ANYO SELECCIONADO: "+yearList.getItemText(yearList.getSelectedIndex()));
                }
            });
    }

    private Widget createUi(ArrayList<String> years) {
        panel.setSpacing(5);
        panel.add(new Label("Mostrar recursos posteriores a:"));
        this.yearList = new ListBox();
        for(int i = 0; i< years.size();i++){
            yearList.addItem(years.get(i));
        }
        panel.add(yearList);

        //panel.setBodyStyle("backgroundColor: #FFFFFF;");
        //panel.setStyle("backgroundColor", "#C6D4E6");
        return panel;
    }



}
