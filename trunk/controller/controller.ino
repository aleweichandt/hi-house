#include <Servo.h>
#include <HiHouseOperator.h>
#include <includes.h>
#include <ProtocolMessage.h>
#include <SerialReader.h>

HiHouseOperator hihouse;

void setup() {
  Serial.begin(9600);
}

void loop() {
  hihouse.update();
}
