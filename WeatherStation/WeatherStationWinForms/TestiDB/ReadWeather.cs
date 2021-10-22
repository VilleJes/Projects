using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.SQLite;

namespace TestiDB
{
    class ReadWeather
    {
        //function to show the whole history of recorded weahter data
        public void readWeather(SQLiteConnection sqlite_conn)
        {
            WeatherData wData = new WeatherData();

            //with the parametered SQLiteConnection create a command for quering the data
            SQLiteDataReader sqlite_datareader;
            SQLiteCommand sqlite_cmd;
            sqlite_cmd = sqlite_conn.CreateCommand();
            sqlite_cmd.CommandText = "SELECT lux, temp, humidity, date FROM weather";

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
                Class1.form1.chart1.Series["Lux"].Points.AddXY(DATEreader, wData.getLUXreader());
                Class1.form1.chart2.Series["Temp"].Points.AddXY(DATEreader, wData.getTEMPreader());
                Class1.form1.chart3.Series["Humidity"].Points.AddXY(DATEreader, wData.getHUMreader());
            }
        }
    }
}
