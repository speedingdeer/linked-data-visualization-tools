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
import es.upm.fi.dia.oeg.map4rdf.client.widget.SimileTimeLine;

/**
 *
 * @author Daniel Garijo
 */
public class SimileTimeLineControl  extends CustomControl {
    private final SimileTimeLine timeline;

    public SimileTimeLineControl(SimileTimeLine tl){
        super(new ControlPosition(ControlAnchor.BOTTOM_LEFT, 20, 30), false, false);
        this.timeline = tl;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    protected Widget initialize(MapWidget map) {
        return timeline;
    }

    public void enable(){
        timeline.enable();
    }
    public void disable(){
        timeline.disable();
    }

}
