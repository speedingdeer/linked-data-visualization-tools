package es.upm.fi.dia.oeg.map4rdf.client.widget;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.netthreads.gwt.simile.timeline.client.BandInfo;
import com.netthreads.gwt.simile.timeline.client.BandOptions;
import com.netthreads.gwt.simile.timeline.client.DateTime;
import com.netthreads.gwt.simile.timeline.client.EventSource;
import com.netthreads.gwt.simile.timeline.client.ITimeLineRender;
import com.netthreads.gwt.simile.timeline.client.TimeLineWidget;
import java.util.ArrayList;

/**
 *
 * @author Alexander de Leon, Daniel Garijo
 * Ampliado con un patron singleton.
 * El mapControl cre el widget, pero posteriormente
 * puede ser utilizado en mas lugares.
 */


public class SimileTimeLine extends Composite{
    private final HorizontalPanel panel = new HorizontalPanel();
    private TimeLineWidget simileWidget;
    private String width = "500px";
    private String height = "150px";
    public static boolean enabled = false;
    
    
    private static SimileTimeLine instance = null;

    public static SimileTimeLine getInstance(){
        if(instance ==null){
            instance =  new SimileTimeLine();
        }
        return instance;
    }

    private SimileTimeLine() {
        initWidget(createUi());
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

    }

    private Widget createUi() {
        panel.setSpacing(5);
        
        
        simileWidget = new TimeLineWidget("100%", "100%", new ITimeLineRender() {

            @Override
            public void render(TimeLineWidget widget) {              
                widget.setWidth("500px");
                widget.setHeight("150px");

            }
        });
        this.loadBandInfos(null);
        /*try{
            this.addEvents();
        }catch(Exception e){
            Window.alert(e.getMessage());
        }*/
        panel.add(simileWidget);
        return panel;
    }

    /*
     * bandas centradas en la fecha del viaje.
     * Comienza si es null no se ponde fecha (por defecto)
     */
    private void loadBandInfos(String date){
        EventSource eventSource = simileWidget.getEventSource();
        ArrayList bandInfos = simileWidget.getBandInfos();
        bandInfos.clear();

        BandOptions optDays = BandOptions.create();
        optDays.setWidth("85%");
        optDays.setIntervalUnit(DateTime.DAY());
        optDays.setIntervalPixels(100);
        optDays.setEventSource(eventSource);


        BandOptions optMonth = BandOptions.create();
        optMonth.setWidth("15%");
        optMonth.setIntervalUnit(DateTime.MONTH());
        optMonth.setIntervalPixels(150);
        optMonth.setEventSource(eventSource);
        optMonth.setShowEventText(false);
        optMonth.setTrackHeight(0.5f);
        optMonth.setTrackGap(0.2f);
        if(date!=null){
            optDays.setDate(date);
            optMonth.setDate(date);
        }     
        BandInfo dayBand = BandInfo.create(optDays);
        BandInfo monthBand = BandInfo.create(optMonth);

        bandInfos.add(dayBand);
        bandInfos.add(monthBand);
        
        monthBand.setSyncWith(bandInfos.indexOf(dayBand));
        monthBand.setHighlight(true);
    }
    
    /*
     * Metodo que anade los eventos al timeline.
     * 
     */
    public void addEvents(String xmlText, String startDate){
        //habria que tener un eventList como tiene Alex, y cargar
        //cada uno de los eventos. Hay que anadir un onclick de alguna manera.
        //String textoPrueba = "<data><event start=\"Apr 03 2010 17:00:00 GMT\" title=\"Reference Added\" link=\"http://elviajero.elpais.com/articulo/20050813elpvialbv_5/Tes\">Title of the guide: Mosaicos romanos y cocina de autor</event></data>";
       /* String textoPrueba = "<data>"+
      "<event start=\"Apr 01 2011 00:00:00 GMT\""+
      " end=\"Apr 05 2011 22:00:00 GMT\""+
      " isDuration=\"true\""+
      " title=\"My Trip to Madrid\">"+
      " My trip to Madrid step by step"+
      "</event>"+
      "<event start=\"Apr 03 2011 17:00:00 GMT\""+
      " title=\"Reference Added\""+
      " link=\"http://elviajero.elpais.com/articulo/20050813elpvialbv_5/Tes\">"+
      " Title of the guide: Mosaicos romanos y cocina de autor"+
      "</event>"+
      "<event start=\"Apr 01 2011 09:00:00 GMT\""+
      " title=\"Reference Added\""+
      " link=\"http://elviajero.elpais.com/articulo/20101106elpviavje_3/Tes\">"+
      " Title of the guide:"+
      " This text can also be a tagline from the guide."+
      " Image of the guide"+
      "</event></data>";*/
   
    	panel.remove(simileWidget);
        simileWidget = new TimeLineWidget("100%", "100%", new ITimeLineRender() {

            @Override
            public void render(TimeLineWidget widget) {
                widget.setWidth(width);
                widget.setHeight(height);

            }
        });
  
    	
    	this.loadBandInfos(startDate);
        simileWidget.getEventSource().loadXMLText(xmlText);
        //simileWidget.getEventSource().loadXMLText(dataTest2);
   
        this.disable();
        this.enable();
        
        panel.add(simileWidget);
    }
    
    @Override
    public void setWidth(String width) {
    	this.width=width;
    	super.setWidth(width);
    }
    @Override
    public void setHeight(String height) {
    	this.height=height;
    	super.setHeight(height);
    }

    /**
     * Metodo que anade la info a un evento.
     * Puede que haya que recargar todo.
     */

}
