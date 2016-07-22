/*
  Bluetooth.cpp - Library for bluetooth communication.
  Created by Vladislav Korecky, March 24, 2016.
*/

#include <Arduino.h>
#include <SoftwareSerial.h>
#include "bluetooth.h"

Bluetooth::Bluetooth(int rx, int tx) : serialBT(SoftwareSerial(rx, tx))
{
  _rx = rx;
  _tx = tx;
}

void Bluetooth::setup() {
  // initialization of serial port 9600 baud
  serialBT.begin(9600);
  // waits on initialization
  delay(500);
}

void Bluetooth::send(String message)
{
  serialBT.println(message);
}

char Bluetooth::read() {
  char charBT;
  if ( serialBT.available())
  {
    charBT = serialBT.read();
  }
  return charBT;
}
