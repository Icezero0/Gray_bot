package botPluginAPI;

import java.util.List;
import java.util.Map;

import botEvent.Message;
import botSession.Session;

public interface BotAPI {
	
	public void sendPrivateMsg(long userID,String content);
	public void sendGroupMsg(long groupID,String content);
	public void sendMessage(Session session,String content);
	public void sendGroupForwardMsg(long group_id,List<Message> nodes);
	public void reply(Message msg,String content);
	public String getRuntimePath(long pluginID);
	public String getResourcePath(long pluginID);
	public long getBotID();
	public String getBotName();
	public static void display(Object obj)
	{
		System.out.println(obj.toString());
	}
}
