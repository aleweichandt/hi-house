#include "ProtocolMessage.h"
#include <arduino.h>

ProtocolMessage::ProtocolMessage(char* buffer, int len) {
	int read = 0;
#ifdef FLOW_CONTROL_PROTOCOL_1
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
#endif
	uint8_t header =  ( buffer[read++] & 0xFF );
//hihouse header (read inverted)
	_ms_future = ( header & 0x3 );
	header >> 2;
	_ms_pins_amount = ( header & 0x3 ) + 1;
	header >> 2;
	_ms_value_type = ( header & 0x3 );
	header >> 2;
	_ms_action = ( header & 0x1 );
	header >> 1;
	_ms_type = ( header & 0x1 );	
// pin values
	for(int i = 0; i < _ms_pins_amount ; i++) {
		_ms_pin_ids[i] = ( buffer[read++] & 0xFF );
		_ms_pin_values[i] = ( buffer[read++] & 0xFF );
	}
}	