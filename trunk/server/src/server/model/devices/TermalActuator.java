package server.model.devices;

import server.model.ArduinoHandler;

public class TermalActuator extends Actuator {

	public static final int TERMAL_ACT_SUBSTATE_COOL = -1;
	public static final int TERMAL_ACT_SUBSTATE_NONE = 0;
	public static final int TERMAL_ACT_SUBSTATE_HEAT = 1;

	protected static final int TERMAL_ACT_SUBTYPE_COOL = 1;//binary 01
	protected static final int TERMAL_ACT_SUBTYPE_HEAT = 2;//binary 10
	
	private int mTermalState = 0;
	private int mSubtype = 0;
	public TermalActuator(String id, String name, String voiceid,
			boolean state, int pin1, int pin2, int pin3, String subtype) {
		super(id, name, voiceid, state, pin1, pin2, pin3);
		mTermalState = TERMAL_ACT_SUBSTATE_NONE;
		mSubtype = Integer.parseInt(subtype);
	}

	@Override
	public int getValueType() {
		return 0;
	}

	@Override
	public int getClassType() {
		return DEVICE_TYPE_AC_TERMAL;
	}

	@Override
	public boolean setState(boolean state) {
		int values[] = {0,0,0};
		if(state) {
			values = translateValues();
		} else {
			mTermalState = TERMAL_ACT_SUBSTATE_NONE;
		}
		ArduinoHandler.getInstance().addOperation(this, false, values);
		waitLock();
		mState = state;
		this.commitToDB();
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
			int values[] = {0,1,0};
			return values;
		}
		}
	}
	
	public boolean canHeat(){ 
		return (mSubtype & TERMAL_ACT_SUBTYPE_HEAT) > 0 ;
	}
	
	public void heat(){ 
		mTermalState = TERMAL_ACT_SUBSTATE_HEAT;
	}

	public boolean canCool(){ 
		return (mSubtype & TERMAL_ACT_SUBTYPE_COOL) > 0 ;
	}
	
	public void cool(){ 
		mTermalState = TERMAL_ACT_SUBSTATE_COOL;
	}
	
	public void none() {
		mTermalState = TERMAL_ACT_SUBSTATE_NONE;
	}
	
	public String getParam1() {
		return Integer.toString(mSubtype);
	}
}
