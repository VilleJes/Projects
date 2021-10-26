import serial
import datetime
import sqlite3
from time import sleep
import threading


class Weather_Station:

	def __init__(self, station_name, port, baudrate, timeout, db_name):
		self.station_name = station_name
		self.port = port
		self.baudrate = baudrate
		self.timeout = timeout
		self.con = sqlite3.connect(db_name, check_same_thread=False)
		self.ser = serial.Serial(port=self.port, baudrate=self.baudrate, timeout=self.timeout)

	def serial_data(self):
		lux = 0
		temp = 0
		humidity = 0

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
				print(threading.current_thread())
				#print(date)
				#print("\n")


				c.execute(f"INSERT INTO weather(date, sensorName, lux, temp, humidity) VALUES (?, ?, ?, ?, ?)", 
																(date, self.station_name, lux, temp, humidity))
				self.con.commit()
				sleep(1)
