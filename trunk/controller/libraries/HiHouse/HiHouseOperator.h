#ifndef __HIHOUSE_OPERATOR_H__
#define __HIHOUSE_OPERATOR_H__

class Servo;
class SerialReader;
class ProtocolMessage;

class HiHouseOperator {
	public:
		HiHouseOperator();
		~HiHouseOperator();
		void update();
	private:
		void execOperationMessage(const ProtocolMessage message);
		ProtocolMessage* makeResponse(const ProtocolMessage message);
		
		Servo* _servo;
		SerialReader* _serial;
};
#endif