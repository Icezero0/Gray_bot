package botEvent;

import java.util.Date;

public final class EmptyEvent extends Event {
	
	public EmptyEvent()
	{
		this.json=null;
		this.jsonStr=null;
		this.time=new Date().getTime();
	}
	
	public Types getType() {
		return Types.EmptyEvent;
	}
	
	public String toString()
	{
		return "Event : Nothing.\n";
	}
}
