package botSession;

import botEvent.*;

public class Session {

	public static enum Types {PrivateSession,GroupSession};
	
	protected long ID=0;
	protected Types type;
	
	public Session(Message msg)
	{
		if (msg.getType()==Message.Types.PrivateMessage)
		{
			this.ID=msg.getSenderID();
			this.type=Types.PrivateSession;
		}
		else if (msg.getType()==Message.Types.GroupMessage)
		{
			this.ID=((GroupMessage)msg).getGroupID();
			this.type=Types.PrivateSession;
		}
	}
	
	public long getID()
	{
		return this.ID;
	}
	
	public Types getType()
	{
		return this.type;
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof Session)
		{
			Session tmp=(Session)obj;
			if (this.ID==tmp.ID && this.type==tmp.type)
				return true;
		}
		return false;
	}
	public int hashCode()
	{
		return Long.hashCode(ID*10+type.ordinal());
	}
}
