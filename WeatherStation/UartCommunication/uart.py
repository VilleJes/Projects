import threading

import weather_station_1

w_station_1 = weather_station_1.Weather_Station("w_station_1", 
												'/dev/ttyACM0',
												9600,
												2,
												'/home/pi/shared/weatherDBtest.sqlite3')



class Thread(threading.Thread):
	def __init__(self, t, *args):
		threading.Thread.__init__(self, target=t, args=args)
		self.start()


def main():
	weather_station = Thread(w_station_1.serial_data)


if __name__ == "__main__":
	main()