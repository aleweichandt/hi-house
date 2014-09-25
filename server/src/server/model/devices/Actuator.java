package server.model.devices;

import server.model.ArduinoHandler;

public abstract class Actuator extends Device {

	public Actuator(String id, String name, String voiceid,
			boolean state, int pin1, int pin2, int pin3) {
		super(id, name, voiceid, state, pin1, pin2, pin3);
	}

	@Override
	public boolean setState(boolean state) {
		int values[] = {state?1:0};
		ArduinoHandler.getInstance().addOperation(this, false, values);
		waitLock();
		mState = state;
		this.commitToDB();
		return true;
	}
}
