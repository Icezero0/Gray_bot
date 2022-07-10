package botEvent;

import com.google.gson.internal.LinkedTreeMap;

import IceUtil.Json;

public class PrivateMessage extends Message {
	
	public PrivateMessage(long userID,String content)
	{
		this.senderID=userID;
		this.content=content;
	}
	
	public PrivateMessage(long userID,String userName,String content)
	{
		this.senderID=userID;
		this.content=content;
		this.senderName=userName;
	}
	
	public PrivateMessage(long userID,String userName,String content,long messageID)
	{
		this.senderID=userID;
		this.content=content;
		this.senderName=userName;
		this.messageID=messageID;
	}
	
	public PrivateMessage(Json json)
	{
		this.json=json;
		this.jsonStr=json.toString();
		this.time=((Double) this.json.get("time")).longValue()*1000L;
		this.messageID=((Double) this.json.get("time")).longValue();
		
		this.content=(String) this.json.get("message");
		LinkedTreeMap<?, ?> sender=(LinkedTreeMap<?, ?>) this.json.get("sender");
		this.senderID=((Double) sender.get("user_id")).longValue();
		this.senderName=(String) sender.get("nickname");
	}

	public Types getType() {
		return Types.PrivateMessage;
	}
	
	public String toString()
	{
		return "Event : Private Message\n"
				+ "  - From User : "+this.senderName+" ("+this.senderID+")\n"
				+ "  - Message : "+this.getContent()+"\n";
	}
}
