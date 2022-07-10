package botEvent;

import IceUtil.Json;
import botSession.Session;

public abstract class Message extends Event {
	
	protected String content=null;
	protected String senderName=null;
	protected long senderID=0;
	protected long messageID=0;
	
	public static Message toMessage(Json json)
	{
		String message_type=(String) json.get("message_type");
		if (message_type==null) return null;
		if (message_type.equals("group"))
		{
			return (Message) new GroupMessage(json);
		}
		else if (message_type.equals("private"))
		{
			return (Message) new PrivateMessage(json);
		}
		else return null;
	}
	
	public String getContent()
	{
		return this.content;
	}
	
	public long getSenderID()
	{
		return this.senderID;
	}
	
	public String getSenderName()
	{
		return this.senderName;
	}
	
	public long getMessageID()
	{
		return this.messageID;
	}
	
	public Session getSession()
	{
		return new Session(this);
	}
}
