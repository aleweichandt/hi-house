package server.model.devices;

import server.model.ArduinoHandler;

public class TermalActuator extends Actuator {

	public static final int TERMAL_ACT_SUBSTATE_COOL = -1;
	public static final int TERMAL_ACT_SUBSTATE_NONE = 0;
	public static final int TERMAL_ACT_SUBSTATE_HEAT = 1;
	
	private int mTermalState = 0;
	public TermalActuator(String id, String name, String voiceid,
			boolean state, int pin1, int pin2, int pin3) {
		super(id, name, voiceid, state, pin1, pin2, pin3);
		mTermalState = TERMAL_ACT_SUBSTATE_NONE;
	}

	@Override
	public int getValueType() {
		return 1;
	}

	@Override
	public int getClassType() {
		return DEVICE_TYPE_AC_TERMAL;
	}

	@Override
	public boolean setState(boolean state) {
		if(!state) {
			mTermalState = TERMAL_ACT_SUBSTATE_NONE;
		}
		int values[] = translateValues();
		ArduinoHandler.getInstance().addOperation(this, false, values);
		return true;
	}
	
	private int[] translateValues() {
		switch(mTermalState) {
		case TERMAL_ACT_SUBSTATE_COOL:{
			int values[] = {0,0,1};
			return values;
		}
		case TERMAL_ACT_SUBSTATE_HEAT:{
			int values[] = {1,0,0};
			return values;
		}
		default:{
			int values[] = {0,0,0};
			return values;
		}
		}
	}
	
	public boolean canHeat(){ 
		//TODO check param1
		return true;
	}
	
	public void heat(){ 
		mTermalState = TERMAL_ACT_SUBSTATE_HEAT;
	}

	public boolean canCool(){ 
		//TODO check param1
		return true;
	}
	
	public void cool(){ 
		mTermalState = TERMAL_ACT_SUBSTATE_COOL;
	}
	
	public void none() {
		mTermalState = TERMAL_ACT_SUBSTATE_NONE;
	}
}
