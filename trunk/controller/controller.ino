#include <Servo.h>
#include <HiHouseOperator.h>

HiHouseOperator hihouse;

void setup() {
  Serial.begin(9600);
}

void loop() {
  hihouse.update();
}
