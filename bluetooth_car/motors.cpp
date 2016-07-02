/*
  Motors.h - Library for control car motors.
  Created by Vladislav Korecky, March 24, 2016.
*/

#include <Arduino.h>
#include "motors.h"

Motors::Motors(int ena, int in1, int in2, int in3, int in4, int enb)
{
  _in1 = in1;
  _in2 = in2;
  _ena = ena;
  _in3 = in3;
  _in4 = in4;
  _enb = enb;

  pinMode(in1, OUTPUT);
  pinMode(in2, OUTPUT);
  pinMode(ena, OUTPUT);
  pinMode(in3, OUTPUT);
  pinMode(in4, OUTPUT);
  pinMode(enb, OUTPUT);
}

void Motors::setup() {
}

// Functions
// ------------------
// Car goes forward
// speed: speed of the car 0-255
// time: how long should car go [mili-seconds]
void Motors::forward(int speed, int time) {
  analogWrite(_ena, speed);
  analogWrite(_enb, speed);
  digitalWrite(_in4, HIGH);
  digitalWrite(_in3, LOW);
  digitalWrite(_in1, HIGH);
  digitalWrite(_in2, LOW);
  delay(time);
}

// Car goes backward
// speed: speed of the car 0-255
// time: how long should car go [mili-seconds]
void Motors::backward(int speed, int time) {
  analogWrite(_ena, speed);
  analogWrite(_enb, speed);
  digitalWrite(_in4, LOW);
  digitalWrite(_in3, HIGH);
  digitalWrite(_in1, LOW);
  digitalWrite(_in2, HIGH);
  delay(time);
}

// Car turns left
// speed: speed of the car 0-255
// time: how long should car go [mili-seconds]
void Motors::left(int speed, int time) {
  analogWrite(_ena, speed);
  analogWrite(_enb, speed);
  digitalWrite(_in4, LOW);
  digitalWrite(_in3, HIGH);
  digitalWrite(_in1, HIGH);
  digitalWrite(_in2, LOW);
  delay(time);
}

// Car turns right
// speed: speed of the car 0-255
// time: how long should car go [mili-seconds]
void Motors::right(int speed, int time) {
  analogWrite(_ena, speed);
  analogWrite(_enb, speed);
  digitalWrite(_in4, HIGH); //
  digitalWrite(_in3, LOW);
  digitalWrite(_in1, LOW); //
  digitalWrite(_in2, HIGH);
  delay(time);
}

// Car stops
// time: how long should car stop [mili-seconds]
void Motors::stop(int time) {
  analogWrite(_ena, 0);
  analogWrite(_enb, 0);
  digitalWrite(_in4, LOW); //
  digitalWrite(_in3, LOW);
  digitalWrite(_in1, LOW); //
  digitalWrite(_in2, LOW);
  delay(time);
}
