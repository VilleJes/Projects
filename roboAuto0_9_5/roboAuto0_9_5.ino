#include <IRremote.h>

#include <Wire.h> //I2C Arduino Library
#include <Math.h>
 
#define HMC5883L_ADDR 0x1E


int recv_pin = 2;
int trigPinL = 12;    // Trigger vasen ultraäänisensori
int echoPinL = 8;     //Echo vasen ultraäänisensori
int trigPinR = 7;    // Trigger oikea ultraäänisensori
int echoPinR = 5;// Echo oikea ultraäänisensori
int trigPinF = 4;    // Trigger etu ultraäänisensori
int echoPinF = 3;// Echo etu ultraäänisensori
int trigPinB = 22;    // Trigger taka ultraäänisensori
int echoPinB = 24;// Echo taka ultraäänisensori
int motorPwmFL = 11; //etuvasemman moottorin PWM ohjaus
int motorPwmFR = 10; //etuoikean moottorin PWM ohjaus
int motorPwmBL = 9;//takavasemman moottorin PWM ohjaus
int motorPwmBR = 6;//takaoikean moottorin PWM ohjaus
int HBridgeFIn1 = A2;//1 ja 2 vasemalle renkaalle. 3 ja 4 oikealle renkaalle.
int HBridgeFIn2 = A3;//täytyy olla samalle renkaalle vastakkaiset tilat
int HBridgeFIn3 = A4;//ESIM. Jos 1 = HIGH silloin 2 = LOW
int HBridgeFIn4 = A5;//näin vaihtaa renkaan pyörimis suuntaa.
int HBridgeBIn1 = A6;
int HBridgeBIn2 = A7;
int HBridgeBIn3 = A8;
int HBridgeBIn4 = A9;
int northLed = 28;
int westLed = 30;
int southLed = 32;
int eastLed = 34;

bool haveHMC5883L = false;

bool detectHMC5883L ()
{
  // read identification registers
  Wire.beginTransmission(HMC5883L_ADDR); //open communication with HMC5883
  Wire.write(10); //select Identification register A
  Wire.endTransmission();
  Wire.requestFrom(HMC5883L_ADDR, 3);
  if(3 == Wire.available()) {
    char a = Wire.read();
    char b = Wire.read();
    char c = Wire.read();
    if(a == 'H' && b == '4' && c == '3')
      return true;
  }

  return false;
}
/* 7-SEGMENTTINÄYTTÖ 
int segmentA = 30;
int segmentB = 31;
int segmentC = 32;
int segmentD = 33;
int segmentE = 34;
int segmentF = 35;
int segmentG = 36;
int applyLED = 37;
*/

long durationL, durationR, durationF, durationB, cmL, cmR, cmF, cmB;

//Kaukosäätimen näppäinpainallukset hexalukuina
long Go = 0x00FFC23D;
long Reverse = 0x00FF02FD;
long Stop = 0x00FFA25D;
long Break = 0x00FF52AD;
long TurnLeft = 0x00FFA857;
long TurnRight = 0x00FF906F;
long IRapplyOFF = 0x00FF9867;
long IRapplyON = 0x00FFB04F;


IRrecv irrecv(recv_pin);
decode_results results;

void dump(decode_results *results)

{
  int count = results->rawlen;
  if (results->decode_type == UNKNOWN)
  {
    Serial.println("Could not decode message");
  }
  else
  {
    if (results->decode_type == NEC)
    {
      Serial.print("Decoded NEC: ");
    }
    else if (results->decode_type == SONY)
    {
      Serial.print("Decoded SONY: ");
    }
    else if (results->decode_type == RC5)
    {
     Serial.print("Decoded RC5: ");
    }
    else if (results->decode_type == RC6)
    {
      Serial.print("Decoded RC6: ");
    }
    Serial.print(results->value, HEX);
    Serial.print(" (");
    Serial.print(results->bits, DEC);
    Serial.println(" bits)");
  }
  Serial.print("Raw (");
  Serial.print(count, DEC);
  Serial.print("): ");

  for (int i = 0; i < count; i++)
  {
    if ((i % 2) == 1)
    {
      Serial.print(results->rawbuf[i]*USECPERTICK, DEC);
    }
    else
    {
      Serial.print(-(int)results->rawbuf[i]*USECPERTICK, DEC);
    }
    Serial.print(" ");
  }
    Serial.println("");
}


void setup() {
  pinMode(recv_pin, INPUT);
  //Serial Port begin
  Serial.begin (9600);
  //Define inputs and outputs
  pinMode(trigPinL, OUTPUT);
  pinMode(echoPinL, INPUT);
  pinMode(trigPinR, OUTPUT);
  pinMode(echoPinR, INPUT);
  pinMode(trigPinF, OUTPUT);
  pinMode(echoPinF, INPUT);
  pinMode(trigPinB, OUTPUT);
  pinMode(echoPinB, INPUT);
  pinMode(motorPwmFL, OUTPUT);
  pinMode(motorPwmFR, OUTPUT);
  pinMode(motorPwmBL, OUTPUT);
  pinMode(motorPwmBR, OUTPUT);
  pinMode(HBridgeFIn1, OUTPUT);
  pinMode(HBridgeFIn2, OUTPUT);
  pinMode(HBridgeFIn3, OUTPUT);
  pinMode(HBridgeFIn4, OUTPUT);
  pinMode(HBridgeBIn1, OUTPUT);
  pinMode(HBridgeBIn2, OUTPUT);
  pinMode(HBridgeBIn3, OUTPUT);
  pinMode(HBridgeBIn4, OUTPUT);
  pinMode(northLed, OUTPUT);
  pinMode(westLed, OUTPUT);
  pinMode(southLed, OUTPUT);
  pinMode(eastLed, OUTPUT);


  /*digitalWrite(northLed, HIGH);
  digitalWrite(westLed, HIGH);
  digitalWrite(southLed, HIGH);
  digitalWrite(eastLed, HIGH);
  delay(500);*/
  
  irrecv.enableIRIn();
  int on = 0;
  unsigned long last = millis();
  
   Serial.println("GY271 TEST");
  Wire.begin();
  // lower I2C clock http://www.gammon.com.au/forum/?id=10896
  TWBR = 78;  // 25 kHz 
  TWSR |= _BV (TWPS0); 
}

  int ultraSonicApply = LOW;
  int IRapply = LOW;

void compass()
{
  bool detect = detectHMC5883L();

  digitalWrite(northLed, LOW);
  digitalWrite(westLed, LOW);
  digitalWrite(southLed, LOW);
  digitalWrite(eastLed, LOW);

  if(!haveHMC5883L) 
  {
    if(detect) 
    {
      haveHMC5883L = true;
      Serial.println("We have HMC5883L, moving on");
      // Put the HMC5883 IC into the correct operating mode
      Wire.beginTransmission(HMC5883L_ADDR); //open communication with HMC5883
      Wire.write(0x02); //select mode register
      Wire.write(0x00); //continuous measurement mode
      Wire.endTransmission();
    }
    else
    {  
      Serial.println("No HMC5883L detected!");
      delay(2000);
      return;
    }
  }
  else
  {
    if(!detect) {
      haveHMC5883L = false;
      Serial.println("Lost connection to HMC5883L!");
      delay(2000);
      return;
    }
  }
  
  int x,y,z; //triple axis data
  
  //Tell the HMC5883 where to begin reading data
  Wire.beginTransmission(HMC5883L_ADDR);
  Wire.write(0x03); //select register 3, X MSB register
  Wire.endTransmission();

 //Read data from each axis, 2 registers per axis
  Wire.requestFrom(HMC5883L_ADDR, 6);
  if(6<=Wire.available()){
    x = Wire.read()<<8; //X msb
    x |= Wire.read(); //X lsb
    z = Wire.read()<<8; //Z msb
    z |= Wire.read(); //Z lsb
    y = Wire.read()<<8; //Y msb
    y |= Wire.read(); //Y lsb 
  }

  float offset_x = (180 + (-10)) / 2;  
  float offset_y = (-300 + (-650)) / 2; 

  float corrected_x = x - offset_x;
  float corrected_y = y - offset_y;

  float angle = atan2(corrected_y, corrected_x);
  //double magnetic_field_strength = sqrt( (x*x) + (y*y) + (z*z) );
  //double kyy = sqrt((x*x)+(x*x));

// corret for when signs are reversed
  if(angle < 0)
  angle +=2*M_PI;
  
  
  
  if (angle > 2*M_PI)
  angle -= 2*M_PI;
  
  

  // rad to deg
    float heading = angle * 180/M_PI;
    
 // Print out heading
 Serial.println(heading);
 if(heading > 330 || heading < 30){
    digitalWrite(northLed, HIGH);
  }
    if(heading > 240 && heading < 300){
    digitalWrite(westLed, HIGH);
  }
    if(heading > 150 && heading < 210){
    digitalWrite(southLed, HIGH);
  }
    if(heading > 60 && heading < 120){
    digitalWrite(eastLed, HIGH);
  }
}

void moveForwards()
{
  ultraSonicApply = HIGH;
  IRapply = LOW;
  digitalWrite(HBridgeFIn1, LOW);
  digitalWrite(HBridgeFIn2, HIGH);
  digitalWrite(HBridgeFIn3, HIGH);
  digitalWrite(HBridgeFIn4, LOW);
  digitalWrite(HBridgeBIn1, LOW);
  digitalWrite(HBridgeBIn2, HIGH);
  digitalWrite(HBridgeBIn3, HIGH);
  digitalWrite(HBridgeBIn4, LOW);
  analogWrite(motorPwmFL, 255);
  analogWrite(motorPwmFR, 255);
  analogWrite(motorPwmBL, 255);
  analogWrite(motorPwmBR, 255);
}

void turnRight()
{
  ultraSonicApply = HIGH;
  IRapply = LOW;
  digitalWrite(HBridgeFIn1, HIGH);
  digitalWrite(HBridgeFIn2, LOW);
  digitalWrite(HBridgeFIn3, HIGH);
  digitalWrite(HBridgeFIn4, LOW);
  digitalWrite(HBridgeBIn1, LOW);
  digitalWrite(HBridgeBIn2, HIGH);
  digitalWrite(HBridgeBIn3, LOW);
  digitalWrite(HBridgeBIn4, HIGH);
  analogWrite(motorPwmFL, 255);
  analogWrite(motorPwmFR, 255);
  analogWrite(motorPwmBL, 255);
  analogWrite(motorPwmBR, 255);
}

void turnLeft()
{
  ultraSonicApply = HIGH;
  IRapply = LOW;
  digitalWrite(HBridgeFIn1, LOW);
  digitalWrite(HBridgeFIn2, HIGH);
  digitalWrite(HBridgeFIn3, LOW);
  digitalWrite(HBridgeFIn4, HIGH);
  digitalWrite(HBridgeBIn1, HIGH);
  digitalWrite(HBridgeBIn2, LOW);
  digitalWrite(HBridgeBIn3, HIGH);
  digitalWrite(HBridgeBIn4, LOW);
  analogWrite(motorPwmFL, 255);
  analogWrite(motorPwmFR, 255);
  analogWrite(motorPwmBL, 255);
  analogWrite(motorPwmBR, 255);
}

void moveBackwards()
{
  ultraSonicApply = HIGH;
  IRapply = LOW;
  digitalWrite(HBridgeFIn1, HIGH);
  digitalWrite(HBridgeFIn2, LOW);
  digitalWrite(HBridgeFIn3, LOW);
  digitalWrite(HBridgeFIn4, HIGH);
  digitalWrite(HBridgeBIn1, HIGH);
  digitalWrite(HBridgeBIn2, LOW);
  digitalWrite(HBridgeBIn3, LOW);
  digitalWrite(HBridgeBIn4, HIGH);
  analogWrite(motorPwmFL, 255);
  analogWrite(motorPwmFR, 255);
  analogWrite(motorPwmBL, 255);
  analogWrite(motorPwmBR, 255);
}

void IRGo()
{
  IRapply = HIGH;
  ultraSonicApply = LOW;    
  digitalWrite(HBridgeFIn1, HIGH);
  digitalWrite(HBridgeFIn2, LOW);
  digitalWrite(HBridgeFIn3, LOW);
  digitalWrite(HBridgeFIn4, HIGH);
  digitalWrite(HBridgeBIn1, HIGH);
  digitalWrite(HBridgeBIn2, LOW);
  digitalWrite(HBridgeBIn3, LOW);
  digitalWrite(HBridgeBIn4, HIGH);
  analogWrite(motorPwmFL, 255);
  analogWrite(motorPwmFR, 255);
  analogWrite(motorPwmBL, 255);
  analogWrite(motorPwmBR, 255);
}
void IRReverse()
{
  IRapply = HIGH;
  ultraSonicApply = LOW;
  digitalWrite(HBridgeFIn1, LOW);
  digitalWrite(HBridgeFIn2, HIGH);
  digitalWrite(HBridgeFIn3, HIGH);
  digitalWrite(HBridgeFIn4, LOW);
  digitalWrite(HBridgeBIn1, LOW);
  digitalWrite(HBridgeBIn2, HIGH);
  digitalWrite(HBridgeBIn3, HIGH);
  digitalWrite(HBridgeBIn4, LOW);
  analogWrite(motorPwmFL, 255);
  analogWrite(motorPwmFR, 255);
  analogWrite(motorPwmBL, 255);
  analogWrite(motorPwmBR, 255);
}

void allMotorStop()
{
  IRapply = HIGH;
  ultraSonicApply = LOW;
  digitalWrite(motorPwmFL, LOW);
  digitalWrite(motorPwmFR, LOW);
  digitalWrite(motorPwmBL, LOW);
  digitalWrite(motorPwmBR, LOW);
}

void IRTurnLeft()
{
  IRapply = HIGH;
  ultraSonicApply = LOW;
  digitalWrite(HBridgeFIn1, LOW);
  digitalWrite(HBridgeFIn2, HIGH);
  digitalWrite(HBridgeFIn3, LOW);
  digitalWrite(HBridgeFIn4, HIGH);
  digitalWrite(HBridgeBIn1, HIGH);
  digitalWrite(HBridgeBIn2, LOW);
  digitalWrite(HBridgeBIn3, HIGH);
  digitalWrite(HBridgeBIn4, LOW);
  analogWrite(motorPwmFL, 255);
  analogWrite(motorPwmFR, 255);
  analogWrite(motorPwmBL, 255);
  analogWrite(motorPwmBR, 255);
}
void IRTurnRight()
{
  IRapply = HIGH;
  ultraSonicApply = LOW;
  digitalWrite(HBridgeFIn1, HIGH);
  digitalWrite(HBridgeFIn2, LOW);
  digitalWrite(HBridgeFIn3, HIGH);
  digitalWrite(HBridgeFIn4, LOW);
  digitalWrite(HBridgeBIn1, LOW);
  digitalWrite(HBridgeBIn2, HIGH);
  digitalWrite(HBridgeBIn3, LOW);
  digitalWrite(HBridgeBIn4, HIGH);
  analogWrite(motorPwmFL, 255);
  analogWrite(motorPwmFR, 255);
  analogWrite(motorPwmBL, 255);
  analogWrite(motorPwmBR, 255);
}


void sensorFront()
{
  digitalWrite(trigPinF, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPinF, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPinF, LOW);

  
  pinMode(echoPinF, INPUT);
  durationF = pulseIn(echoPinF, HIGH);

  cmF = (durationF/2) / 29.1;

  Serial.print(cmF);
  Serial.print("cmF");
  Serial.print("   ");
  
}

void sensorRight()
{
  digitalWrite(trigPinR, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPinR, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPinR, LOW);

  
  pinMode(echoPinR, INPUT);
  durationR = pulseIn(echoPinR, HIGH);

  cmR = (durationR/2) / 29.1;

  Serial.print(cmR);
  Serial.print("cmR");
  Serial.print("   ");
  
}

void sensorLeft()
{
  digitalWrite(trigPinL, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPinL, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPinL, LOW);

  
  pinMode(echoPinL, INPUT);
  durationL = pulseIn(echoPinL, HIGH);

  cmL = (durationL/2) / 29.1;

  Serial.print(cmL);
  Serial.print("cmL");
  Serial.print("   ");
  
}

void sensorBack()
{
  digitalWrite(trigPinB, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPinB, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPinB, LOW);

  
  pinMode(echoPinB, INPUT);
  durationB = pulseIn(echoPinB, HIGH);

  cmB = (durationL/2) / 29.1;

  Serial.print(cmB);
  Serial.print("cmB");
  Serial.println("   ");
  
}

void loop() 
{
  int on = 0;
  unsigned long last = millis();
  sensorFront();
  sensorRight();
  sensorLeft();
  sensorBack();
  compass();

  if (ultraSonicApply == HIGH && IRapply == LOW)
  {
   if ( cmF > 25 && cmR > 25 && cmL > 25 && ultraSonicApply == HIGH)
   {
    moveForwards();
   }
   else if ( cmR < 25 && cmL > 25 && ultraSonicApply == HIGH)
   {
    turnLeft();
   }
   else if ( cmR > 25 && cmL < 25 && ultraSonicApply == HIGH)
   {
    turnRight();
   }
   else if ( cmF <= 20 && ultraSonicApply == HIGH/*|| cmR <= 15 || cmL <= 15*/)
   {
    moveBackwards(); 
   }
  } 
  
  if (irrecv.decode(&results))
  {
    if (millis() - last > 250)
    {
      on = !on;
      digitalWrite(13, on ? HIGH : LOW);
      dump(&results);
    }

    if (results.value == Go)
    {
      IRapply = HIGH;
      ultraSonicApply = LOW;
      IRGo();
     // segment3();
    }
    if (results.value == Reverse)
    {
      IRapply = HIGH;
      ultraSonicApply = LOW;
      IRReverse();
     // segment4();
    }
    if (results.value == Stop)
    {
      IRapply = HIGH;
      ultraSonicApply = LOW;
      allMotorStop(); 
    //  segment5();   
    }
    if (results.value == TurnLeft)
    {
      IRapply = HIGH;
      ultraSonicApply = LOW;
      IRTurnLeft();
    //  segment6();
    }
    if (results.value == TurnRight)
    {
      IRapply = HIGH;
      ultraSonicApply = LOW;
      IRTurnRight();
     // segment7();
    }
    if (results.value == IRapplyOFF)
    {
      IRapply = LOW;
      ultraSonicApply = HIGH;
    }
    if (results.value == IRapplyON)
    {
      IRapply = HIGH;
      ultraSonicApply = LOW;
    }
      
  last = millis();
  irrecv.resume();
  }   
}
