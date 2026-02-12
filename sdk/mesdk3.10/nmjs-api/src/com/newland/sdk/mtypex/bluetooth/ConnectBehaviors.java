package com.newland.sdk.mtypex.bluetooth;

import com.newland.sdk.mtypex.bluetooth.BlueToothConnectForceBehavior.ConnectType;

import java.util.ArrayList;
import java.util.List;

public class ConnectBehaviors {
	
	private static final List<BlueToothConnectForceBehavior> behaviors = new ArrayList<BlueToothConnectForceBehavior>();
	
	public static BlueToothConnectForceBehavior BEHAVIOR = new BlueToothConnectForceBehavior(".*",".*", true, ConnectType.INSECURE, false ,false);
	
	static{
		behaviors.add(new BlueToothConnectForceBehavior("LENOVO","IdeaTabS2110AH", false, ConnectType.INSECURE, true ,false));
		behaviors.add(new BlueToothConnectForceBehavior("HUAWEI","MediaPad 7 Vogue", false, ConnectType.INSECURE, true ,false));
		behaviors.add(new BlueToothConnectForceBehavior("HUAWEI","HUAWEI A199", false, ConnectType.INSECURE, true ,false));
		behaviors.add(new BlueToothConnectForceBehavior("Xiaomi","2013022", false, ConnectType.SECURE, false,true));
		behaviors.add(new BlueToothConnectForceBehavior("HUAWEI","HUAWEI MT1-U06", false, ConnectType.INSECURE, true ,false));
	
		for(BlueToothConnectForceBehavior behavior:behaviors){
			if(behavior.matches()){
				BEHAVIOR = behavior;
				break;
			}
		}
	}
	

}
