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
import com.netthreads.gwt.simile.timeline.client.BandInfo;
import com.netthreads.gwt.simile.timeline.client.BandOptions;
import com.netthreads.gwt.simile.timeline.client.DateTime;
import com.netthreads.gwt.simile.timeline.client.EventSource;
import com.netthreads.gwt.simile.timeline.client.ITimeLineRender;
import com.netthreads.gwt.simile.timeline.client.TimeLineWidget;
//import com.netthreads.test.simile.timeline.client.StonehengeRender;
//import com.netthreads.test.simile.timeline.client.StonehengeRender;
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
       /* TimeLineWidget simileWidget = new TimeLineWidget("100%", "100%", new ITimeLineRender() {

            @Override
            public void render(TimeLineWidget widget) {
                EventSource eventSource = widget.getEventSource();
                ArrayList bandInfos = widget.getBandInfos();

                BandOptions optTime = BandOptions.create();
                optTime.setWidth("90%");
                optTime.setIntervalUnit(DateTime.HOUR());
                optTime.setIntervalPixels(100);
                optTime.setEventSource(eventSource);

                BandOptions optDays = BandOptions.create();
                optDays.setWidth("10%");
                optDays.setIntervalUnit(DateTime.DAY());
                optDays.setIntervalPixels(300);
                optDays.setEventSource(eventSource);
                optDays.setShowEventText(false);
                optDays.setTrackHeight(0.5f);
                optDays.setTrackGap(0.2f);

                BandInfo timeBand = BandInfo.create(optTime);
                BandInfo dayBand = BandInfo.create(optDays);

                bandInfos.add(timeBand);
                bandInfos.add(dayBand);

                dayBand.setSyncWith(bandInfos.indexOf(timeBand));
                dayBand.setHighlight(true);

                widget.setWidth("500px");
                widget.setHeight("100px");
                
            }
        });
        panel.add(simileWidget);
        //TimeLineWidget simileWidget = new TimeLineWidget("100px", "200px", new StonehengeRender());

//                = new TimeLineWidget("100%", "100%", new ITimeLineRender() {
//
//            @Override
//            public void render(TimeLineWidget widget) {
//                Window.alert("Se crea");
//            }
//        });
//        return simileWidget;
        //panel.add(simileWidget);
        /**
         * public DefaultDynamicTimelineRenderer(String width, String height)
  {
    m_width = width;
    m_height = height;
  }

  public void render(TimeLineWidget widget)
  {
    EventSource eventSource = widget.getEventSource();
    ArrayList bandInfos = widget.getBandInfos();

    BandOptions optTime = BandOptions.create();
    optTime.setWidth("90%");
    optTime.setIntervalUnit(DateTime.HOUR());
    optTime.setIntervalPixels(100);
    optTime.setEventSource(eventSource);

    BandOptions optDays = BandOptions.create();
    optDays.setWidth("10%");
    optDays.setIntervalUnit(DateTime.DAY());
    optDays.setIntervalPixels(300);
    optDays.setEventSource(eventSource);
    optDays.setShowEventText(false);
    optDays.setTrackHeight(0.5f);
    optDays.setTrackGap(0.2f);

    BandInfo timeBand = BandInfo.create(optTime);
    BandInfo dayBand = BandInfo.create(optDays);

    bandInfos.add(timeBand);
    bandInfos.add(dayBand);

    dayBand.setSyncWith(bandInfos.indexOf(timeBand));
    dayBand.setHighlight(true);

    widget.setWidth(m_width);
    widget.setHeight(m_height);
         */
        return panel;
    }



}
