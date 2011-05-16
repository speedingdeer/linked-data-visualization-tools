package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

public class WebNMasUnoTrip extends WebNMasUnoResource implements Serializable {
    
	private String title;
        private String url;
        private String uri;
        private String idItinerario;
        private String date;
        
	//Esto solo son algunas, podria haber mas (Ejemplo visualizacion)
	
	public WebNMasUnoTrip(){
		
	}
	
	public WebNMasUnoTrip(String title, String url, String uri, String idItiner, String d){
            if(title.equals("")){
                this.title = "No disponible";
            }
            else{
                this.title = title;
            }
            if(d.equals("")){
                date = "No disponible";
            }else{
                date = d;
            }
            this.url = url;
            this.uri = uri;
            this.idItinerario = idItiner;
	}
	
	public String getTitle(){
		return title;
	}

        public String getURL() {
            return url;
        }
        
	public String getURI(){
            return uri;
	}

        public String getItinerario(){
            return idItinerario;
        }

        public String getDate(){
            return date;
        }
	
}
