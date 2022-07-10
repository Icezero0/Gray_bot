package botEvent;

import com.google.gson.internal.LinkedTreeMap;

import IceUtil.Json;

public class GroupMessage extends Message {
	
	private long groupID=0;
	private String senderCard=null;
	
	public GroupMessage(long groupID,String content)
	{
		this.groupID=groupID;
		this.content=content;
	}
	
	public GroupMessage(long groupID,String content,long messageID)
	{
		this.groupID=groupID;
		this.content=content;
		this.messageID=messageID;
	}
	
	public GroupMessage(Json json)
	{
		this.json=json;
		this.jsonStr=json.toString();
		this.time=((Double) this.json.get("time")).longValue()*1000L;
		this.messageID=((Double) this.json.get("time")).longValue();
		
		this.content=(String) this.json.get("message");
		LinkedTreeMap<?, ?> sender=(LinkedTreeMap<?, ?>) this.json.get("sender");
		this.senderID=((Double) sender.get("user_id")).longValue();
		this.senderName=(String) sender.get("nickname");
		
		this.groupID=((Double) this.json.get("group_id")).longValue();
		this.senderCard=(String) sender.get("card");
	}
	
	public long getGroupID()
	{
		return this.groupID;
	}
	
	public String getSenderCard()
	{
		return this.senderCard;
	}
	
	public Types getType() {
		return Types.GroupMessage;
	}
	
	public String toString()
	{
		return "Event : Group Message\n"
				+ "  - From Group : "+this.groupID+"\n"
				+ "  - From User : "+this.senderCard+" ("+this.senderID+")\n"
				+ "  - Message : "+this.getContent()+"\n";
	}
}
