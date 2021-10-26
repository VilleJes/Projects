using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.SQLite;
using System.Threading;

namespace TestiDB
{
    class CurrentWeather
    {
        //Function to show the last recorded values for weather data
        public void ReadCurrentWeather(SQLiteConnection sqlConnection)
        {
            WeatherData wData = new WeatherData();

            //with the parametered SQLiteConnection create a command for quering the data
            SQLiteDataReader sqlite_datareader;
            SQLiteCommand sqlite_cmd;
            sqlite_cmd = sqlConnection.CreateCommand();
            sqlite_cmd.CommandText = "SELECT lux, temp, humidity, date FROM weather order by ROWID desc limit 1";

            sqlite_datareader = sqlite_cmd.ExecuteReader();
            while (sqlite_datareader.Read())
            {
                //get data from different rows in database, column is represented by integer value as parameter
                wData.setLUXreader(sqlite_datareader.GetString(0));
                wData.setTEMPreader(sqlite_datareader.GetString(1));
                wData.setHUMreader(sqlite_datareader.GetString(2));

                string DATEreader = sqlite_datareader.GetString(3);

                //Manipulate date data to only show relevant decimals
                DATEreader = DATEreader.Remove(DATEreader.Length - 7);

                //populate the relevant charts with the data gathered from the database
                Class1.form1.chartLightlevel.Series["Lightlevel"].Points.AddXY(DATEreader, wData.getLUXreader());
                Class1.form1.chartTemperature.Series["Temperature"].Points.AddXY(DATEreader, wData.getTEMPreader());
                Class1.form1.chartHumidity.Series["Humidity"].Points.AddXY(DATEreader, wData.getHUMreader());
            }
            sqlConnection.Close();
        }
    }
}
