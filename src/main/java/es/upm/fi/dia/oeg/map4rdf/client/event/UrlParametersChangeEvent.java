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
package es.upm.fi.dia.oeg.map4rdf.client.event;

import java.util.HashMap;

import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Alexander De Leon
 */
public class UrlParametersChangeEvent extends GwtEvent<UrlParametersChangeEventHandler> {

	private static GwtEvent.Type<UrlParametersChangeEventHandler> TYPE;

	private HashMap<String, String> paramaters;

	public UrlParametersChangeEvent(){
		super();
	}
	
	public UrlParametersChangeEvent(HashMap<String, String> paramters) {
		super();
		setParamaters(paramters);
	}

	public static Type<UrlParametersChangeEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<UrlParametersChangeEventHandler>();
		}
		return TYPE;
	}


	@Override
	protected void dispatch(UrlParametersChangeEventHandler handler) {
		// TODO Auto-generated method stub
		handler.onParametersChange(this);
	}

	@Override
	public Type<UrlParametersChangeEventHandler> getAssociatedType() {
		return getType();
	}

	public HashMap<String, String> getParamaters() {
		return paramaters;
	}

	public void setParamaters(HashMap<String, String> paramaters) {
		this.paramaters = paramaters;
	}

}
