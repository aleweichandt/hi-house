#ifndef __HIHOUSE_H__
#define __HIHOUSE_H__

//enable to debug code, output to serial, only use for one side connection
//#define SERIAL_DEBUG 1
#ifdef SERIAL_DEBUG
#define DLOG_BUFF(a,b) Serial.write(a,b)
#define DLOG(a) Serial.write(a)
#endif


//enable this to user sequence and acknowledge numbers to control single message flow
#define FLOW_CONTROL_PROTOCOL_1 0
//enable this to user sequence and acknowledge numbers to control multiple message flow
#define FLOW_CONTROL_PROTOCOL_2 0

#endif