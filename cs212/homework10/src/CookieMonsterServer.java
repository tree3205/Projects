import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;



public class CookieMonsterServer 
{

	
	public static void main(String[] args) throws Exception
	{
		Log.setLog(new StdErrLog());
		
		Server server = new Server(8080);
		
		ServletHandler sh = new ServletHandler();
		sh.addServletWithMapping(CookieMonsterServlet.class, "/");
		
        server.setHandler(sh);
		
		server.start();
		server.join();

	}

}
