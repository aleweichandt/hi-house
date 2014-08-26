#include <HHSystem.h>

HHSystem hihouse();

void setup() {
  Serial.begin(9600);
  hihouse.init();
}

void loop() {
  hihouse.update();
}
