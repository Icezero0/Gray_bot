package botEvent;

public class BotMessage extends Message{

	private long userID;
	private long groupID;
	
	public BotMessage(Message msg,long messageID)
	{
		this.messageID=messageID;
		switch (msg.getType())
		{
		case PrivateMessage:
			PrivateMessage pmsg=(PrivateMessage)msg;
			this.userID=msg.getSenderID();
			this.groupID=-1;
			this.senderID=0;
			this.content=pmsg.content;
			break;
		case GroupMessage:
			GroupMessage gmsg=(GroupMessage)msg;
			this.userID=-1;
			this.content=gmsg.content;
			this.groupID=gmsg.getGroupID();
			this.senderID=0;
			break;
		case GroupForwardMessage:
			GroupForwardMessage gfmsg=(GroupForwardMessage)msg;
			this.userID=-1;
			this.groupID=gfmsg.getGroupID();
			this.senderID=0;
			this.content="[合并消息]";
			break;
		default:
			return;
		}
	}
	
	@Override
	public Types getType() {
		return Types.BotMessage;
	}
	
	public long getUserID()
	{
		return this.userID;
	}
	
	public long getGroupID()
	{
		return this.groupID;
	}
	
	public boolean isGroupMessage()
	{
		return this.groupID!=-1;
	}

	public String toString()
	{
		StringBuilder s=new StringBuilder("Event : Bot Sent Message\n");
		if (this.isGroupMessage())
			s.append("  - To Group : ").append(this.groupID).append("\n");
		else
			s.append("  - To User : ").append(this.userID).append("\n");
		s.append("  - Message : ").append(this.getContent()).append("\n");
		return s.toString();
	}
}
