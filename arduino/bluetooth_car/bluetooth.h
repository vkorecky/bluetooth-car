/*
  Bluetooth.h - Library for bluetooth communication.
  Created by Vladislav Korecky, March 24, 2016.
*/
#ifndef Bluetooth_h
#define Bluetooth_h

#include <Arduino.h>
#include <SoftwareSerial.h>


class Bluetooth
{
  public:
    Bluetooth(int rx, int tx);
    void setup();
    void send(String message);
    char read();
  private:
    SoftwareSerial serialBT;
    int _rx;
    int _tx;
};

#endif
