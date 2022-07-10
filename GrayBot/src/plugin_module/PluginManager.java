package plugin_module;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import IceUtil.IceJar;
import api_module.BotOperator;
import botEvent.Event;
import botEvent.Message;
import botPluginAPI.BotPlugin;
import botSession.Session;
import kernel.Bot;

public class PluginManager {
	
	private String pluginHome=null;
	private BotOperator botOperator=null;
	private HashMap<Long,Class<?>> pluginClasses=null;
	private HashMap<Long,String> pluginRuntimePath=null;
	private HashMap<Long,String> pluginResourcePath=null;
	private ArrayList<BotPlugin> globalPlugins=null;
	private HashMap<Session,ArrayList<BotPlugin>> sessionalPlugins=null;
	
	public PluginManager(String pluginHome,BotOperator botOperator,HashMap<Long,Class<?>> pluginClasses,
			ArrayList<BotPlugin> globalPlugins,HashMap<Session,ArrayList<BotPlugin>> sessionalPlugins,
			HashMap<Long,String> pluginRuntimePath,HashMap<Long,String> pluginResourcePath) throws IOException
	{
		if (!pluginHome.endsWith("\\") && !(pluginHome.endsWith("/"))) pluginHome=pluginHome+File.separator;
		this.pluginHome=pluginHome;
		File homeFile=new File(pluginHome);
		if (!homeFile.exists()) homeFile.mkdir();
		
		this.botOperator=botOperator;
		this.pluginClasses=pluginClasses;
		this.globalPlugins=globalPlugins;
		this.sessionalPlugins=sessionalPlugins;
		this.pluginRuntimePath=pluginRuntimePath;
		this.pluginResourcePath=pluginResourcePath;
		
		this.botOperator.setPluginManager(this);
	}
	
	@SuppressWarnings("deprecation")
	public void loadPlugins() throws IOException
	{
		File homeFile=new File(this.pluginHome);
		File[] files=homeFile.listFiles();
		for (File file : files)
		{
			if (file.isFile() && file.getName().endsWith(".jar"))
			{
				String jarFileName=file.getName().substring(0,file.getName().length()-4);
				Class<?>[] fileClasses=IceJar.loadAllClass(file);
				IceJar.unzipJar(this.pluginHome+jarFileName, file.getAbsolutePath(), "resource");
				String resourcePath=this.pluginHome+jarFileName+File.separator+"resource";
				for (Class<?> Clazz : fileClasses) 
				{
					Object obj=null;
					try {
						obj= Clazz.newInstance();
					} catch (InstantiationException | IllegalAccessException e1) {
					}
					if (obj!=null && obj instanceof BotPlugin)
					{
						
						BotPlugin pluginins=(BotPlugin) obj;
						
						Long pluginID=pluginins.getPluginID();
						String pluginName=pluginins.getPluginName();
						if (this.pluginClasses.containsKey(pluginID))
						{
							Bot.Debug("Load Plugin \""+pluginName+"\" failed : Plugin ID conflict.");
							continue;
						}
						
						if (pluginins.getType()==BotPlugin.Types.Global)
							this.globalPlugins.add(pluginins);
						else 
							this.pluginClasses.put(pluginID, Clazz);
						File newJarFolder=new File(this.pluginHome+jarFileName);
						if (!newJarFolder.exists()) newJarFolder.mkdir();
						String runtimePath=this.pluginHome+jarFileName;
						
						File readmeFile=new File(this.pluginHome+jarFileName+File.separator+pluginID+".txt");
						PrintWriter opt=new PrintWriter(readmeFile);
						opt.println("## GrayBot Plugin\nID : "+pluginID+"\nName : "+pluginName+"\nDescription : "+pluginins.getPluginDescription());
						opt.flush();
						opt.close();
						
						this.pluginRuntimePath.put(pluginID, runtimePath);
						this.pluginResourcePath.put(pluginID, resourcePath);
						Bot.Debug("Load Plugin Succeeded : \""+pluginName+"\".");
					}
				}
			}
		}
	}
	
	public void doGlobalPlugin(Event e)
	{
		for (BotPlugin plugin:this.globalPlugins)
		{
			plugin.deal(e, this.botOperator);
		}
	}
	
	public void doSessionalPlugin(Event e)
	{
		if (e.getType()==Event.Types.PrivateMessage || e.getType()==Event.Types.GroupMessage)
		{
			Session session=new Session((Message) e);
			ArrayList<BotPlugin> pluginlist=null;
			pluginlist=this.sessionalPlugins.get(session);
			if (pluginlist==null) 
			{
				createNewSessionPlugins(session);
				pluginlist=this.sessionalPlugins.get(session);
			}
			if (pluginlist!=null)
			{
				for (BotPlugin plugin:pluginlist)
				{
					plugin.deal(e, this.botOperator);
				}
			}	
		}else
		{
			for (Entry<Session, ArrayList<BotPlugin>> entry : this.sessionalPlugins.entrySet())
			{
				ArrayList<BotPlugin> pluginlist=entry.getValue();
				for (BotPlugin plugin:pluginlist)
				{
					plugin.deal(e, botOperator);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void createNewSessionPlugins(Session session)
	{
		ArrayList<BotPlugin> pluginlist=null;
		pluginlist=this.sessionalPlugins.get(session);
		if (pluginlist==null)
		{
			pluginlist=new ArrayList<BotPlugin>();	
			BotPlugin newPluginInstance;
			for (Entry<Long,Class<?>> entry : this.pluginClasses.entrySet())
			{
				Class<?> Clazz=(Class<?>) entry.getValue();
				newPluginInstance=null;
				try {
					newPluginInstance=(BotPlugin) Clazz.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
				if (newPluginInstance!=null && newPluginInstance.getType()==BotPlugin.Types.Sessional)
					pluginlist.add(newPluginInstance);
			}
			this.sessionalPlugins.put(session, pluginlist);
		}
		else return;
	}
	
	public String getRuntimePath(long pluginID)
	{
		return this.pluginRuntimePath.get(pluginID);
	}
	
	public String getResourcePath(long pluginID)
	{
		return this.pluginResourcePath.get(pluginID);
	}
	
}
