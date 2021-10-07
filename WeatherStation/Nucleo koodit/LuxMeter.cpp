#include "LuxMeter.h"
#include "mbed.h"

//Serial pc(USBTX, USBRX);
extern Serial pc;

LuxMeter::LuxMeter()
{
	
}

LuxMeter::~LuxMeter()
{
	
}

void LuxMeter::calculateLux(float paramLux)
{
    //rawData = luxSensorVal.read();
	rawData = paramLux;
	//pc.printf("%f \n", rawData);
		
	voltage = rawData * voltageIn;
	LDRVoltage = voltageIn - voltage;
	LDRresistance = LDRVoltage / voltage * resistor;
	lux = luxScalar * pow(LDRresistance, luxExponent);
	//pc.printf("lux %f\n", lux);

}

float LuxMeter::printLux()
{
	//pc.printf("%f\n", lux);
	return(lux);
}