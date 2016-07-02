/*
  Motors.h - Library for control car motors.
  Created by Vladislav Korecky, March 24, 2016.
*/
#ifndef Motors_h
#define Motors_h

#include <Arduino.h>

class Motors
{
  public:
    Motors(int ena, int in1, int in2, int in3, int in4, int enb);
    void setup();
    void forward(int speed, int time);
    void backward(int speed, int time);
    void left(int speed, int time);
    void right(int speed, int time);
    void stop(int time);
  private:
    int _in1;
    int _in2;
    int _ena;
    int _in3;
    int _in4;
    int _enb;
};

#endif
