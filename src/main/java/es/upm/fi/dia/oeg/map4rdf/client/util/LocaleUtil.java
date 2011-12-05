/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informetica, Universidad 
 * Politecnica de Madrid, Spain
 * 
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
package es.upm.fi.dia.oeg.map4rdf.client.util;

import com.google.gwt.i18n.client.LocaleInfo;

import es.upm.fi.dia.oeg.map4rdf.share.Resource;

/**
 * @author Alexander De Leon
 */
public class LocaleUtil {

	private static String lang = null;
	private static String[] fallbackLangs = { "en", "es" };

	public static String getClientLanguage() {
		if (lang == null) {
			lang = LocaleInfo.getCurrentLocale().getLocaleName();
			if (lang.equals("default")) {
				lang = "es";
			}
			if (lang.contains("_")) {
				lang = lang.split("_")[0];
			}
		}
		return lang;
	}

	public static String getBestLabel(Resource resource) {
		return getBestLabel(resource, false);
	}

	public static String getBestLabel(Resource resource, boolean includeLang) {
		String label = null;
		String clientLang = getBestLang(resource);
		if (clientLang != null) {
			label = resource.getLabel(clientLang);
			if (includeLang) {
				label += " (" + clientLang + ")";
			}
		}
		if (label == null) {
			label = resource.getDefaultLabel();
		}
		if (label == null) {
			label = resource.getUri();
		}
		return label;
	}

	public static String getBestLang(Resource resource) {
		String clientLang = getClientLanguage();
		if (resource.getLabel(clientLang) != null) {
			return clientLang;
		}
		for (String fallbackLang : fallbackLangs) {
			if (resource.getLabel(fallbackLang) != null) {
				return fallbackLang;
			}
		}
		if (resource.getDefaultLabel() == null && !resource.getLangs().isEmpty()) {
			return resource.getLangs().iterator().next();
		}
		return null;
	}

	public static String[] getFallbackLanguages() {
		return fallbackLangs;
	}
}
