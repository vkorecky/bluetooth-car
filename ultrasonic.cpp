
/*
  Ultrasonic.h - Library for control ultrasonic sensor.
  Created by Vladislav Korecky, March 24, 2016.
*/
#include <Servo.h>
#include <Arduino.h>
#include "ultrasonic.h"

Servo myservo;

Ultrasonic::Ultrasonic(int servo, int echo, int trig)
{
  _servo = servo;
  _echo = echo;
  _trig = trig;
  pinMode(echo, INPUT);    //
  pinMode(trig, OUTPUT);  //
}

void Ultrasonic::setup()
{
  myservo.attach(_servo);    //
}

// Functions
// ------------------
long Ultrasonic::measure(int servoAngel)
{
  long duration, distance;

  myservo.write(servoAngel);
  delay(250);

  digitalWrite(_trig, LOW);  // Added this line
  delayMicroseconds(2); // Added this line
  digitalWrite(_trig, HIGH);
  delayMicroseconds(10); // Added this line
  digitalWrite(_trig, LOW);
  duration = pulseIn(_echo, HIGH);

  distance = (duration / 2) / 29.1;
  if (distance >= 200 || distance <= 0) {
    return -1;
  }
  return distance;
}

