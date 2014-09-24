package server.model.devices;

public class LightSensor extends Sensor {

	public LightSensor(String id, String name, String voiceid,
			boolean state, int pin1, int pin2, int pin3) {
		super(id, name, voiceid, state, pin1, pin2, pin3);
	}

	@Override
	public float getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getValueType() {
		return 1;
	}

	@Override
	public int getClassType() {
		return DEVICE_TYPE_SN_LIGHT;
	}

}
