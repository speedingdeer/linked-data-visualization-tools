/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Daniel Garijo
 */
public class Intervalo implements Serializable {
	private int anno;
	private int mes;
	private int dia;
	private int hora;
	private int min;

	public Intervalo() {

	}

	public Intervalo(int anno, int mes, int dia, int hora, int minuto) {
		this.anno = anno;
		this.mes = mes;
		this.dia = dia;
		this.hora = hora;
		min = minuto;
	}

	public int getAnno() {
		return anno;
	}

	public int getDia() {
		return dia;
	}

	public int getHora() {
		return hora;
	}

	public int getMes() {
		return mes;
	}

	public int getMin() {
		return min;
	}

	public Date asDate() {
		return new Date(anno, mes, dia, hora, min);
	}

	public String asXSDDateTime() {
		return anno + "-" + (mes < 10 ? "0" : "") + mes + "-" + (dia < 10 ? "0" : "") + dia + "T"
				+ (hora < 10 ? "0" : "") + hora + ":" + (min < 10 ? "0" : "") + min + ":00Z";
	}

	@Override
	public String toString() {
		return (hora + ":" + min + " " + dia + "/" + mes + "/" + anno);
	}

}
