package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

public class AemetObs implements Serializable {
    /**?obs ?estacion ?prov ?alt ?qv ?vv ?dv ?tIni ?tFin ?hr ?in ?vm ?est ?rviento ?qrviento ?ta ?prec ?tpr ?pres ?raglob ?presnMar**/
	private String idObs;
        private String uriObs;
        private String estacion;
        private String prov;
        private String alt;
        private String qv;
        private String vv;
        private String dv;
        private String rviento;
        private String qrviento;
        private String ta;
        private String prec;
        private String tpr;
        private String pres;
        private String raglob;
        private String presnMar;
	private String tInicio;
	private String tFin;
	private String hR;
	private String indisinop;
	private String vMax;
	private String tMax;
	//Esto solo son algunas, podria haber mas (Ejemplo visualizacion)
	
	public AemetObs(){
		
	}
	
	public AemetObs(String id,String uriObs,String estacion,String tIni, String tFin, String hr, String indi,
			String vM, String tM, String prov, String alt, String qv, String vv, String dv,
                        String rviento, String qrviento, String ta, String prec, String tpr, String pres,
                        String raglob, String presnMar){
		this.idObs = id;
                this.uriObs = uriObs;
		this.tInicio = tIni;
		this.tFin = tFin;
		this.hR = hr;
		this.indisinop = indi;
		this.vMax = vM;
		this.tMax = tM;
                this.estacion = estacion;
                this.prov = prov;
                this.alt = alt;
                this.qv = qv;
                this.vv = vv;
                this.dv = dv;
                this.rviento = rviento;
                this.qrviento = qrviento;
                this.ta = ta;
                this.prec = prec;
                this.tpr = tpr;
                this.pres = pres;
                this.raglob = raglob;
                this.presnMar = presnMar;
	}
	
	public String getIdObs(){
		return idObs;
	}

        public String getUriObs() {
            return uriObs;
        }
        
	public String getTInicio(){
		return tInicio;
	}
	
	public String getTFin(){
		return tFin;
	}
	
	public String getTHR(){
		return hR;
	}
	
	public String getIndisinop(){
		return indisinop;
	}
	
	public String getVMax(){
		return vMax;
	}
	
	public String getTMax(){
		return tMax;
	}

    public String getAlt() {
        return alt;
    }

    public String getDv() {
        return dv;
    }

    public String getEstacion() {
        return estacion;
    }

    public String getPrec() {
        return prec;
    }

    public String getPres() {
        return pres;
    }

    public String getPresnMar() {
        return presnMar;
    }

    public String getProv() {
        return prov;
    }

    public String getQrviento() {
        return qrviento;
    }

    public String getQv() {
        return qv;
    }

    public String getRaglob() {
        return raglob;
    }

    public String getRviento() {
        return rviento;
    }

    public String getTa() {
        return ta;
    }

    public String getTpr() {
        return tpr;
    }

    public String getVv() {
        return vv;
    }

    public String getvMax() {
        return vMax;
    }



}
