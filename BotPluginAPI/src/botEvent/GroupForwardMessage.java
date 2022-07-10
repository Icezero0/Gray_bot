package botEvent;

import java.util.List;


//for send only
public class GroupForwardMessage extends Message{

	
	private long groupID=0;
	private List<Message> nodes;
	
	public GroupForwardMessage(long  groupID,List<Message> nodes)
	{
		this.groupID=groupID;
		this.nodes=nodes;
	}
	
	@Override
	public Types getType() {
		return Types.GroupForwardMessage;
	}
	
	public long getGroupID()
	{
		return this.groupID;
	}
	
	public List<Message> getNodes()
	{
		return this.nodes;
	}

}
