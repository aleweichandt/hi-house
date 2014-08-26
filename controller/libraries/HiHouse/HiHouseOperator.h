#ifndef __HIHOUSE_OPERATOR_H__
#define __HIHOUSE_OPERATOR_H__

#include "ProtocolMessage.h"
#include "SerialReader.h"
#include <Servo.h>

//enable this to user sequence and acknowledge numbers to control single message flow
#define FLOW_CONTROL_PROTOCOL_1 0
//enable this to user sequence and acknowledge numbers to control multiple message flow
#define FLOW_CONTROL_PROTOCOL_2 0

class HiHouseOperator {
	public:
		HiHouseOperator();
		~HiHouseOperator();
		void update();
	private:
		void execOperationMessage(ProtocolMessage* message);
		ProtocolMessage* makeResponse(const ProtocolMessage message);
		
		Servo* _servo;
		SerialReader* _serial;
};
#endif