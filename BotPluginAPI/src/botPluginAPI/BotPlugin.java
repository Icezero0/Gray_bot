package botPluginAPI;

import botEvent.Event;

public interface BotPlugin {
	
	public enum Types{Global,Sessional};
	
	public Types getType();
	public String getPluginName();
	public long getPluginID();
	public String getPluginDescription();
	
	public void deal(Event e,BotAPI botop);
	
}
