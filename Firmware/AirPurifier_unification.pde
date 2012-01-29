#include <Wire.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define  RELAY1          A0
#define  DUST_SENSOR     A2
#define  SMOKE_SENSOR    A3

AndroidAccessory acc("Yunsu Choi",
"Air Purifier",
"DemoKit Arduino Board",
"1.0",
"http://www.android.com",
"0000000012345678");

void init_relays()
{
  pinMode(RELAY1, OUTPUT);
}

void setup()
{
  Serial.begin(9600);
  Serial.print("\r\nStart");
  init_relays();
  acc.powerOn();
}

void loop()
{
  static byte count = 0;
  byte msg[3];

  if (acc.isConnected()) {
    Serial.print("Accessory connected. ");
    int len = acc.read(msg, sizeof(msg), 1); // Do Not Change 1 into -1
    Serial.print("Message length: ");
    Serial.println(len, DEC);
  } 
  delay(100);

  if (acc.isConnected()) {
    int len = acc.read(msg, sizeof(msg), 1);

    uint16_t val;

    if (len > 0) {
      if (msg[0] == 0x2) {
      } 
      else if (msg[1] == 0x0)
        digitalWrite(RELAY1, msg[2] ? HIGH : LOW);
    }

    msg[0] = 0x1;
    switch (count++ % 0x10) {
    case 0:
      val = analogRead(SMOKE_SENSOR);
      msg[0] = 0x4;
      msg[1] = val >> 8;
      msg[2] = val & 0xff;
      acc.write(msg, 3);
      break;

    case 0x4:
      val = analogRead(DUST_SENSOR);
      msg[0] = 0x5;
      msg[1] = val >> 8;
      msg[2] = val & 0xff;                                  
      acc.write(msg, 3);
      break;
    }
  } 
  else {
    digitalWrite(RELAY1, LOW);
  }
  delay(1);
}
