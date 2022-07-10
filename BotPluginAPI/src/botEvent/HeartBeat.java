package botEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

import IceUtil.Json;

public class HeartBeat extends Event {
	
	HeartBeat(Json json)
	{
		this.json=json;
		this.jsonStr=json.toString();
		this.time=((Double) json.get("time")).longValue()*1000L;
	}

	public Types getType() {
		return Types.HeartBeat;
	}
	
	public String toString()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return "Event : Heart Beat Package\n"
				+ "  - Time : "+ sdf.format(new Date(this.time)) + "\n";
	}
}
