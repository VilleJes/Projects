import serial
import threading
import datetime, time
import sqlite3
import atexit

import weather_station_1


w_station_1 = weather_station_1.Weather_Station("w_station_1", serial.Serial(port='/dev/ttyACM0',baudrate=9600),
												 sqlite3.connect('/home/pi/shared/weatherDBtest.sqlite3'))

class Thread(threading.Thread):
	def __init__(self, t, *args):
		threading.Thread.__init__(self, target=t, args=args)
		self.start()


def main():
	weather_station = Thread(w_station_1.serial_data())


if __name__ == "__main__":
	main()