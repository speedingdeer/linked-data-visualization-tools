/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.widget.Timeline;
import es.upm.fi.dia.oeg.map4rdf.client.widget.YearSelector;
import es.upm.fi.dia.oeg.map4rdf.share.Year;
import java.util.ArrayList;

/**
 *
 * @author Dani
 */
@Singleton
public class TimeLineFilterControl extends LayerControl{
    private TimelineControl timelineControl;
    private DatePickerControl controlFecha;
    private BrowserResources res;

    @Inject
    public TimeLineFilterControl(BrowserResources resources){
        res = resources;
        createUi();
    }

    public void enable(){
        //getMap().addControl(timelineControl);        
        clear(); //se cierran las ventanas al elegir el boton
        getMap().addControl(controlFecha);
        controlFecha.enable();
    }

    @Override
    public void disable(){        
        super.disable();
        //getMap().removeControl(timelineControl);
        controlFecha.disable();
        getMap().removeControl(controlFecha);
    }

    private void createUi() {
        //crear timeLineControl con los años
//        ArrayList<Year> y = new ArrayList<Year>();
//        for (int i = 0; i< 10; i++){
//            y.add(new Year("uriYear"+i,Integer.parseInt("200"+i)));
//        }
//        y.add(new Year("uriYear10",Integer.parseInt("2010")));
//
//        Timeline timeline = new Timeline(y, res.css());
//        timeline.addValueChangeHandler(new ValueChangeHandler<Year>() {
//                @Override
//                public void onValueChange(ValueChangeEvent<Year> event) {
//                        //Window.alert("Agno elegido: "+event.getValue().getValue());
//                        //timeline.setCurrentValue(event.getValue().getValue(), true);
//                }
//        });
//        timelineControl = new TimelineControl(timeline);
//        //getMap().addControl(timelineControl);
//        //getDisplay().stopProcessing();
//        timeline.setCurrentValue(0, false);


        ArrayList<String> y = new ArrayList<String>();
        for (int i = 2000; i< 2011; i++){
            y.add(""+i);
        }
        YearSelector yearS = new YearSelector(y);

        controlFecha = new DatePickerControl(yearS);
    }


}
