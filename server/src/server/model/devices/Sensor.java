package server.model.devices;

import server.model.ArduinoHandler;

public abstract class Sensor extends Device {
	
	protected int mValue;
	
	public Sensor(String id, String name, String voiceid,
			boolean state, int pin1, int pin2, int pin3) {
		super(id, name, voiceid, state, pin1, pin2, pin3);
	}

	@Override
	public boolean setState(boolean state) {
		mState = state;
		return mState;
	}
	
	public int getValue() {
		ArduinoHandler.getInstance().addOperation(this, false);
		//TODO make async
		return 0;
	};
}
