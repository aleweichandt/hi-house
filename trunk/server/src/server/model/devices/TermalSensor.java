package server.model.devices;

import server.model.C;

public class TermalSensor extends Sensor {

	public TermalSensor(String id, String name, String voiceid,
			boolean state, int pin1, int pin2, int pin3) {
		super(id, name, voiceid, state, pin1, pin2, pin3);
	}

	@Override
	public float getValue() {
		return (float) (((super.getValue() * 0.004882814)  - 0.5) * 100.0);
	}

	@Override
	public int getValueType() {
		return 1;
	}

	@Override
	public int getClassType() {
		return DEVICE_TYPE_SN_TERMAL;
	}

	@Override
	public boolean isWarnValue(float value) {
		return value > C.Config.SECURITY_SN_TERMAL_LIMIT;
	}

}
