package es.upm.fi.dia.oeg.map4rdf.share;

import com.google.gwt.http.client.URL;

public class URLSafty {
	
	private String url;
	
	
	public URLSafty() {
		
	}
	
	public URLSafty(String url) {
		this.url = url;
	}
	

	public String getUrlSafty() {
		return URL.encode(url).toString().replaceAll(",","%2C");
	}

	public String getUrl() {
		return URL.decode(url).toString().replaceAll("%2C",",");
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
