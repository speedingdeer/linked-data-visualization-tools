/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.Display;
import ca.nanometrics.gflot.client.DataPoint;
import ca.nanometrics.gflot.client.PlotModel;
import ca.nanometrics.gflot.client.PlotWidget;
import ca.nanometrics.gflot.client.SeriesHandler;
import ca.nanometrics.gflot.client.SimplePlot;
import ca.nanometrics.gflot.client.options.PlotOptions;
import ca.nanometrics.gflot.client.options.TimeSeriesAxisOptions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetAemetObsForProperty;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.share.AemetObs;
import es.upm.fi.dia.oeg.map4rdf.share.AemetResource;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Intervalo;

/**
 * 
 * @author Daniel Garijo
 */
public class DrawAemetResourcesOnClick extends DrawGeoResourceOnClick {

	DispatchAsync dispatchAsync;
	VerticalPanel mainPanel = new VerticalPanel();
	ScrollPanel sp = new ScrollPanel(mainPanel);

	public DrawAemetResourcesOnClick(String uri, final Display disp, GeoResourcesMapControl g, LatLng latLong,
			DispatchAsync dispatchAsync) {
		super(uri, disp, g, latLong);
		this.dispatchAsync = dispatchAsync;
	}

	@Override
	public void draw(SingletonResult<GeoResource> result) {

		HorizontalPanel caractYGraficas = new HorizontalPanel();
		VerticalPanel caracteristicas = new VerticalPanel();
		VerticalPanel graficas = new VerticalPanel();
		caracteristicas.setSpacing(0);
		graficas.setSpacing(0);
		String text = "No se han obtenido datos de observacion reciente";
		try {
			AemetResource ae = (AemetResource) result.getValue();
			// if(ae==null)return;
			ArrayList<AemetObs> obs = ae.getObs();
			if (obs.isEmpty()) {
				mainPanel.add(new Label(text));
			} else {
				final AemetObs firstObservation = obs.get(0);
				mainPanel.add(new Label("Estacion " + firstObservation.getEstacion().getDefaultLabel()));
				mainPanel.add(new Label(firstObservation.getIntervalo().toString()));
				for (final AemetObs observation : obs) {
					HorizontalPanel obsActual = dameUnidadMedicion(observation.getPropiedad().getDefaultLabel(),
							observation.getUriObs(), Double.toString(observation.getValor()));
					caracteristicas.add(obsActual);

					HorizontalPanel graf = new HorizontalPanel();
					graf.setSpacing(5);
					// Graficas de alex aqui
					Anchor dayAnchor = new Anchor("dia");
					dayAnchor.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							createDayChart(observation, sp, firstObservation.getIntervalo().asDate());
						}
					});
					graf.add(dayAnchor);
					graf.add(new Label("|"));
					Anchor weekAnchor = new Anchor("semana");
					weekAnchor.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							createWeekChart(observation, sp, firstObservation.getIntervalo().asDate());
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
				mainPanel.add(caractYGraficas);
			}
			sp.setSize("350px", "350px");
		} catch (Exception e) {
			// Window.alert("Error: " + e.getMessage() + e.toString());
			System.err.println("Error: " + e.getMessage());
			mainPanel.add(new Label(text));
			sp.setSize("310px", "20px");
		}
		infoWindow.open(latl, new InfoWindowContent(sp));
	}

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
		action.setStationUri(ao.getEstacion().getUri());
		action.setPropertyUri(ao.getPropiedad().getUri());
		action.setStart(start);
		action.setEnd(end);
		final Widget lastPannel = ref;
		disp.startProcessing();
		dispatchAsync.execute(action, new AsyncCallback<ListResult<AemetObs>>() {

			@Override
			public void onFailure(Throwable caught) {
				disp.stopProcessing();
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(ListResult<AemetObs> result) {
				disp.stopProcessing();
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

				infoWindow.close();
				VerticalPanel vp = new VerticalPanel();
				vp.setHorizontalAlignment(vp.ALIGN_CENTER);
				vp.add(plot.getWidget());
				PushButton p = new PushButton("Volver");
				p.setWidth("50px");
				p.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						infoWindow.close();
						infoWindow.open(latl, new InfoWindowContent(lastPannel));
					}
				});
				vp.add(p);
				vp.setSize("" + (plot.getWidth()) + "px", "" + (plot.getHeight() + 20) + "px");
				infoWindow.open(latl, new InfoWindowContent(vp));

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
			panelAct.add(new Anchor("Vel. media del viento: ", uri, "_blank"));
			panelAct.add(new Label(valor + " m/s"));
		}
		if (label.contains("DV")) {
			panelAct.add(new Anchor("Dir. media del viento: ", uri, "_blank"));
			panelAct.add(new Label(valor + " grados"));
		}
		if (label.contains("RVIENTO")) {
			panelAct.add(new Anchor("Recorrido del viento: ", uri, "_blank"));
			panelAct.add(new Label(valor + " Hm"));
		}
		if (label.contains("DMAX")) {
			panelAct.add(new Anchor("Dir. de la v. max. del viento: ", uri, "_blank"));
			panelAct.add(new Label(valor + " grados"));
		}
		if (label.contains("VMAX")) {
			panelAct.add(new Anchor("Vel. max. del viento: ", uri, "_blank"));
			panelAct.add(new Label(valor + " m/s"));
		}
		if (label.contains("TA")) {
			panelAct.add(new Anchor("Temperatura del aire: ", uri, "_blank"));
			panelAct.add(new Label(valor + " grados C."));
		}
		if (label.contains("HR")) {
			panelAct.add(new Anchor("Humedad relativa: ", uri, "_blank"));
			panelAct.add(new Label(valor + " %"));
		}
		if (label.contains("PREC")) {
			panelAct.add(new Anchor("Precipitacion: ", uri, "_blank"));
			panelAct.add(new Label(valor + " litros/m2"));
		}
		if (label.contains("TPR")) {
			panelAct.add(new Anchor("Temp. del pto. de rocio: ", uri, "_blank"));
			panelAct.add(new Label(valor + " grados C."));
		}
		if (label.contains("PRES_nmar")) {
			panelAct.add(new Anchor("Pres. reducida al nivel del mar: ", uri, "_blank"));
			panelAct.add(new Label(valor + " hPa"));
		} else if (label.contains("PRES")) {
			panelAct.add(new Anchor("Presion: ", uri, "_blank"));
			panelAct.add(new Label(valor + " hPa"));
		}
		if (label.contains("RAGLOB")) {
			panelAct.add(new Anchor("Radiacion global", uri, "_blank"));
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
}
