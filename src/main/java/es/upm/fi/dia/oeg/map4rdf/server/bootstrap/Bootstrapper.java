package es.upm.fi.dia.oeg.map4rdf.server.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import es.upm.fi.dia.oeg.map4rdf.server.conf.Configuration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import es.upm.fi.dia.oeg.map4rdf.server.conf.FacetedBrowserConfiguration;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserActionHandlerModule;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserConfigModule;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserModule;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserServletModule;

/**
 * @author Alexander De Leon
 */
public class Bootstrapper extends GuiceServletContextListener {

	private static final Logger LOG = Logger.getLogger(Bootstrapper.class.getName());

	private Configuration config;
	private FacetedBrowserConfiguration facetedBrowserConfiguration;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		InputStream propIn = servletContextEvent.getServletContext().getResourceAsStream(Constants.CONFIGURATION_FILE);
		try {
			config = new Configuration(propIn);
			// add config to servlet context so it can be accessed in JSPs
			servletContextEvent.getServletContext().setAttribute(Configuration.class.getName(), config);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Unable to load configuration file", e);
			throw new RuntimeException(e);
		}

		InputStream facetConfigIn = servletContextEvent.getServletContext().getResourceAsStream(
				Constants.FACET_CONFIGURATION_FILE);
		try {
			facetedBrowserConfiguration = new FacetedBrowserConfiguration(facetConfigIn);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Unable to load faceted broser configuration file", e);
			throw new RuntimeException(e);
		}
		super.contextInitialized(servletContextEvent);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().removeAttribute(Configuration.class.getName());
	}

	@Override
	protected Injector getInjector() {

		return Guice.createInjector(new BrowserModule(), new BrowserConfigModule(config, facetedBrowserConfiguration),
				new BrowserServletModule(), new BrowserActionHandlerModule());
	}

}
