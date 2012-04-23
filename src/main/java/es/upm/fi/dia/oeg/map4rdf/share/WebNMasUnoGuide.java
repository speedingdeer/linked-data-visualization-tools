package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

public class WebNMasUnoGuide extends WebNMasUnoResource implements Serializable {
    
	private String title;
        private String url;
        private String uri;
        private String date;
//        meter la fecha en la query en dbpediadao.
//        tb hacer un boton para saber si esta activado el years o no (una var de estado)
	//Esto solo son algunas, podria haber mas (Ejemplo visualizacion)
	
	public WebNMasUnoGuide(){
		
	}
	
	public WebNMasUnoGuide(String title, String url, String uri, String date){
            if(title.equals("")){
                this.title = "No disponible";
            }else{
                this.title = title;
            }
            if(date.equals("")){
                this.date = "No disponible";
            }else{
                this.date = date;
            }
            this.url = url;
            this.uri = uri;
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

        public String getDate(){
                return date;
        }
	
}
