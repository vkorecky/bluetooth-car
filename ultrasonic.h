/*
  Ultrasonic.h - Library for control ultrasonic sensor.
  Created by Vladislav Korecky, March 24, 2016.
*/
#ifndef Ultrasonic_h
#define Ultrasonic_h

#include <Arduino.h>
#include <Servo.h>

class Ultrasonic
{
  public:
    Ultrasonic(int servo, int echo, int trig);
    void setup();
    long measure(int servoAngel);
  private:
    Servo myservo;
    int _servo;
    int _echo;
    int _trig;
};


#endif

