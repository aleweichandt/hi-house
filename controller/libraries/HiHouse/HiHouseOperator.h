#ifndef __HIHOUSE_OPERATOR_H__
#define __HIHOUSE_OPERATOR_H__

#include "HiHouse.h"
#include "ProtocolMessage.h"
#include "SerialReader.h"
#include <Servo.h>

class HiHouseOperator {
	public:
		HiHouseOperator();
		~HiHouseOperator();
		void update();
	private:
		void execOperationMessage(ProtocolMessage* message);
		void sendResponse(ProtocolMessage* message);
		
		Servo* _servo;
		SerialReader* _serial;
};
#endif