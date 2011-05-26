/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.client.widget.mapcontrol;

import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import java.util.ArrayList;
import java.util.Comparator;
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
		String text = "No se han obtenidos datos de observacion reciente";
		try {
			AemetResource ae = (AemetResource) result.getValue();
			// if(ae==null)return;
			ArrayList<AemetObs> obs = ae.getObs();
			if (obs.isEmpty()) {
				mainPanel.add(new Label(text));
			} else {
				AemetObs firstObservation = obs.get(0);
				mainPanel.add(new Label("Estacion " + firstObservation.getEstacion().getDefaultLabel()));
				mainPanel.add(new Label(firstObservation.getIntervalo().toString()));
				for (final AemetObs obsevation : obs) {
					HorizontalPanel obsActual = new HorizontalPanel();
					obsActual.setSpacing(5);
					obsActual.add(new Anchor(obsevation.getPropiedad().getDefaultLabel(), obsevation.getUriObs(),
							"_blank"));
					obsActual.add(new Label(Double.toString(obsevation.getValor())+dameUnidadMedicion(obsevation.getPropiedad().getDefaultLabel())));
					caracteristicas.add(obsActual);

					HorizontalPanel graf = new HorizontalPanel();
					graf.setSpacing(5);
					// Graficas de alex aqui
					Anchor dayAnchor = new Anchor("dia");
					dayAnchor.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							createDayChart(obsevation,sp);
						}
					});
					graf.add(dayAnchor);
					graf.add(new Label("|"));
                                        Anchor weekAnchor = new Anchor("semana");
					weekAnchor.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							Window.alert("Por hacer");
						}
					});
					graf.add(weekAnchor);
					graf.add(new Label("|"));
                                        Anchor monthAnchor = new Anchor("mes");
					monthAnchor.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							Window.alert("Por hacer");
						}
					});
					graf.add(monthAnchor);
					graficas.add(graf);
				}
				caractYGraficas.add(caracteristicas);
				caractYGraficas.add(graficas);
				mainPanel.add(caractYGraficas);
			}
                    sp.setSize("300px", "350px");
		} catch (Exception e) {
			//Window.alert("Error: " + e.getMessage() + e.toString());
			System.err.println("Error: " + e.getMessage());
			mainPanel.add(new Label(text));
                        sp.setSize("310px", "20px");
		}		
		infoWindow.open(latl, new InfoWindowContent(sp));
	}

	private void createDayChart(final AemetObs ao, Widget ref) {
		final GetAemetObsForProperty action = new GetAemetObsForProperty();
		action.setStationUri(ao.getEstacion().getUri());
		action.setPropertyUri(ao.getPropiedad().getUri());
		action.setStart(new Intervalo(2011, 05, 12, 00, 00));
		action.setEnd(new Intervalo(2011, 05, 12, 23, 59));
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
                                vp.setSize(""+(plot.getWidth())+"px",""+(plot.getHeight()+20)+"px");
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

    private String dameUnidadMedicion(String label) {
        if(label.contains("TPR")){return " %";}
        if(label.contains("PRES")){return " hPa";}
        if(label.contains("HR")){return " grados C";}
        if(label.contains("TA")){return " %";}
        if(label.contains("DMAX")){return "m/s";}
        if(label.contains("VMAX")){return " grados";}
        if(label.contains("RVIENTO")){return "";}
        if(label.contains("DV")){return " grados";}
        if(label.contains("VV")){return " m/s";}
        return "";
    }
}
