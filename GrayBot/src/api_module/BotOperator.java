package api_module;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import botEvent.Event;
import botEvent.GroupForwardMessage;
import botEvent.GroupMessage;
import botEvent.Message;
import botEvent.PrivateMessage;
import botPluginAPI.BotAPI;
import botSession.Session;
import plugin_module.PluginManager;

public class BotOperator implements BotAPI{
	
	private BlockingQueue<Event> sendList=null;
	private PluginManager pluginManager=null;
	private long BotID;
	private String BotName;
	
	public BotOperator(BlockingQueue<Event> sendList)
	{
		this.sendList=sendList;
	}
	
	public void setPluginManager(PluginManager pluginManager)
	{
		this.pluginManager=pluginManager;
	}
	
	public void setBotInfo(long BotID,String BotName)
	{
		this.BotID=BotID;
		this.BotName=BotName;
	}
	
	@Override
	public void sendPrivateMsg(long userID,String content)
	{
		PrivateMessage pmsg=new PrivateMessage(userID,content);
		try {
			this.sendList.put(pmsg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendGroupMsg(long groupID,String content)
	{
		GroupMessage gmsg=new GroupMessage(groupID,content);
		try {
			this.sendList.put(gmsg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendGroupForwardMsg(long groupID, List<Message> nodes) {
		GroupForwardMessage gfmsg=new GroupForwardMessage(groupID, nodes);
		try {
			this.sendList.put(gfmsg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendMessage(Session session,String content)
	{
		if (session.getType()==Session.Types.PrivateSession)
		{
			sendPrivateMsg(session.getID(),content);
		}
		else if (session.getType()==Session.Types.GroupSession)
		{
			sendGroupMsg(session.getID(),content);
		}
	}
	
	@Override
	public void reply(Message msg,String content)
	{
		if (msg.getType()==Event.Types.PrivateMessage)
			sendPrivateMsg(msg.getSenderID(),content);
		else
			sendGroupMsg(((GroupMessage)msg).getGroupID(),content);
	}

	@Override
	public String getRuntimePath(long pluginID) {
		return this.pluginManager.getRuntimePath(pluginID);
	}

	@Override
	public String getResourcePath(long pluginID) {
		return this.pluginManager.getResourcePath(pluginID);
	}

	@Override
	public long getBotID() {
		return this.BotID;
	}

	@Override
	public String getBotName() {
		return this.BotName;
	}

}
