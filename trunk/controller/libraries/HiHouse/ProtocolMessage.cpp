#include "ProtocolMessage.h"
#include <arduino.h>

ProtocolMessage::ProtocolMessage(char* buffer, int len) {
#ifdef SERIAL_DEBUG
	DLOG("creating message\n");
#endif
	int read = 0;
/*#ifdef FLOW_CONTROL_PROTOCOL_1
//protocolo header
	_seq = ( ( buffer[read++] & 0xFF ) << 8 );
	_seq += ( buffer[read++] & 0xFF );
	_ack = ( ( buffer[read++] & 0xFF ) << 8 );
	_ack += ( buffer[read++] & 0xFF );;
#ifdef FLOW_CONTROL_PROTOCOL_2
	uint32_t ackbits = 0;
	ackbits += ( ( buffer[read++] & 0xFF ) << 24 );
	ackbits += ( ( buffer[read++] & 0xFF ) << 16 ); 
	ackbits += ( ( buffer[read++] & 0xFF ) << 8 );
	ackbits +=  ( buffer[read++] & 0xFF );
	for( int i = ARRAY_MAX_ACK - 1; i >= 0; i++ ) {
		_ack_bits[i] = ( ackbits & 0x1 );
		ackbits >> 1;
	}
#endif
#endif*/
	uint8_t header =  ( buffer[read++] & 0xFF );
//hihouse header (read inverted)
	_ms_future = ( header & 0x3 );
	header = header >> 2;
	_ms_pins_amount = ( header & 0x3 );
	header = header >> 2;
	_ms_value_type = ( header & 0x3 );
	header = header >> 2;
	_ms_action = ( header & 0x1 );
	header = header >> 1;
	_ms_type = ( header & 0x1 );
#ifdef SERIAL_DEBUG
	DLOG("future=");DLOG(char(_ms_future));DLOG("\n");
	DLOG("pins=");DLOG(char(_ms_pins_amount));DLOG("\n");
	DLOG("value_t=");DLOG(char(_ms_value_type));DLOG("\n");
	DLOG("action=");DLOG(char(_ms_action));DLOG("\n");
	DLOG("type=");DLOG(char(_ms_type));DLOG("\n");
#endif
// pin values
	for(int i = 0; i < _ms_pins_amount ; i++) {
		_ms_pin_ids[i] = ( buffer[read++] & 0xFF );
		_ms_pin_values[i] = ( buffer[read++] & 0xFF );
#ifdef SERIAL_DEBUG
		DLOG("pin id=");DLOG(char(_ms_pin_ids[i]));DLOG("\n");
		DLOG("pin val=");DLOG(char(_ms_pin_values[i]));DLOG("\n");
#endif
	}
}	