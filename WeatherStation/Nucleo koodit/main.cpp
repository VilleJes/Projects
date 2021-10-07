#include "mbed.h"

#include "LuxMeter.h"
#include "Dht.h"

AnalogIn luxSensorVal(A0);
//Dht11 sensor(A1);
DHT sensor(A1, DHT11);

LuxMeter objLuxMeter;
//Serial pc(PA_2, PA_3, 9600);
Serial pc(USBTX, USBRX);


int main()
{
	//wait_ms(1000);
	while(true)
	{
		luxSensorVal.read();		
		objLuxMeter.calculateLux(luxSensorVal);
		pc.printf("%.2f ", objLuxMeter.printLux());

		sensor.readData();		
		printf("%.2f ",sensor.ReadTemperature(CELCIUS));
		printf("%.2f\r\n",sensor.ReadHumidity());
		wait_ms(2000); 
	}
	//wait_ms(5000);
}
