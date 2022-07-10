package botPluginAPI;

import botEvent.Event;

public abstract class StateFlowPlugin implements BotPlugin {
//仿StateFlow流程的插件
	
	public void deal(Event e, BotAPI botop) {
		
	}
	
	protected abstract State getInitialState();
	protected abstract State trigger();
	
	protected interface State
	{
		public boolean isFinalState();
		public void entry();
		public void during();
		public void exit();
	}

}
