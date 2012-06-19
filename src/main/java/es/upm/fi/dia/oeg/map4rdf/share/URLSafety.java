package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

import com.google.gwt.http.client.URL;

/**
 * @author Filip
 */
public class URLSafety implements Serializable{
	
	private String url;
	
	
	public URLSafety() {
		
	}
	
	public URLSafety(String url) {
		this.url = url;
	}
	

	public String getUrlSafty() {
		if (url==null) {
			return "";
		}
		return url.replaceAll(",","%2C").replaceAll("\\^", "%5E").replaceAll(" ","%20");
	}

	public String getUrl() {
		if (url==null){
			return "";
		}
		return (URL.decode(url).toString().replaceAll("%2C",",").replaceAll("%5E", "\\^")).toString();
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
