/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

/**
 *
 * @author Daniel Garijo
 */
public class Intervalo implements Serializable {
    private String anno;
    private String mes;
    private String dia;
    private String hora;
    private String min;

    public Intervalo(){

    }

    public Intervalo (String anno, String mes, String dia, String hora, String minuto){
        this.anno = anno;
        this.mes = mes;
        this.dia = dia;
        this.hora = hora;
        this.min = minuto;
    }

    public String getAnno() {
        return anno;
    }

    public String getDia() {
        return dia;
    }

    public String getHora() {
        return hora;
    }

    public String getMes() {
        return mes;
    }

    public String getMin() {
        return min;
    }

    @Override
    public String toString(){
     return (this.hora+":"+this.min+" "+this.dia+"/"+this.mes+"/"+this.anno);
    }


}
