package server.model.devices;

import server.model.ArduinoHandler;

public class DoorActuator extends Actuator {

	public DoorActuator(String id, String name, String voiceid,
			boolean state, int pin1, int pin2, int pin3) {
		super(id, name, voiceid, state, pin1, pin2, pin3);
	}

	@Override
	public int getValueType() {
		return 2;
	}

	@Override
	public int getClassType() {
		return DEVICE_TYPE_AC_DOOR;
	}

	@Override
	public boolean setState(boolean state) {
		int values[] = {state?0:75};
		ArduinoHandler.getInstance().addOperation(this, false, values);
		waitLock();
		mState = state;
		this.commitToDB();
		return true;
	}
}
