using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Data.SQLite;


namespace TestiDB
{
    public partial class Form1 : Form
    {
        bool clikkeri1, clikkeri2, clikkeri3 = true;
        private Timer timer1;

        public Form1()
        {
            InitializeComponent();
        }

        //Interval for chart updating
        public void InitTimer()
        {
            timer1 = new Timer();
            timer1.Tick += new EventHandler(timer1_Tick);
            timer1.Interval = 60000;
            timer1.Start();
        }

        //what the chart update tick does
        private void timer1_Tick(object sender, EventArgs e)
        {
            SQLiteConnection sqlite_conn;
            sqlite_conn = CreateConnection();

            clearCharts();
            readCurrentWeather(sqlite_conn);
            readWeather(sqlite_conn);
            readWeather30(sqlite_conn);

            sqlite_conn.Close();
        }
        //On form load populate charts with data and create connection to the database
        private void Form1_Load(object sender, EventArgs e)
        {
            SQLiteConnection sqlite_conn;
            sqlite_conn = CreateConnection();
            InitTimer();

            readCurrentWeather(sqlite_conn);
            readWeather(sqlite_conn);
            readWeather30(sqlite_conn);

            chart1.ChartAreas["ChartArea1"].AxisX.MajorGrid.Enabled = false;

            chartLightlevel.ChartAreas["ChartArea1"].AxisX.MajorGrid.Enabled = false;
            chartLightlevel.ChartAreas["ChartArea1"].AxisX.LabelStyle.Enabled = false;

            chartTemperature.ChartAreas["ChartArea1"].AxisX.MajorGrid.Enabled = false;
            chartTemperature.ChartAreas["ChartArea1"].AxisX.LabelStyle.Enabled = false;

            chartHumidity.ChartAreas["ChartArea1"].AxisX.MajorGrid.Enabled = false;
            chartHumidity.ChartAreas["ChartArea1"].AxisX.LabelStyle.Enabled = false;
        }

        //create connection
        static SQLiteConnection CreateConnection()
        {
            SQLiteConnection sqlite_conn;

            //Connection address located on raspberry pi on the same network
            sqlite_conn = new SQLiteConnection("Data source = \\\\raspberrypi\\share\\weatherDBtest.sqlite3");
            sqlite_conn.ParseViaFramework = true;

            try
            {
                sqlite_conn.Open();
            }
            catch (Exception ex)
            {

            }
            //pass the generated connection as parameter to be used with the data read functions
            return sqlite_conn;
        }


        //Function to show last 30 ticks of weather data
        void readWeather30(SQLiteConnection sqlite_conn)
        {
            SQLiteDataReader sqlite_datareader;
            SQLiteCommand sqlite_cmd;
            sqlite_cmd = sqlite_conn.CreateCommand();
            sqlite_cmd.CommandText = "SELECT lux, temp, humidity, date FROM weather order by ROWID desc limit 30";

            sqlite_datareader = sqlite_cmd.ExecuteReader();
            while (sqlite_datareader.Read())
            {
                //get data from different rows in database, represented by integer value as parameter
                string myLUXreader = sqlite_datareader.GetString(0);
                string myTEMPreader = sqlite_datareader.GetString(1);
                string myHUMreader = sqlite_datareader.GetString(2);
                string myDATEreader = sqlite_datareader.GetString(3);

                //Manipulate data to only show relevant decimals
                myDATEreader = myDATEreader.Substring(10);
                myDATEreader = myDATEreader.Remove(myDATEreader.Length - 7);

                chart4.Series["Last 30 Humidity Values"].Points.AddXY(myDATEreader, myHUMreader);
                chart5.Series["Last 30 Celcius Values"].Points.AddXY(myDATEreader, myTEMPreader);
                chart6.Series["Last 30 Lux Values"].Points.AddXY(myDATEreader, myLUXreader);

            }
        }

        //Function to show complete weather data
        void readWeather(SQLiteConnection sqlite_conn)
        {
            SQLiteDataReader sqlite_datareader;
            SQLiteCommand sqlite_cmd;
            sqlite_cmd = sqlite_conn.CreateCommand();
            sqlite_cmd.CommandText = "SELECT lux, temp, humidity, date FROM weather";

            sqlite_datareader = sqlite_cmd.ExecuteReader();
            while (sqlite_datareader.Read())
            {
                //get data from different rows in database, represented by integer value as parameter
                string myLUXreader = sqlite_datareader.GetString(0);
                string myTEMPreader = sqlite_datareader.GetString(1);
                string myHUMreader = sqlite_datareader.GetString(2);
                string myDATEreader = sqlite_datareader.GetString(3);

                //Manipulate data to only show relevant decimals
                myDATEreader = myDATEreader.Remove(myDATEreader.Length - 7);


                chart1.Series["Lux"].Points.AddXY(myDATEreader, myLUXreader);
                chart2.Series["Temp"].Points.AddXY(myDATEreader, myTEMPreader);
                chart3.Series["Humidity"].Points.AddXY(myDATEreader, myHUMreader);

            }
        }

        //Function to show current weather data
        void readCurrentWeather(SQLiteConnection sqlite_conn)
        {
            SQLiteDataReader sqlite_datareader;
            SQLiteCommand sqlite_cmd;
            sqlite_cmd = sqlite_conn.CreateCommand();
            sqlite_cmd.CommandText = "SELECT lux, temp, humidity, date FROM weather order by ROWID desc limit 1";

            sqlite_datareader = sqlite_cmd.ExecuteReader();
            while (sqlite_datareader.Read())
            {
                //get data from different rows in database, represented by integer value as parameter
                string myLUXreader = sqlite_datareader.GetString(0);
                string myTEMPreader = sqlite_datareader.GetString(1);
                string myHUMreader = sqlite_datareader.GetString(2);
                string myDATEreader = sqlite_datareader.GetString(3);

                //Manipulate data to only show relevant decimals
                myDATEreader = myDATEreader.Remove(myDATEreader.Length - 7);


                chartLightlevel.Series["Lightlevel"].Points.AddXY(myDATEreader, myLUXreader);
                chartTemperature.Series["Temperature"].Points.AddXY(myDATEreader, myTEMPreader);
                chartHumidity.Series["Humidity"].Points.AddXY(myDATEreader, myHUMreader);

            }
        }

        //Clear chart data to ready the charts for next injection of data to be displayed
        void clearCharts()
        {
            chartLightlevel.Series["Lightlevel"].Points.Clear();
            chartTemperature.Series["Temperature"].Points.Clear();
            chartHumidity.Series["Humidity"].Points.Clear();

            chart1.Series["Lux"].Points.Clear();
            chart3.Series["Humidity"].Points.Clear();
            chart2.Series["Temp"].Points.Clear();

            chart4.Series["Last 30 Humidity Values"].Points.Clear();
            chart5.Series["Last 30 Celcius Values"].Points.Clear();
            chart6.Series["Last 30 Lux Values"].Points.Clear();
        }


        //Button functionality for selecting what data to display
        private void buttonTemp_Click(object sender, EventArgs e)
        {
           

            if (clikkeri1 == true)
            {
                clikkeri1 = false;
                chart2.Hide();
                chart5.Show();
            }
            else
            {
                clikkeri1 = true;
                chart2.Show();
                chart5.Hide();
            }
        }

        private void buttonLight_Click(object sender, EventArgs e)
        {
            if (clikkeri2 == true)
            {
                clikkeri2 = false;
                chart1.Hide();
                chart6.Show();
            }
            else
            {
                clikkeri2 = true;
                chart1.Show();
                chart6.Hide();
            }
        }

        private void buttonHumidity_Click(object sender, EventArgs e)
        {
            if (clikkeri3 == true)
            {
                clikkeri3 = false;
                chart3.Show();
                chart4.Hide();
            }
            else
            {
                clikkeri3 = true;
                chart3.Hide();
                chart4.Show();
            }
        }
    }
}

