#ifndef __SERIAL_READER_H__
#define __SERIAL_READER_H__

#include "HiHouse.h"

//enable this to user sequence and acknowledge numbers to control single message flow
#define FLOW_CONTROL_PROTOCOL_1 0
//enable this to user sequence and acknowledge numbers to control multiple message flow
#define FLOW_CONTROL_PROTOCOL_2 0

class SerialReader {

	public:
		SerialReader(int bufferSize, int headerLen);
		void read();
		void write(char *message, int len);
		char *getMessage();
		int getMessageLength();
		bool isAvailable();
		
	private:
		unsigned int getPacketLength();
		char *_buffer;
		int _bufferSize;
		int _headerLen;
		unsigned int _messageLength;
	
};

#endif