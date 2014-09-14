package server.model.devices;

public class TermalActuator extends Actuator {

	public TermalActuator(String id, String name, String voiceid,
			boolean state, int pin1, int pin2, int pin3) {
		super(id, name, voiceid, state, pin1, pin2, pin3);
	}

	@Override
	public int getValueType() {
		return 1;
	}
}
