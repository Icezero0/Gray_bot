package comm_module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

import IceUtil.Func;
import IceUtil.Json;
import botEvent.GroupForwardMessage;
import botEvent.GroupMessage;
import botEvent.Message;
import botEvent.PrivateMessage;
import kernel.Bot;
import botEvent.BotMessage;
import botEvent.Event;
import botEvent.Event.Types;



public class BotHttpSender implements Runnable{
	
	public Thread thread=null;
	
	private String sendURL=null;
	
	private boolean alive=false;
	
	private BlockingQueue<Event> reportList=null;
	
	private BlockingQueue<Event> sendList=null;
	
	public BotHttpSender(String sendURL,BlockingQueue<Event> sendList,BlockingQueue<Event> reportList) throws IOException
	{
		this.sendURL=sendURL;
		this.sendList=sendList;
		this.reportList=reportList;
		this.thread=new Thread(this);
		this.alive=false;
	}

	
	public void start()
	{
		this.alive=true;
		this.thread.start();
	}
	
	public void close()
	{
		this.alive=false;
		this.sendList.add(new PrivateMessage(0, null));
	}

	public void run() {
		while(this.alive)
		{
			Message term;
			do
			{
				term=null;
				try {
					term=(Message) this.sendList.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (term!=null && (term.getContent()!=null || term.getType()==Types.GroupForwardMessage))
				{
//					Bot.Debug(sendMessage(term));
					sendMessage(term);
				}
			}while(this.sendList.size()>0);
		}
	}
	
	private String sendMessage(Message msg)
	{
		if (msg.getContent()==null && msg.getType()!=Types.GroupForwardMessage) return "NULL";
		String rst=null;
		switch(msg.getType())
		{
		case PrivateMessage:
			rst=this.sendPrivateMsg(msg.getSenderID(), msg.getContent());
			break;
		case GroupMessage:
			rst=this.sendGroupMsg(((GroupMessage)msg).getGroupID(), msg.getContent());
			break;
		case GroupForwardMessage:
			rst=this.sendGroupForwardMsg(((GroupForwardMessage)msg).getGroupID(),((GroupForwardMessage)msg).getNodes());
			break;
		default:
			return null;
		}
		Json json=null;
		try{
			json=new Json(rst);
		}catch(JsonSyntaxException e)
		{
			e.printStackTrace();
		}
		String status=(String) json.get("status");
		Map data=null;
		Double message_id=null;
		if (status!=null && status.equals("ok"))
		{
			data=(Map) json.get("data");
			if (data!=null)
			{
				message_id=(Double) data.get("message_id");
				if (message_id!=null)
				{
					long messageID=message_id.longValue();
					this.reportList.add(new BotMessage(msg, messageID));
				}
			}
		}
			
		return rst;
	}
	
	private String sendPrivateMsg(String user_id,String message)
	{
		String OP="/send_private_msg";
		Json json=new Json();
		json.add("user_id", user_id);
		json.add("message", message);
		String rst=null;
		try {
			rst=this.PostToBot(OP,json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rst;
	}
	
	private String sendPrivateMsg(long user_id,String message)
	{
		return this.sendPrivateMsg(String.valueOf(user_id),message);
	}
	
	private String sendGroupMsg(String group_id,String message)
	{
		String OP="/send_group_msg";
		Json json=new Json();
		json.add("group_id", group_id);
		json.add("message", message);
		String rst=null;
		try {
			rst=this.PostToBot(OP,json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rst;
	}
	
	private String sendGroupMsg(long group_id,String message)
	{
		return this.sendGroupMsg(String.valueOf(group_id),message);
	}
	
	private String sendGroupForwardMsg(String group_id, List<Message> nodes) {
		String OP="/send_group_forward_msg";
		Json json=new Json();
		json.add("group_id", group_id);
		ArrayList<LinkedTreeMap<String,Object>> messages=new ArrayList<LinkedTreeMap<String,Object>>();
		for (Message node:nodes)
		{
			LinkedTreeMap<String,Object> msg=new LinkedTreeMap<String,Object>();
			msg.put("type", "node");
			LinkedTreeMap<String,String> data=new LinkedTreeMap<String,String>();
			data.put("name", node.getSenderName());
			data.put("uin", String.valueOf(node.getSenderID()));
			data.put("content", node.getContent());
			msg.put("data", data);
			messages.add(msg);
		}
		json.add("messages", messages);
		String rst=null;
		try {
			rst=this.PostToBot(OP, json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rst;
	}
	
	private String sendGroupForwardMsg(long group_id, List<Message> nodes) {
		return this.sendGroupForwardMsg(String.valueOf(group_id),nodes);
	}
	
	private String PostToBot(String URI,String content) throws IOException
	{
//		Bot.Debug("Post "+URI+","+content);
		return Func.quickHttpPost(this.sendURL+URI, content);
	}
	
	
	
}
