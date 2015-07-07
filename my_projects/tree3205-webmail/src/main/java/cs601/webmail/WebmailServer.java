package cs601.webmail;

import cs601.webmail.managers.SearchService;
import cs601.webmail.pages.DispatchServlet;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class WebmailServer {
	public static void main(String[] args) throws Exception {
        String staticFilesDir = ".";
        String logDir = "./var/log/webmail";

        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(WebmailServer.class.getResource(
                "/keystore.jks").toExternalForm());
        sslContextFactory.setKeyStorePassword("tree3205");
        sslContextFactory.setKeyManagerPassword("3205tree");

        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        sslConnector.setPort(8081);

        server.setConnectors(new Connector[] { connector, sslConnector });

		ServletContextHandler context = new
		            ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

        // add a simple Servlet at "/dynamic/*"
        ServletHolder holderDynamic = new ServletHolder("dynamic", DispatchServlet.class);
		context.addServlet(holderDynamic, "/*");

        // add special pathspec of "/home/" content mapped to the homePath
        ServletHolder holderHome = new ServletHolder("static-home", DefaultServlet.class);
        holderHome.setInitParameter("resourceBase", staticFilesDir);
        holderHome.setInitParameter("dirAllowed","true");
        holderHome.setInitParameter("pathInfoOnly","true");
		context.addServlet(holderHome, "/files/*");

        // Lastly, the default servlet for root content (always needed, to satisfy servlet spec)
        // It is important that this is last.
        ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);
        holderPwd.setInitParameter("resourceBase","/tmp/foo");
        holderPwd.setInitParameter("dirAllowed","true");
		context.addServlet(holderPwd, "/");

		// log using NCSA (common log format)
		// http://en.wikipedia.org/wiki/Common_Log_Format
		NCSARequestLog requestLog = new NCSARequestLog();
		requestLog.setFilename(logDir + "/yyyy_mm_dd.request.log");
		requestLog.setFilenameDateFormat("yyyy_MM_dd");
		requestLog.setRetainDays(90);
		requestLog.setAppend(true);
		requestLog.setExtended(true);
		requestLog.setLogCookies(false);
		requestLog.setLogTimeZone("GMT");
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		requestLogHandler.setRequestLog(requestLog);
		requestLogHandler.setServer(server);

        HandlerCollection handlers=new HandlerCollection();
        server.setHandler(handlers);
        handlers.addHandler(context);
        handlers.addHandler(requestLogHandler);
		server.start();
        // Start the search service at the beginning
        new SearchService().getDir();
        new SearchService().createSearchData();
		server.join();
	}
}
