package server.model.devices;

public class DoorSensor extends Sensor {

	public DoorSensor(String id, String name, String voiceid,
			boolean state, int pin1, int pin2, int pin3) {
		super(id, name, voiceid, state, pin1, pin2, pin3);
	}

	@Override
	public float getValue() {
		return super.getValue();
	}

	@Override
	public int getValueType() {
		return 1;
	}

	@Override
	public int getClassType() {
		return DEVICE_TYPE_SN_DOOR;
	}
	
	@Override
	public boolean isWarnValue(float value) {
		return value > 50;
	}
}
