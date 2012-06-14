/**
 * Copyright (c) 2011 Alexander De Leon Battista
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
package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.gwtopenmaps.openlayers.client.Size;

import net.customware.gwt.dispatch.client.DefaultDispatchAsync;
import net.customware.gwt.dispatch.client.DispatchAsync;

import ca.nanometrics.gflot.client.DataPoint;
import ca.nanometrics.gflot.client.PlotModel;
import ca.nanometrics.gflot.client.PlotWidget;
import ca.nanometrics.gflot.client.SeriesHandler;
import ca.nanometrics.gflot.client.SimplePlot;
import ca.nanometrics.gflot.client.options.PlotOptions;
import ca.nanometrics.gflot.client.options.TimeSeriesAxisOptions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.override.client.Grid;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetAemetObsForProperty;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.share.AemetObs;
import es.upm.fi.dia.oeg.map4rdf.share.AemetResource;
import es.upm.fi.dia.oeg.map4rdf.client.view.v2.OpenLayersMapView;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.Intervalo;
import es.upm.fi.dia.oeg.map4rdf.share.MapShape;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.conf.UrlParamtersDict;

/**
 * @author Alexander De Leon
 */
public class GeoResourceSummary extends Composite {

	public interface Stylesheet {
		String summaryLabelStyle();
		String summaryPropertyName();
		String summaryPropertyValue();
	}

	private Stylesheet style;
	private BrowserMessages messages;
	DispatchAsync dispatchAsync;
	private VerticalPanel listPanel;
	private VerticalPanel graphPanel;
	private FlowPanel mainPanel;
	private ScrollPanel listScrollPanel;
	private OpenLayersMapView display;
	private AemetResource ae;
	ArrayList<AemetObs> obs;
	private Size mapSize;
	private Float windowHeight;
	private Float windowWidth;
	
	public GeoResourceSummary(BrowserMessages messages, BrowserResources appResources) {
		this.messages = messages;
		style = appResources.css();
		initWidget(createUi());
	    dispatchAsync = new DefaultDispatchAsync();
	}

	public GeoResourceSummary() {
		initWidget(createUi());
	}

	public void setGeoResource(GeoResource resource, Geometry geometry, final OpenLayersMapView display, Size mapSize) {
		this.display = display;
		display.startProcessing();
		listPanel.clear();
		listPanel.setVisible(true);
		graphPanel.setVisible(false);
		this.mapSize = mapSize;
		this.setSize(true);
		GetGeoResource action = new GetGeoResource(resource.getUri());
        DispatchAsync d = new DefaultDispatchAsync();
		d.execute(action, new AsyncCallback<SingletonResult<GeoResource>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				System.err.println("Fallo "+caught.getMessage());
				display.stopProcessing();
			}
            @Override
            public void onSuccess(SingletonResult<GeoResource> result) {
            	setVariables(result);	
            	buildWindow();
            	display.stopProcessing();
            }
           	});
	}
	
	private Widget createUi() {
		listPanel = new VerticalPanel();
		graphPanel = new VerticalPanel();
		mainPanel = new FlowPanel();
		listScrollPanel = new ScrollPanel();
		listScrollPanel.add(listPanel);
		mainPanel.add(listScrollPanel);
		mainPanel.add(graphPanel);
		return mainPanel;
	}
	
	private void setVariables(SingletonResult<GeoResource> result){
		AemetResource ae = (AemetResource) result.getValue();
		obs = ae.getObs();
	}
	private void buildWindow() {
		listPanel.clear();
		HorizontalPanel caractYGraficas = new HorizontalPanel();
		VerticalPanel caracteristicas = new VerticalPanel();
		VerticalPanel graficas = new VerticalPanel();
		caracteristicas.setSpacing(0);
		graficas.setSpacing(0);
		String text = messages.noObserveData();
		try {
			if (obs.isEmpty()) {
				listPanel.add(new Label(text));
			} else {
				final AemetObs firstObservation = obs.get(0);
				listPanel.add(new Label(messages.station()+ " " + firstObservation.getEstacion().getDefaultLabel()));
				listPanel.add(new Label(firstObservation.getIntervalo().toString()));
				for (final AemetObs observation : obs) {
					HorizontalPanel obsActual = dameUnidadMedicion(observation.getPropiedad().getDefaultLabel(),
							observation.getUriObs(), Double.toString(observation.getValor()));
					caracteristicas.add(obsActual);

					HorizontalPanel graf = new HorizontalPanel();
					graf.setSpacing(5);
					// Graficas de alex aqui
					Anchor dayAnchor = new Anchor(messages.day());
					dayAnchor.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							createDayChart(observation, listPanel, firstObservation.getIntervalo().asDate());
						}
					});
					graf.add(dayAnchor);
					graf.add(new Label("|"));
					Anchor weekAnchor = new Anchor(messages.week());
					weekAnchor.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							createWeekChart(observation, listPanel, firstObservation.getIntervalo().asDate());
						}
					});
					graf.add(weekAnchor);
					/*
					 * graf.add(new Label("|")); Anchor monthAnchor = new
					 * Anchor("mes"); monthAnchor.addClickHandler(new
					 * ClickHandler() {
					 * 
					 * @Override public void onClick(ClickEvent event) {
					 * Window.alert("Por hacer"); } }); graf.add(monthAnchor);
					 */
					graficas.add(graf);

				}
				caractYGraficas.add(caracteristicas);
				caractYGraficas.add(graficas);
				listPanel.add(caractYGraficas);
			}
		} catch (Exception e) {
			// Window.alert("Error: " + e.getMessage() + e.toString());
			System.err.println("Error: " + e.getMessage());
			listPanel.add(new Label(text));
		}
	}
	
	//helpers
	
	private void createWeekChart(final AemetObs ao, Widget ref, Date day) {
		Intervalo end = new Intervalo(day.getYear(), day.getMonth(), day.getDate(), 00, 00);
		Date aWeekEarlier = new Date(day.getTime() - (7 * 24 * 60 * 60 * 1000));
		Intervalo start = new Intervalo(aWeekEarlier.getYear(), aWeekEarlier.getMonth(), aWeekEarlier.getDate(), 23, 59);
		createChart(ao, start, end, ref);
	}

	private void createDayChart(final AemetObs ao, Widget ref, Date day) {
		createChart(ao, new Intervalo(day.getYear(), day.getMonth(), day.getDate(), 00, 00),
				new Intervalo(day.getYear(), day.getMonth(), day.getDate(), 23, 59), ref);
	}

	private void createChart(final AemetObs ao, Intervalo start, Intervalo end, Widget ref) {
		final GetAemetObsForProperty action = new GetAemetObsForProperty();
		setSize(false);
		action.setStationUri(ao.getEstacion().getUri());
		action.setPropertyUri(ao.getPropiedad().getUri());
		action.setStart(start);
		action.setEnd(end);
		display.startProcessing();
		dispatchAsync.execute(action, new AsyncCallback<ListResult<AemetObs>>() {

			@Override
			public void onFailure(Throwable caught) {
				display.stopProcessing();
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(ListResult<AemetObs> result) {
				//disp.stopProcessing();
				List<AemetObs> observations = result.asList();
				if (observations.isEmpty()) {
					return;
				}
				DataPoint[] points = new DataPoint[observations.size()];
				int index = 0;
				for (AemetObs observation : observations) {
					points[index++] = new DataPoint(observation.getIntervalo().asDate().getTime(), observation
							.getValor());
				}
				java.util.Arrays.sort(points, new Comparator<DataPoint>() {
					@Override
					public int compare(DataPoint o1, DataPoint o2) {
						return (int) (o1.getX() - o2.getX());
					}
				});
				PlotWidget plot = createPlot(points, ao.getPropiedad().getDefaultLabel(), ao.getEstacion()
						.getDefaultLabel());

				//infoWindow.close();
				VerticalPanel vp = new VerticalPanel();
				vp.setHorizontalAlignment(vp.ALIGN_CENTER);
				vp.add(plot.getWidget());
				Anchor p = new Anchor(messages.back());
				p.setWidth("50px");
				p.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						graphPanel.clear();
						setSize(true);
							}
				});
				vp.add(p);
				plot.setHeight((int) (windowHeight*0.45));
				plot.setWidth((int) (windowWidth*0.95));
				//vp.setSize("" + (plot.getWidth()) + "px", "" + (plot.getHeight() + 20) + "px");
				graphPanel.clear();
				graphPanel.add(vp);
				display.stopProcessing();
			}

		});
	}

	private PlotWidget createPlot(DataPoint[] points, String property, String station) {
		PlotModel model = new PlotModel();
		PlotOptions plotOptions = new PlotOptions();

		plotOptions.setXAxisOptions(new TimeSeriesAxisOptions());
		SeriesHandler handler = model.addSeries(property + " on " + station, "blue");

		for (DataPoint point : points) {
			handler.add(point);
		}

		return new SimplePlot(model, plotOptions);

	}

	private HorizontalPanel dameUnidadMedicion(String label, String uri, String valor) {
		/**
		 * ? ALT=58.0, --Altitud (m)
		 * 
		 * ? VV10m=5.0, --Velocidad media del viento (m/s) ? QVV10m=0, ---Valor
		 * de Calidad Asignado
		 * 
		 * ? DV10m=202, --Dirección media del viento (grados) ? QDV10m=0,
		 * --Valor de Calidad Asignado ? RVIENTO=30.0, --Recorrido del viento
		 * (Hm) ? QRVIENTO=0, --Valor de Calidad Asignado
		 * 
		 * ? VMAX10m=11.3, --Velocidad máxima del viento (m/s) ? QVMAX10m=0,
		 * --Valor de Calidad Asignado ? DMAX10m=228, --Dirección de la
		 * velocidad máxima del viento (grados) ? QDMAX10m=0, --Valor de Calidad
		 * Asignado
		 * 
		 * ? TA=11.3, --Temperatura del aire (grados Celsius) ? QTA=0, --Valor
		 * de Calidad Asignado
		 * 
		 * ? HR=74,--Humedad relativa (%) ? QHR=0,-- Valor de Calidad Asignado ?
		 * PREC=0.0, --Precipitación (mm) == (litros/m2) ? QPREC=0, --Valor de
		 * Calidad Asignado ? PRES=1005.5, --Presión (hPa) ? QPRES=0, --Valor de
		 * Calidad Asignado ? TPR=6.8, --Temperatura del punto de rocío (grados
		 * Celsius) ? QTPR=0, -- Valor de Calidad Asignado ? PRES_nmar=1013.6,
		 * --Presión reducida al nivel del mar (hPa) ? QPRES_nmar=0 --Valor de
		 * Calidad Asignado
		 */
		HorizontalPanel panelAct = new HorizontalPanel();
		panelAct.setSpacing(5);
		if (label.contains("VV")) {
			// return " m/s";
			panelAct.add(new Anchor(messages.avarageVelocityOfWind()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " m/s"));
		}
		if (label.contains("DV")) {
			panelAct.add(new Anchor(messages.avarageWind()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " grados"));
		}
		if (label.contains("RVIENTO")) {
			panelAct.add(new Anchor(messages.windTour()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " Hm"));
		}
		if (label.contains("DMAX")) {
			panelAct.add(new Anchor(messages.maxWind()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " grados"));
		}
		if (label.contains("VMAX")) {
			panelAct.add(new Anchor(messages.maxWindVelocity()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " m/s"));
		}
		if (label.contains("TA")) {
			panelAct.add(new Anchor(messages.airTemperature()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " grados C."));
		}
		if (label.contains("HR")) {
			panelAct.add(new Anchor(messages.rh()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " %"));
		}
		if (label.contains("PREC")) {
			panelAct.add(new Anchor(messages.precipitation()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " litros/m2"));
		}
		if (label.contains("TPR")) {
			panelAct.add(new Anchor(messages.tempTheDewPoint()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " grados C."));
		}
		if (label.contains("PRES_nmar")) {
			panelAct.add(new Anchor(messages.pressureReducedToSeaLevel()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " hPa"));
		} else if (label.contains("PRES")) {
			panelAct.add(new Anchor("Presion: ", uri, "_blank"));
			panelAct.add(new Label(valor + " hPa"));
		}
		if (label.contains("RAGLOB")) {
			panelAct.add(new Anchor(messages.globalRadiation()+": ", uri, "_blank"));
			panelAct.add(new Label(valor + " KJ/m2"));
		}
		if (label.contains("GEO925")) {
			panelAct.add(new Anchor("GEO925", uri, "_blank"));
			panelAct.add(new Label(valor + "m"));
		}
		if (label.contains("GEO850")) {
			panelAct.add(new Anchor("GEO850", uri, "_blank"));
			panelAct.add(new Label(valor + " m"));
		}
		return panelAct;
	}
	
	private void setSize(Boolean fullList){
		if(fullList) {
			windowWidth =  mapSize.getWidth()*(float)0.68;
			windowHeight =  mapSize.getHeight()*(float)0.68;
			listScrollPanel.setSize(windowWidth+"px", windowHeight+"px");
			graphPanel.setWidth(windowWidth+"px");
			graphPanel.setVisible(false);
		} else {
			listScrollPanel.setHeight(windowHeight*0.5+"px");
			graphPanel.setHeight(windowHeight*0.3+"px");			
			graphPanel.setVisible(true);
		}
	}
}
