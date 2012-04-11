/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.user.client.ui.Widget;
import es.upm.fi.dia.oeg.map4rdf.client.widget.YearSelector;

/**
 *
 * @author Daniel Garijo
 */
public class DatePickerControl  extends CustomControl {
    private final YearSelector yearSelector;

    public DatePickerControl(YearSelector ys){
        super(new ControlPosition(ControlAnchor.BOTTOM_RIGHT, 20, 30), false, false);
        this.yearSelector = ys;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    protected Widget initialize(MapWidget map) {
        return yearSelector;
    }

    public void enable(){
        yearSelector.enable();
    }
    public void disable(){
        yearSelector.disable();
    }

}
