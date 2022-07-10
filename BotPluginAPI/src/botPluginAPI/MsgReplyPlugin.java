package botPluginAPI;

import botEvent.Event;
import botEvent.Message;

public abstract class MsgReplyPlugin implements BotPlugin{
	
	protected abstract void deal(Message msg,BotAPI botop);
	
	public void deal(Event e,BotAPI botop)
	{
		Message msg=null;
		if (e.getType()==Event.Types.PrivateMessage || e.getType()==Event.Types.GroupMessage)
			msg=(Message)e;
		if (msg==null) return;
		deal(msg,botop);
	}
}
