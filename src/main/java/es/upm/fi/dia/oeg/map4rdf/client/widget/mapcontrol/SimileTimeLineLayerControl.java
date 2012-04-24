package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.widget.SimileTimeLine;
import es.upm.fi.dia.oeg.map4rdf.client.widget.Timeline;
import es.upm.fi.dia.oeg.map4rdf.client.widget.YearSelector;
import es.upm.fi.dia.oeg.map4rdf.share.Year;
import java.util.ArrayList;

/**
 *
 * @author Daniel Garijo
 */
@Singleton
public class SimileTimeLineLayerControl extends LayerControl{
    private SimileTimeLineControl timelineControl;
    private BrowserResources res;

    @Inject
    public SimileTimeLineLayerControl(BrowserResources resources){
        res = resources;
        createUi();
    }

    public void enable(){
        //getMap().addControl(timelineControl);        
        clear(); //se cierran las ventanas al elegir el boton
        getMap().addControl(timelineControl);
        timelineControl.enable();
    }

    @Override
    public void disable(){        
        super.disable();
        //getMap().removeControl(timelineControl);
        timelineControl.disable();
        getMap().removeControl(timelineControl);
    }

    private void createUi() {
          timelineControl = new SimileTimeLineControl(SimileTimeLine.getInstance());
    }


}
