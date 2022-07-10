package botEvent;
import IceUtil.Json;


public abstract class Event {
	
	public static enum Types{EmptyEvent,HeartBeat,PrivateMessage,GroupMessage,BotStop,GroupForwardMessage,BotMessage};
	protected String jsonStr;
	protected Json json;
	protected long time;
	
	public static Event toEvent(Json json)
	{
		if (json==null || json.toString().equals(""))
		{
			return (Event) new EmptyEvent();
		}
		String type=null;
		type=(String) json.get("meta_event_type");
		if (type!=null && type.equals("heartbeat"))
			return (Event) new HeartBeat(json);
		type=(String) json.get("post_type");
		if (type!=null && type.equals("message"))
			return (Event) Message.toMessage(json);
		return (Event) new EmptyEvent();
	}
	
	public abstract Types getType();
	
	public Json getJson()
	{
		return this.json;
	}
	
	public String getJsonStr()
	{
		return this.jsonStr;
	}

	public long getTime()
	{
		return this.time;
	}
	
	public GroupMessage toGroupMessage()
	{
		if (this.getType()==Types.GroupMessage)
			return (GroupMessage) this;
		else return null;
	}
	
	public PrivateMessage toPrivateMessage()
	{
		if (this.getType()==Types.PrivateMessage)
			return (PrivateMessage) this;
		else return null;
	}
}
