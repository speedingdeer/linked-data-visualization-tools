/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

//import com.anotherbigidea.flash.movie.Button;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.widget.YearSelector;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoGuide;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoResource;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoResourceContainer;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoTrip;
import java.util.ArrayList;
import net.customware.gwt.presenter.client.Display;

/**
 *
 * @author Daniel Garijo
 */
public class DrawWebNMasUnoResourcesOnClick extends DrawGeoResourceOnClick {
    private final BrowserResources bResources;
public DrawWebNMasUnoResourcesOnClick(BrowserResources resources,String uri, final Display disp,GeoResourcesMapControl g,LatLng latLong){
        super(uri,disp, g, latLong);
        this.bResources = resources;
    }

    @Override
    public void draw(SingletonResult<GeoResource> result) {
        InfoWindowContent iwc;
        VerticalPanel panelGuias, panelViajes;
        TabPanel panelPestanas = new TabPanel();
        panelPestanas.setWidth("400px");
        panelGuias = new VerticalPanel();
        panelViajes = new VerticalPanel();
        WebNMasUnoResourceContainer wrc = (WebNMasUnoResourceContainer)result.getValue();
        ArrayList<WebNMasUnoResource> resources = wrc.getWebNMasUnoResources();

//        if(YearSelector.enabled)Window.alert(YearSelector.valorElegido);
        
        for(int i = 0; i< resources.size(); i++){
            //check whether it is a guide or a trip (for the moment we are only drawing this)
            //in the future-> edges.
            try{
                WebNMasUnoGuide guide = (WebNMasUnoGuide) resources.get(i);
                if(YearSelector.enabled){
                    if (is_guide_date_greater_or_equal_than_selector(YearSelector.valorElegido, guide.getDate())){
                        panelGuias.add(this.addEntryGuide(guide));
                    }
                }else{//no esta activado el panel
                    panelGuias.add(this.addEntryGuide(guide));
                }
            }catch(Exception e){
                //class cast exception
                try{
                    WebNMasUnoTrip trip = (WebNMasUnoTrip) resources.get(i);
                    if(YearSelector.enabled){
                        if (is_guide_date_greater_or_equal_than_selector(YearSelector.valorElegido, trip.getDate())){
                            panelViajes.add(this.addEntryTrip(trip));
                            }
                    }else{
                        panelViajes.add(this.addEntryTrip(trip));
                    }
                }catch(Exception e2){
                    System.err.println("Error: "+e2.getMessage());
                }
            }
        }

        if(panelGuias.getWidgetCount() == 0){
            String message = "No hay guias disponibles sobre este punto";
            if(YearSelector.enabled){
                message += " posteriores a la fecha seleccionada";
            }
            panelGuias.add(new Label(message));
        }
        if(panelViajes.getWidgetCount() == 0){
            String message = "No hay viajes disponibles sobre este punto";
            if(YearSelector.enabled){
                 message += " posteriores a la fecha seleccionada";
            }
            panelViajes.add(new Label(message));
        }
        ScrollPanel scrollerGuias = new ScrollPanel();        
        scrollerGuias.add(panelGuias);
        scrollerGuias.setHeight("100px");
        ScrollPanel scrollerViajes = new ScrollPanel();
        scrollerViajes.add(panelViajes);
        scrollerViajes.setHeight("100px");
        VerticalPanel pG = new VerticalPanel();
        pG.add(scrollerGuias);
        pG.setHeight("100px");
        panelPestanas.add(pG,"Guias");

        VerticalPanel pV = new VerticalPanel();
        pV.add(scrollerViajes);
        pV.setHeight("100px");
        panelPestanas.add(pV,"Viajes");

        panelPestanas.selectTab(0);
        panelPestanas.setHeight("150px");
        
        /**
         * Prueba para ver imgs. aparte del panel con pestanas anadimos  el de la foto
         */
        HorizontalPanel panelGlobal = new HorizontalPanel();
        panelGlobal.setSpacing(10);
        panelGlobal.setWidth("530px");
        VerticalPanel panelFoto = new VerticalPanel();
        Image im = new Image(("http://elviajero.elpais.com/recorte/20050430elpviavje_4/XLCO/Ies/20050430elpviavje_4.jpg"));
        im.setPixelSize(100, 135);
        PushButton p = new PushButton(im);
        panelFoto.add(p);
        panelFoto.add(new Label("Anterior/siguiente"));
        
        panelGlobal.add(panelFoto);
        panelGlobal.add(panelPestanas);
        //fin prueba

        //iwc = new InfoWindowContent(panelPestanas);
        iwc = new InfoWindowContent(panelGlobal);
        this.infoWindow.open(latl, iwc);
    }

    private HorizontalPanel addEntryGuide(final WebNMasUnoGuide g){
        HorizontalPanel panelLinea = new HorizontalPanel();
        panelLinea.setSpacing(10);
        if(g.getTitle()!=null && !g.getTitle().equals("")){
            Label titulo = new Label("Titulo:");
            //poner en negrita el titulo
            panelLinea.add(titulo);
            if(!g.getURL().equals("")){
                Anchor titulo_url = new Anchor(g.getTitle(), g.getURL(),"_blank");
                panelLinea.add(titulo_url);
            }
            else{
                panelLinea.add(new Label (g.getTitle()));
            }
        }
        /**
        Document.get().createAnchorElement()
        Anchor rdf_uri = new Anchor("rdf", g.getURI(),"_blank");
        Image icon = new Image(bResources.rdfMiniLabel());
        panelLinea.add(icon);
         * construccion de un anchor con una imagen
         **/
        
        panelLinea.add(this.getRDFButton(g.getURI()));


        if(g.getDate()!=null && !g.getDate().equals("")){            
            //panelLinea.add(new Label("Fecha:"));
            panelLinea.add(new Label(this.transformDate(g.getDate())));
        }
        return panelLinea;
    }

    private HorizontalPanel addEntryTrip(final WebNMasUnoTrip t){
        HorizontalPanel panelLinea = new HorizontalPanel();
        panelLinea.setSpacing(10);
        if(t.getTitle()!=null && !t.getTitle().equals("")){
            Label titulo = new Label("Titulo:");
            //poner en negrita el titulo
            panelLinea.add(titulo);
            if(!t.getURL().equals("")){
                Anchor titulo_url = new Anchor(t.getTitle(), t.getURL(),"_blank");
                panelLinea.add(titulo_url);
            }else{
                panelLinea.add(new Label(t.getTitle()));
            }
        }
        panelLinea.add(this.getRDFButton(t.getURI()));
        if(t.getItinerario()!=null && !t.getItinerario().equals("")){
            Anchor dibujar = new Anchor("Dibujar viaje");
            dibujar.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    //Window.open("http://google.com", "_blank", null);
                    //hacer aqui la llamada a pintar el viaje polilinea.
                    new DrawItineraryOnClick(t.getItinerario(),disp ,ref);
                }
            }
            );
            panelLinea.add(dibujar);
        }        
        if(t.getDate()!=null && !t.getDate().equals("")){
            //panelLinea.add(new Label("Fecha:"));
            panelLinea.add(new Label(this.transformDate(t.getDate())));
        }
        return panelLinea;
    }
    /**
     * Transforms a date on format 20090721 to 2009-07-21
     * @param date
     * @return
     */
    private String transformDate(String date){
        if(date.contains("disponible"))return date; //si pone "No disponible" lo dejamos =
        String returnValue = "";
        String anyo = date.substring(0,4);
        String mes = date.substring(4,6);
        String dia = date.substring(6,date.length());
        returnValue = dia+"-"+mes+"-"+anyo;
        return returnValue;

    }

    /**
     * Compares the year and the date. Returns true if the date
     * is later than the year
     * @param year Year. Example: 2001
     * @param date Date form the guides. Example: 20010131
     * @return
     */
    private boolean is_guide_date_greater_or_equal_than_selector(String year, String date){
        try{
            int y = Integer.parseInt(year);//viene bien ya
            String d = date.trim();//para asegurarnos de que coge bien los caracteres.
            d = d.substring(0, 4); //de 20010131 coge 2001
            int y1 = Integer.parseInt(d);
            if(y<=y1)return true;
            else return false;
        }catch(Exception e){
            return false;
        }
    }

    private PushButton getRDFButton(final String url){
        Image icon = new Image(bResources.rdfMiniLabel());
        PushButton b = new PushButton(icon);
        b.setPixelSize(icon.getWidth(), icon.getHeight());
        //b.setSize(""+icon.getWidth()+"px",""+icon.getHeight()+"px");//the size of the image
        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open(url, "_blank", "");
            }
        });
        return b;
    }
}
