#include "HiHouseOperator.h"
#include <arduino.h>

HiHouseOperator::HiHouseOperator(){
	_servo = new Servo();
	_serial = new SerialReader(128,0);
}

HiHouseOperator::~HiHouseOperator(){

}

void HiHouseOperator::update() {
//read message
#ifdef SERIAL_DEBUG
	DLOG("recibiendo..\n");
#endif
	_serial->read();
	ProtocolMessage* received = new ProtocolMessage(_serial->getMessage(), _serial->getMessageLength());
	if(received->isRequest()) {
	//operate message
		execOperationMessage(received);
		sendResponse(received);
	}
}

void HiHouseOperator::execOperationMessage(ProtocolMessage* message){
	bool isReadOperation = (message->getAction() == ProtocolMessage::MSG_ACTION_READ);
	for(int i = 0; i < message->getPinsAmount(); i++) {
		int pin = message->getPin(i);
		int value = message->getPinValue(i);
		switch (message->getValueType()) {
			case ProtocolMessage::MSG_VAL_TYPE_DIG: {
				pinMode( pin, ( isReadOperation ? INPUT : OUTPUT ) );
				if(isReadOperation) {
					value = digitalRead(pin);
				} else {
					digitalWrite(pin, value);
				}
				break;
			}
			case ProtocolMessage::MSG_VAL_TYPE_ANALOG: {
				pinMode( pin, ( isReadOperation ? INPUT : OUTPUT ) );
				if(isReadOperation) {
					value = analogRead(pin);
				} else {
					analogWrite(pin, value);
				}
				break;
			}
			case ProtocolMessage::MSG_VAL_TYPE_SERVO: {
				_servo->attach(pin);
				if(isReadOperation) {
					value = _servo->read();
				} else {
					_servo->write(value);
				}
				delay(1000);
				_servo->detach();
				break;
			}
			default: {
				break;
			}
		}
		value = map(value, 0, 1023, 0, 255);
		message->setPinValue(i, value);
	}
}

void HiHouseOperator::sendResponse(ProtocolMessage* message) {
	message->setResponseMode();
	char* serialized;
	serialized = (char *) malloc(8 * sizeof(char));
	int len = message->serialize(serialized);
	_serial->write(serialized, len);
	free(serialized);
}
