package com.github.trecloux.flashcookie.web;

import net.sourceforge.jwebunit.junit.WebTester;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class IntegrationTest extends WebTester {
	
	private static final String WEBAPP = "src/main/webapp/";
	private static final String CONTEXTPATH = "/";

	
	@Test
	public void shouldShowFlashAttributeAfterRedirect() throws Exception {
		beginAt("/");
		assertTextNotPresent("It works");
		clickLinkWithExactText("Set Flash Attribute");
		assertTextPresent("It works");
	}

	@Test
	public void shouldShowFlashAttributeAfterRedirectAndDeleteAfterRefresh() throws Exception {
		beginAt("/");
		clickLinkWithExactText("Set Flash Attribute");
		assertTextPresent("It works");
		clickLinkWithExactText("Refresh");
		assertTextNotPresent("It works");
	}
	
	
	
	
	@Before
	public void startSever() throws Exception {
		Server server = new Server();
		Connector connector = new SelectChannelConnector();
		connector.setPort(8080);
		connector.setHost("0.0.0.0");
		server.addConnector(connector);
	 
		WebAppContext wac = new WebAppContext();
		wac.setContextPath(CONTEXTPATH);
		wac.setWar(WEBAPP);
		
		server.setHandler(wac);
		server.setStopAtShutdown(true);

		server.start();
	}

}
