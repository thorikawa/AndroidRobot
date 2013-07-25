#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#include <CapSense.h>

#define  TOUCH_RECV     14
#define  TOUCH_SEND     15
#define  MOTOR1_PINA    7
#define  MOTOR1_PINB    8
#define  MOTOR2_PINA    9
#define  MOTOR2_PINB    10
#define  SERVO1         11
#define  SERVO2         12
#define  SERVO3         13

#define DATA 0
#define MOTOR_CMD 1
#define SERVO_CMD 2
#define LED_CMD 3

AndroidAccessory acc("Google, Inc.",
		     "DemoKit",
		     "DemoKit Arduino Board",
		     "1.0",
		     "http://www.android.com",
		     "0000000012345678");

//byte cmdByte = 0;
int pointer = 0;
byte dataBytes[5];

boolean motor1_move = false;
boolean motor1_forward = false;
boolean motor2_move = false;
boolean motor2_forward = false;

Servo servos[3];
boolean servo1_move = false;
boolean servo2_move = false;
boolean servo3_move = false;
int servo1_deg = 0;
int servo2_deg = 0;
int servo3_deg = 0;

// 10M ohm resistor on demo shield
CapSense   touch_robot = CapSense(TOUCH_SEND, TOUCH_RECV);

void setup() {
  Serial.begin(115200);
  Serial.print("\r\nStart");
  
  pinMode(MOTOR1_PINA, OUTPUT);
  pinMode(MOTOR1_PINB, OUTPUT);
  pinMode(MOTOR2_PINA, OUTPUT);
  pinMode(MOTOR2_PINB, OUTPUT);
  servos[0].attach(SERVO1);
  servos[0].write(90);
  servos[1].attach(SERVO2);
  servos[1].write(90);
  servos[2].attach(SERVO3);
  servos[2].write(90);
  acc.powerOn();
}

void loop() {
  byte cmdByte[4];
  if (acc.isConnected()) {
      int len = acc.read(cmdByte, sizeof(cmdByte), 1);
      //cmdByte = acc.read();
      if (len > 0) {
        if (get_cmd_type(cmdByte[0]) == MOTOR_CMD) {
          run_motor_cmd(cmdByte[0], cmdByte[1], cmdByte[2]);
        } else if (get_cmd_type(cmdByte[0]) == SERVO_CMD) {
          run_servo_cmd(cmdByte[0], cmdByte[1], cmdByte[2], cmdByte[3]);
        } else if (get_cmd_type(cmdByte[0]) == LED_CMD) {
          run_led_cmd(cmdByte[0], cmdByte[1], cmdByte[2]);
        }
      }
  }
  delay(100);
}

int get_cmd_type(byte data) {
  //data check
  if ((data & B11000000) == B00000000) {
    Serial.print("\r\nData");
    return DATA;
  }
  //motor cmd check
  if ((data & B11000000) == B01000000) {
    Serial.print("\r\nMotor");
    return MOTOR_CMD;
  }
  //servo cmd check
  if ((data & B11000000) == B10000000) {
    Serial.print("\r\nServo");
    return SERVO_CMD;
  }
  //LED cmd check
  if ((data & B11000000) == B11000000) {
    Serial.print("\r\nLed");
    return LED_CMD;
  }
}

void run_motor_cmd(byte cmd, byte motor1data, byte motor2data) {
  // check motor1
  if ((cmd & B00001100) == B00000000) {
    // motor1 stop
    motor1_move = false;
  } else if ((cmd & B00001100) == B00000100) {
    // motor1 forward
    motor1_move = true;
    motor1_forward = true;
  } else if ((cmd & B00001100) == B00001100) {
    // motor1 backward
    motor1_move = true;
    motor1_forward = false;
  }
  // check motor2
  if ((cmd & B00110000) == B00000000) {
    // motor2 stop
    motor2_move = false;
  } else if ((cmd & B00110000) == B00010000) {
    // motor2 forward
    motor2_move = true;
    motor2_forward = true;
  } else if ((cmd & B00110000) == B00110000) {
    // motor2 backward
    motor2_move = true;
    motor2_forward = false;
  }
  
  // controling the motor
  if (motor1_move) {
    if (motor1_forward) {
      digitalWrite(MOTOR1_PINA, HIGH);
      digitalWrite(MOTOR1_PINB, LOW);
    } else {
      digitalWrite(MOTOR1_PINA, LOW);
      digitalWrite(MOTOR1_PINB, HIGH);
    }
  } else {
    digitalWrite(MOTOR1_PINA, LOW);
    digitalWrite(MOTOR1_PINB, LOW);
  }
  if (motor2_move) {
    if (motor2_forward) {
      analogWrite(MOTOR2_PINA, 200);
      analogWrite(MOTOR2_PINB, 0);
    } else {
      analogWrite(MOTOR2_PINA, 0);
      analogWrite(MOTOR2_PINB, 200);
    }
  } else {
    digitalWrite(MOTOR2_PINA, LOW);
    digitalWrite(MOTOR2_PINB, LOW);
  }
}

int convert2deg (int n) {
  int r = n*180/64;
  if (r==0) r++;
  return r;
}

void run_servo_cmd(byte cmd, byte servo1data, byte servo2data, byte servo3data) {
  Serial.print("\r\nrun serv");
  //check servo1
  if ((cmd & B00000001) == B00000000) {
    // servo1 stop
    servo1_move = false;
  } else if ((cmd & B00000001) == B00000001) {
    // servo1 action
    servo1_move = true;
    // get servo1 angle
    servo1_deg = convert2deg(byte2dec(servo1data));
  }
  //check servo2
  if ((cmd & B00000010) == B00000000) {
    // servo2 stop
    servo2_move = false;
  } else if ((cmd & B00000010) == B00000010) {
    // servo2 action
    servo2_move = true;
  }
  //check servo3
  if ((cmd & B00000100) == B00000000) {
    // servo3 stop
    servo3_move = false;
  } else if ((cmd & B00000100) == B00000100) {
    // servo3 action
    servo3_move = true;
  }
  
  // servo action
  if (servo1_move) {
    servos[0].write(servo1_deg);
  }
  if (servo2_move) {
    servos[1].write(servo2_deg);
  }
  if (servo3_move) {
    servos[2].write(servo3_deg);
  }
}

void run_led_cmd(byte cmd, byte led1data, byte led2data) {
  Serial.print("\r\nrun led");
  //check led1
  if ((cmd & B00000001) == B00000000) {
    // led1 off
  } else if ((cmd & B00000001) == B00000001) {
    // led1 on
  }
  //check led2
  if ((cmd & B00000010) == B00000000) {
    // led2 off
  } else if ((cmd & B00000010) == B00000010) {
    // led2 on
  }
  
}

int byte2dec(byte b) {
  int i = 0;
  i |= b & 0xFF;
  return i;
}
