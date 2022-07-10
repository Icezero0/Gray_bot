package kernel;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import IceUtil.Commander;
import IceUtil.PropertiesConfiger;
import api_module.BotOperator;
import botEvent.BotStop;
import botEvent.Event;
import botPluginAPI.BotPlugin;
import botSession.Session;
import comm_module.BotHttpSender;
import comm_module.BotHttpServer;
import plugin_module.PluginManager;



public class Bot {

	private String BotHome=IceUtil.Func.currentPath();
	private final String configPath="config.properties";
	
	private final String serverIP="127.0.0.1";
	private String gocqhttpIP="127.0.0.1";
	private int serverPort=5701;
	private int gocqhttpPort=5700;
	private long botID=1;
	private String botName="Gray";
	
	private String PluginsFolderPath=null;
	
	private boolean alive=false;
	
	private PropertiesConfiger configer=null;
	
	private BotHttpServer httpServer=null;
	private BlockingQueue<Event> reportList=null;
	
	private BotHttpSender httpSender=null;
	private BlockingQueue<Event> sendList=null;
	
	private Commander cmder=null;
	
	private BotOperator botOperator=null;
	
	private PluginManager pluginManager=null;
	private HashMap<Long,Class<?>> pluginClasses=null;
	private ArrayList<BotPlugin> globalPlugins=null;
	private HashMap<Session,ArrayList<BotPlugin>> sessionalPlugins=null;
	private HashMap<Long,String> pluginRuntimePath=null;
	private HashMap<Long,String> pluginResourcePath=null;
	
	public static void main(String[] args) throws IOException {
		
		Bot gray=new Bot();
		gray.run();
	}
	
	Bot() throws IOException
	{
		this.setConfig();
		this.createServer();
		this.createSender();
		this.createCmder();
		this.createOperator();
		this.createPluginManager();
		
	}

	public void run() throws IOException
	{
		Bot.Debug("starting...");
		this.httpServer.start();
		this.httpSender.start();
		this.cmder.start();
		
		this.pluginManager.loadPlugins();
		//开启各功能线程
		Bot.Debug("Bot : "+this.botName+" is running...");
		
		this.alive=true;
		
		while (this.alive)
		{
			Event newEvent=null;
			try {
				newEvent=this.reportList.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			
			if (newEvent==null) continue;
			if (newEvent.getType()!=Event.Types.HeartBeat)
				Bot.Debug(newEvent);
			
			this.pluginManager.doGlobalPlugin(newEvent);
			this.pluginManager.doSessionalPlugin(newEvent);
			
		}
		
		this.httpSender.close();
		try {
			this.httpSender.thread.join();
		} catch (InterruptedException e) {
		}
		
		this.httpServer.close();
		
		this.cmder.stop();
		Bot.Debug("Bot stopped.");
		Bot.Debug("Press any key to continue.");
		try {
			this.cmder.thread.join();
		} catch (InterruptedException e) {
		}
	}
	
	public void stop()
	{
		this.alive=false;
		try {
			this.reportList.put(new BotStop());
		} catch (InterruptedException e) {
		}
	}
	
	private void setConfig() throws IOException
	{
		this.configer=new PropertiesConfiger(this.configPath);
		this.configer.load();
		
		this.gocqhttpPort=Integer.valueOf(this.configer.getProperty("Go-cqhttp-Server-Port", "5700"));
		this.configer.setProperty("Go-cqhttp-Server-Port", String.valueOf(this.gocqhttpPort));
		
		this.serverPort=Integer.valueOf(this.configer.getProperty("Event-Report-Port", "5701"));
		this.configer.setProperty("Event-Report-Port", String.valueOf(this.serverPort));
		
		this.gocqhttpIP=this.configer.getProperty("Go-cqhttp-Server-Address", "127.0.0.1");
		this.configer.setProperty("Go-cqhttp-Server-Address", this.gocqhttpIP);
		
		this.PluginsFolderPath=this.configer.getProperty("Plugins-Folder", "plugins");
		this.configer.setProperty("Plugins-Folder", this.PluginsFolderPath);
		
		String botID="1";
		botID=this.configer.getProperty("QQ_id", "1");
		this.configer.setProperty("QQ_id", botID);
		try{
			this.botID=Long.valueOf(botID);
		}catch (NumberFormatException e)
		{
			this.botID=1;
		}
		
		this.botName=this.configer.getProperty("Bot_name", "Gray");
		this.configer.setProperty("Bot_name", this.botName);
		
		this.configer.save("GrayBot config File");
	}
	
	private void createServer() throws IOException
	{
		this.reportList=new LinkedBlockingQueue<Event>();
		this.httpServer=new BotHttpServer(new InetSocketAddress(this.serverIP,this.serverPort), this.reportList);
	}
	
	private void createSender() throws IOException
	{
		this.sendList=new LinkedBlockingQueue<Event>();
		this.httpSender=new BotHttpSender("http://"+this.gocqhttpIP+":"+this.gocqhttpPort,this.sendList,this.reportList);
	}
	
	private void createCmder()
	{
		this.cmder=new Commander();
		this.cmder.setCmdIgnoreCase(true);
		this.cmder.addCmd("STOP", new Runnable() {

			public void run() {
				stop();
			}});
	}
	
	private void createOperator()
	{
		this.botOperator=new BotOperator(sendList);
		this.botOperator.setBotInfo(botID, botName);
	}
	
	private void createPluginManager() throws IOException 
	{
		this.pluginClasses=new HashMap<Long,Class<?>>();
		this.globalPlugins=new ArrayList<BotPlugin>();
		this.sessionalPlugins=new HashMap<Session,ArrayList<BotPlugin>>();
		this.pluginRuntimePath=new HashMap<Long,String>();
		this.pluginResourcePath=new HashMap<Long,String>();
		this.pluginManager=new PluginManager(this.BotHome+File.separator+this.PluginsFolderPath+File.separator,
												this.botOperator,this.pluginClasses,this.globalPlugins,
												this.sessionalPlugins,this.pluginRuntimePath,this.pluginResourcePath);
		
	}
	
	static public void Debug(Object obj)
	{
		System.out.println("[Bot] "+obj.toString());
	}
	
	public long getBotID()
	{
		return Long.valueOf(this.botID);
	}
	
}
