package es.upm.fi.dia.oeg.map4rdf.share;

import com.google.gwt.http.client.URL;

public class URLSafety {
	
	private String url;
	
	
	public URLSafety() {
		
	}
	
	public URLSafety(String url) {
		this.url = url;
	}
	

	public String getUrlSafty() {
		return URL.encode(url).toString().replaceAll(",","%2C").replaceAll("\\^", "%5E");
	}

	public String getUrl() {
		return URL.decode(url).toString().replaceAll("%2C",",").replaceAll("%5E", "\\^");
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
