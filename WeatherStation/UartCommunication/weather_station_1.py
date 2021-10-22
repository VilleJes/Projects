import serial
import datetime, time
import sqlite3
import atexit
#import threading

class Weather_Station:

	def __init__(self, station_name, ser, con):
		self.station_name = station_name
		self.ser = ser
		self.con = con


	def serial_data(self):
		lux = 0
		temp = 0
		humidity = 0

		#ser = serial.Serial(port='/dev/ttyACM0',baudrate=9600,timeout=2)

		#con = sqlite3.connect('/home/pi/shared/weatherDBtest.sqlite3')
		c = self.con.cursor()

		while True:
			date = datetime.datetime.now()

			line = self.ser.readline().decode('utf-8').rstrip()
			if line:
				a_string = line.split()
				#print(a_string)

				lux = a_string[0]
				temp = a_string[1]
				humidity = a_string[2]

				print(self.station_name)
				print(lux)
				print(temp)
				print(humidity)
				#print(threading.current_thread())
				#print(date)
				#print("\n")


				c.execute(f"INSERT INTO weather(date, sensorName, lux, temp, humidity) VALUES (?, ?, ?, ?, ?)", 
																(date, self.station_name, lux, temp, humidity))
				self.con.commit()
