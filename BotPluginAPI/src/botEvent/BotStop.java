package botEvent;

import java.util.Date;

public class BotStop extends Event {
	
	public BotStop()
	{
		this.json=null;
		this.jsonStr=null;
		this.time=new Date().getTime();
	}
	
	public Types getType() {
		return Types.BotStop;
	}
	
	public String toString()
	{
		return "Event : Bot Stop Order.\n";
	}
}
