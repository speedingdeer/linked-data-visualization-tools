/**
 * Copyright (c) 2011 Alexander De Leon Battista
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;

import org.tmatesoft.sqljet.core.internal.lang.SqlParser.neq_subexpr_return;

import net.customware.gwt.dispatch.client.DefaultDispatchAsync;
import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import de.micromata.opengis.kml.v_2_2_0.Link;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetImagesResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoGuide;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoImage;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoResource;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoResourceContainer;
import es.upm.fi.dia.oeg.map4rdf.share.WebNMasUnoTrip;

/**
 * @author Alexander De Leon
 */
public class GeoResourceSummary extends Composite {

	private Label label;	
	private Anchor link;
	private HorizontalPanel panelGlobal;
	private Hyperlink editLink;
	private InlineLabel moreInfo;
	private InlineLabel editInfo;
	private VerticalPanel panelGuias, panelViajes, panelFoto;
	private TabPanel panelPestanas;
	private VerticalPanel photoContainer;
	VerticalPanel pieDeFoto;
	HorizontalPanel links;
	private final Label pieFoto = new Label("No hay fotos disponibles ");
	public interface Stylesheet {
		String summaryLabelStyle();
		String summaryPropertyName();
		String summaryPropertyValue();
	}

	private Stylesheet style;
	private BrowserMessages messages;
	private BrowserResources browserResources;
	
	//data
    private ArrayList<WebNMasUnoResource> guias = null;
    private int numGuiaActual;
    
	public GeoResourceSummary(BrowserMessages messages, BrowserResources browserResources) {
		this.messages = messages;
		this.browserResources = browserResources;
		style = browserResources.css();
		initWidget(createUi());
	}

	public GeoResourceSummary() {
		initWidget(createUi());
	}

	public void setGeoResource(GeoResource resource, Geometry geometry) {
		//fill up data
		WebNMasUnoResourceContainer wrc = (WebNMasUnoResourceContainer)resource;
	    ArrayList<WebNMasUnoResource> resources = wrc.getWebNMasUnoResources();
		numGuiaActual = 0;
        guias = new ArrayList<WebNMasUnoResource>();
        panelGuias.clear();
        panelViajes.clear();
        //photoContainer.clear();
        links.clear();
        for(int i = 0; i< resources.size(); i++){
            //check whether it is a guide or a trip (for the moment we are only drawing this)
            //in the future-> edges.
            try{
                WebNMasUnoGuide guide = (WebNMasUnoGuide) resources.get(i);
                this.guias.add(guide);
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
      
        panelPestanas.selectTab(0);
        //photoContainer.add(new Image());//necesaria para inicializacion
        if(guias.size()>0){
            panelGlobal.setWidth("530px");
            this.replaceContainer(photoContainer,pieFoto);
            
            Anchor anterior = new Anchor("anterior");
            anterior.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if(guias.size()>0){
                        numGuiaActual--;
                        if(numGuiaActual<0){
                            numGuiaActual = guias.size()-1;
                        }
                        replaceContainer(photoContainer,pieFoto);
                    }
                }
            });
            links.add(anterior);
            Anchor siguiente = new Anchor("siguiente");
            siguiente.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if(guias.size()>0){
                        numGuiaActual++;
                        numGuiaActual = numGuiaActual % guias.size();
                        replaceContainer(photoContainer,pieFoto);
                    }
                }
            });
            links.add(siguiente);
            
            
        }

	}
	

	private void hideMoreInfo() {
		editInfo.setVisible(false);
		editLink.setVisible(false);
		link.setVisible(false);
		moreInfo.setVisible(false);
	}
	private void showMoreInfo() {
		editInfo.setVisible(true);
		editLink.setVisible(true);
		link.setVisible(true);
		moreInfo.setVisible(true);
	}
	private Widget createUi() {
		panelGlobal = new HorizontalPanel();
		panelPestanas = new TabPanel();
        panelPestanas.setWidth("400px");
        panelGuias = new VerticalPanel();
        panelViajes = new VerticalPanel();
        
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
        
        panelGlobal.setSpacing(10);        
        panelFoto = new VerticalPanel();
        photoContainer = new VerticalPanel();
        pieDeFoto = new VerticalPanel();
        
        links = new HorizontalPanel();
        links.setSpacing(4);
        
        photoContainer.add(new Image());//necesaria para inicializacion
        
        panelFoto.add(photoContainer);
        panelFoto.add(links);
        panelGlobal.add(panelFoto);
        pieDeFoto.add(panelPestanas);
        pieDeFoto.add(pieFoto);
        panelGlobal.add(pieDeFoto);
        
        return panelGlobal;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/*******************************************************************
	 *************************HELPERS**********************************
	 *******************************************************************/
	

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
            VerticalPanel dibujos = new VerticalPanel();
            Anchor dibujarIt = new Anchor("Dibujar viaje");
            dibujarIt.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    //Window.open("http://google.com", "_blank", null);
                    //hacer aqui la llamada a pintar el viaje polilinea.
                    //new DrawItineraryOnClick(t.getItinerario(),disp ,ref);
                    if(SimileTimeLine.enabled){
                        //new DrawTripTimeLine(t.getURI(), disp, ref);
                    }
                }
            });
            Anchor timeL = new Anchor("Historial");
            timeL.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if(SimileTimeLine.enabled){
                        //new DrawTripTimeLine(t.getURI(), disp, ref);
                    }else{
                        Window.alert("Has de activar la linea temporal (Capas->Linea temporal)");
                    }
                }
            });
            dibujos.setSpacing(5);
            dibujos.add(dibujarIt);
            dibujos.add(timeL);
            panelLinea.add(dibujos);
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
//        Image icon = new Image(bResources.rdfMiniLabel());
//
//        icon.setPixelSize(icon.getWidth(), icon.getHeight());
//        //b.setSize(""+icon.getWidth()+"px",""+icon.getHeight()+"px");//the size of the image
//        icon.addStyleName("globalMoveCursor");
//        icon.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                com.google.gwt.user.client.Window.open(url, "_blank", "");
//            }
//        });
//        return icon;

        //En su momento devolver esto con css y sin pushButton
        Image icon = new Image(browserResources.rdfMiniLabel());

        icon.setPixelSize(icon.getWidth(), icon.getHeight());
        PushButton b = new PushButton(icon);
        b.setSize(""+icon.getWidth()+"px",""+icon.getHeight()+"px");//the size of the image
        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open(url, "_blank", "");
            }
        });        
        return b;

    }

    private void replaceContainer(final VerticalPanel photoContainer, final Label pieFoto){
        //String imgNueva = getImage();
        final WebNMasUnoGuide guia= (WebNMasUnoGuide)guias.get(numGuiaActual);
        String uriGuiaRef = guia.getURI();
        pieFoto.setText("no hay fotos de la guia: "+guia.getTitle());
        if(uriGuiaRef==null||uriGuiaRef.equals(""))return;
        GetImagesResource action = new GetImagesResource(uriGuiaRef);
        //disp.startProcessing();
        DispatchAsync d = new DefaultDispatchAsync();
        d.execute(action, new AsyncCallback<SingletonResult<GeoResource>>() {
                @Override
                public void onFailure(Throwable caught) {
                        // TODO Auto-generated method stub
                        //disp.stopProcessing();
                        System.err.println("Fallo "+caught.getMessage());
                        //Window.alert("Fallo "+caught.getMessage());
                }
                @Override
                public void onSuccess(SingletonResult<GeoResource> result) {
                    //disp.stopProcessing();
                    try{
                        final WebNMasUnoResourceContainer imgs = (WebNMasUnoResourceContainer) result.getValue();
                        if(imgs.getWebNMasUnoResources().size()>0){
                            //Window.alert(((WebNMasUnoImage)imgs.getWebNMasUnoResources().get(0)).getPname());
                            //solo devolvemos la primera imagen (1 por guia)
                            String imgNueva = ((WebNMasUnoImage)imgs.getWebNMasUnoResources().get(0)).getPname();
                            if(imgNueva!=null && !imgNueva.equals("")){
                                imgNueva = dameURLFoto(imgNueva);
                                Image i = new Image(imgNueva);
                                i.setPixelSize(100, 135);
                                photoContainer.remove(0);
                                //photoContainer.add(new PushButton(i));
                                photoContainer.add(i);
                                pieFoto.setText("Foto de guia: "+guia.getTitle());
                            }
                        }else{
                            //ponemos foto de no encontrado
                            //
                            Image i = new Image("http://blog.bioethics.net/no-photo%20red.jpg");
                            i.setPixelSize(100, 135);
                            photoContainer.remove(0);
                            //photoContainer.add(new PushButton(i));
                            photoContainer.add(i);
                        }
                    }catch(Exception e){
                        Window.alert(e.getMessage());    
                    }
                }
            });
    }
    /**
     * Metodo auxiliar que dada un id de una foto devuelve su URL ( la del jpg)
     */
    private String dameURLFoto(String idFoto){
        String [] splitResult = idFoto.split("/");
        String convertedURL = "http://elviajero.elpais.com/recorte/"+splitResult[4]+"/XLCO/Ies/"+splitResult[4]+".jpg";
        //System.out.println(intento);
        return convertedURL;
    }
	
	
	
	
}
