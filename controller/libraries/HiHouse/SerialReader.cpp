#include "Serialreader.h"
#include <arduino.h>

SerialReader::SerialReader(int bufferSize, int headerLen) {
	_buffer = (char *) malloc(bufferSize * sizeof(char));
	_headerLen = headerLen;
	_bufferSize = bufferSize;
}

unsigned int SerialReader::getPacketLength() {
	unsigned int len = 0;
	int chr, i;
	while(!Serial.available() > 1); //wait until len comes
	for( i = 0 ; i < 2 ; i++ ) {
		chr = Serial.read();
		len += ( chr << (8 * i) );
	}
	return len;
}

int SerialReader::getMessageLength() {
	return _messageLength;
}

char *SerialReader::getMessage() {
	return _buffer + _headerLen;
}

void SerialReader::write(char *message, int len) {
	char header[2];
	header[0] = len & 0xFF;
	header[1] = (len >> 8) & 0xFF;
	Serial.write( (uint8_t *) &header, 2 );
	Serial.write( (uint8_t *) message, len );
}

void SerialReader::read() {
	int i=0, input=0;
	int bufferSize = _bufferSize - 1; // leave space for 0x00
	_messageLength=getPacketLength();
#ifdef SERIAL_DEBUG
	DLOG(char(_messageLength));
#endif
	memset( _buffer, 0, _bufferSize );
	while(!Serial.available() > (_bufferSize-1)); //wait until len comes
	while( i < _messageLength ) {
		input = Serial.read();
		if( i < bufferSize ) {
			_buffer[ i ] = input;
		}
		i++;
	}
#ifdef SERIAL_DEBUG
	DLOG("complete\n");
#endif
	if( _messageLength > _bufferSize ) {
		_messageLength = _bufferSize;
	}
}
