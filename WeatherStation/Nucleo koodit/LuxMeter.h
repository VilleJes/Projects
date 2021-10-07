#ifndef MBED_LUXMETER_H
#define MBED_LUXMETER_H

#include "mbed.h"

class LuxMeter {
        public:
                LuxMeter();
                ~LuxMeter();
                void calculateLux(float paramLux); 
                float printLux();

        private:

                float voltageIn = 3.3;
                int resistor = 10000;
                int luxScalar = 12518931;
                float luxExponent = -1.405;
                float voltage;
                float LDRVoltage;
                float LDRresistance;
                float lux;
                float rawData;
};
#endif