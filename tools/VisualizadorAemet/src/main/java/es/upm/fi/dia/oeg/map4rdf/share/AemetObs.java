package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

public class AemetObs implements Serializable {
    
	//private String idObs;
        private String uriObs;
        private String estacion;
        private String valor;
        private String calidad;
        private String propiedad;
        private String featureOfInterest;        
        private Intervalo intervalo;
        
	
	public AemetObs(){
		
	}
	
	public AemetObs(String uriObs,String est,String valor, String calidad, String prop,
                String feature, Intervalo inter ){
		
                this.uriObs = uriObs;
                this.estacion = est;
		this.valor = valor;
                this.calidad = calidad;
                this.propiedad = prop;
                this.featureOfInterest = feature;
                this.intervalo = inter;
	}

        public String getUriObs() {
            return uriObs;
        }
        
        public String getEstacion() {
            return estacion;
        }

        public String getCalidad() {
            return calidad;
        }

        public String getFeatureOfInterest() {
            return featureOfInterest;
        }

        public String getIntervalo() {
            return intervalo.toString();
        }

        public String getPropiedad() {
            return propiedad;
        }

        public String getValor() {
            return valor;
        }     
}
