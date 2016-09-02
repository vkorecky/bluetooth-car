// **************************************************
// Bluetooth car
// -------------
// Car comunicate with computer throught bluetooth.
// All inteligence must be in computer software,
// car is stupid and only receive commands and sends
// information about enviroment to the computer
// **************************************************

// Includes
// ---------
#include <Servo.h>
#include <Arduino.h>
#include <SoftwareSerial.h>

#include "motors.h"
#include "ultrasonic.h"
#include "bluetooth.h"

Bluetooth bluetooth =  Bluetooth(12, 13); //RX = 12, TX = 13
Ultrasonic ultrasonic =  Ultrasonic(3, 4, 5); //servo = 3; echo = 4; trig = 5;
Motors motors =  Motors(6, 7, 8, 9, 10, 11); // ena = 6, in1 = 7, in2 = 8, in3 = 9, in4 = 10, enb = 11

int _servoAngel = 0;
int _servoAngelStep = 90;
int _servoDirection = 1;

char terminationChar = '\n';
String receivedMessage = "";


// Setup
// -----
void setup() {
  //Serial.begin(9600);      // open the serial port at 9600 bps:
  bluetooth.setup();
  ultrasonic.setup();
  motors.setup();
}

// Main
// -----
void loop() {
  // Send information from CAR 
  String sendMessage = String(_servoAngel) + ":" + String(-1);  
  bluetooth.send(sendMessage);
  
  long distance = ultrasonic.measure(_servoAngel); 
  
  sendMessage = String(_servoAngel) + ":" + String(distance);  
  bluetooth.send(sendMessage);

  if ((_servoDirection == 1) && ((_servoAngel + _servoAngelStep) > 180)) {
    _servoDirection = 0;    
  } else if ((_servoDirection == 0) && ((_servoAngel - _servoAngelStep) < 0)) {
    _servoDirection = 1;    
  }  

  if (_servoDirection == 1) {
    _servoAngel = _servoAngel + _servoAngelStep;
  } else {
    _servoAngel = _servoAngel - _servoAngelStep;
  }

  // Receive information from Bluetooth  
  char receivedChar = bluetooth.read();  //gets one byte from serial buffer
  if (receivedChar == terminationChar) {
    if (receivedMessage.length() > 0) {
      receivedMessage.toLowerCase();
      if (receivedMessage == "forward"){
        motors.forward(255,100);
      } else if (receivedMessage == "backward"){
        motors.backward(255,100);
      } else if (receivedMessage == "left"){
        motors.left(255,100);
      } else if (receivedMessage == "right"){
        motors.right(255,100);     
      } 
      receivedMessage=""; //clears variable for new input
  }
  } else {
    receivedMessage = String(receivedMessage) + String(receivedChar); 
  }
}

