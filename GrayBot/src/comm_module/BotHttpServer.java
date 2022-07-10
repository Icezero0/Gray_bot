package comm_module;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import IceUtil.*;
import botEvent.Event;



public class BotHttpServer{
	
	private HttpServer httpServer=null;
	private BlockingQueue<Event> reportList=null;
	
	public BotHttpServer(InetSocketAddress address,BlockingQueue<Event> reportList) throws IOException
	{
		this.httpServer=HttpServer.create(address,0);
		
		this.reportList=reportList;
		
		initialContext();
	}
	
	protected void initialContext()
	{
		this.httpServer.createContext("/report",new EventReportHandler());
		this.httpServer.createContext("/test",new BotRunningTestHandler());
	}
	
	public void start()
	{
		this.httpServer.start();
	}
	
	public void close()
	{
		this.httpServer.stop(0);
	}
	
	private class EventReportHandler implements HttpHandler
	{
		
		public void handle(HttpExchange httpExchange) throws IOException 
		{
			String method=httpExchange.getRequestMethod();
			
			if (method.equals("POST"))
			{
				InputStreamReader ipt=new InputStreamReader(httpExchange.getRequestBody(),"UTF-8");
				
		        String content=Func.readAllAndClose(ipt);
				try {
					BotHttpServer.this.reportList.put(Event.toEvent(new Json(content)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
		        httpExchange.sendResponseHeaders(200, 0);
		        httpExchange.close();
			}
	    }
		
	}
	
	private class BotRunningTestHandler implements HttpHandler
	{
		public void handle(HttpExchange httpExchange) throws IOException {
			
			String method=httpExchange.getRequestMethod();
			
			if (method.equals("GET"))
			{
				httpExchange.sendResponseHeaders(200, 0);
				OutputStream opt=httpExchange.getResponseBody();
				opt.write("Bot is Running.".getBytes("UTF-8"));
				opt.close();
			}
			
		}
		
	}
	
	
}
