#ifndef __PROTOCOL_MESSAGE_H__
#define __PROTOCOL_MESSAGE_H__

#include <inttypes.h>

#define ARRAY_MAX_ACK 32
#define ARRAY_MAX_PINS 4

class ProtocolMessage {

	public:
//const
	//type
		static const uint8_t MSG_TYPE_REQ = 0;
		static const uint8_t MSG_TYPE_RESP = 1;
	//action
		static const uint8_t MSG_ACTION_READ = 0;
		static const uint8_t MSG_ACTION_WRITE = 1;
	//value type
		static const uint8_t MSG_VAL_TYPE_DIG = 0;
		static const uint8_t MSG_VAL_TYPE_ANALOG = 1;
		static const uint8_t MSG_VAL_TYPE_SERVO = 2;
		
		ProtocolMessage(char* buffer, int len);
		~ProtocolMessage();
		
		bool isRequest() { return _ms_type == MSG_TYPE_REQ; };
		uint8_t getAction() { return _ms_action; };
		uint8_t getValueType() { return _ms_value_type; };
		uint8_t getPinsAmount() { return _ms_pins_amount; };
		uint8_t getPin( uint8_t index ) { return _ms_pin_ids[ index ]; };
		uint8_t getPinValue( uint8_t index ) { return _ms_pin_values[ index ]; };
		
	private:
#ifdef FLOW_CONTROL_PROTOCOL_1
	//protocolo header
		uint16_t _seq;
		uint16_t _ack;
#ifdef FLOW_CONTROL_PROTOCOL_2
		uint8_t _ack_bits[ARRAY_MAX_ACK];
#endif
#endif
	//hihouse message header
		uint8_t _ms_type;			//1 bit
		uint8_t _ms_action;			//1 bit
		uint8_t _ms_value_type;		//2 bit
		uint8_t _ms_pins_amount;	//2 bit
		uint8_t _ms_future;			//2 bit
	//hihouse message pins
		uint8_t _ms_pin_ids[ARRAY_MAX_PINS + 1];
		uint8_t _ms_pin_values[ARRAY_MAX_PINS + 1];
};

#endif