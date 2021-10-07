import serial
import datetime, time
import sqlite3



ser = serial.Serial(port='/dev/ttyACM0',baudrate=9600,timeout=2)

con = sqlite3.connect('/home/pi/shared/weatherDBtest.sqlite3')
c = con.cursor()

lux = 0
temp = 0
humidity = 0

def serial_data():
	while True:
		date = datetime.datetime.now()

		line = ser.readline().decode('utf-8').rstrip()
		if line:
			a_string = line.split()
			#print(a_string)

			lux = a_string[0]
			temp = a_string[1]
			humidity = a_string[2]

			print(lux)
			print(temp)
			print(humidity)

			#print(date)
			#print("\n")


			c.execute(f"INSERT INTO weather(date, lux, temp, humidity) VALUES ('{date}', '{lux}', '{temp}', '{humidity}')")
			con.commit()


serial_data()

con.commit()
con.close()

ser.close()


