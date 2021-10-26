using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Threading;

namespace TestiDB
{
    public partial class Form1 : Form
    {
        bool clikkeri1, clikkeri2, clikkeri3 = true;
        private System.Windows.Forms.Timer timer1;
        public Form1()
        {
            InitializeComponent();
        }

        //Interval for chart updating
        //set to update once every 60 seconds
        public void InitTimer()
        {
            timer1 = new System.Windows.Forms.Timer();
            timer1.Tick += new EventHandler(timer1_Tick);
            timer1.Interval = 100;
            timer1.Start();
        }

        //what the chart update tick does
        private void timer1_Tick(object sender, EventArgs e)
        {
            //create SQLiteConnection object from SqliteConnection class
            SqliteConnection sqlConnection = new SqliteConnection();

            clearCharts();

            //create CurrentWeather object
            //read current weather
            //created sqlConnection is sent as parameter to establsih connection and execute query
            CurrentWeather currentW = new CurrentWeather();
            currentW.ReadCurrentWeather(sqlConnection.CreateConnection());

            //create ReadWeather30 object
            //read last 30 weather datapoints
            //created sqlConnection is sent as parameter to establsih connection and execute query
            ReadWeather30 read30 = new ReadWeather30();
            read30.readWeather30(sqlConnection.CreateConnection());

            //create ReadWeather object
            //read complete weather history
            //created sqlConnection is sent as parameter to establsih connection and execute query
            ReadWeather readW = new ReadWeather();
            readW.readWeather(sqlConnection.CreateConnection());
        }

        //On form load populate charts with data and create connection to the database
        private void Form1_Load(object sender, EventArgs e)
        {
            chart1.ChartAreas["ChartArea1"].AxisX.MajorGrid.Enabled = false;

            chartLightlevel.ChartAreas["ChartArea1"].AxisX.MajorGrid.Enabled = false;
            chartLightlevel.ChartAreas["ChartArea1"].AxisX.LabelStyle.Enabled = false;

            chartTemperature.ChartAreas["ChartArea1"].AxisX.MajorGrid.Enabled = false;
            chartTemperature.ChartAreas["ChartArea1"].AxisX.LabelStyle.Enabled = false;

            chartHumidity.ChartAreas["ChartArea1"].AxisX.MajorGrid.Enabled = false;
            chartHumidity.ChartAreas["ChartArea1"].AxisX.LabelStyle.Enabled = false;

            //start the timer for chart updating
            Thread timerThread = new Thread(new ThreadStart(InitTimer));
            //InitTimer();
            //create SQLiteConnection object from SqliteConnection class
            SqliteConnection sqlConnection = new SqliteConnection();

            //create CurrentWeather object
            //read current weather
            //created sqlConnection is sent as parameter to establsih connection and execute query
            CurrentWeather currentW = new CurrentWeather();
            currentW.ReadCurrentWeather(sqlConnection.CreateConnection());

            //create ReadWeather30 object
            //read last 30 weather datapoints
            //created sqlConnection is sent as parameter to establsih connection and execute query
            ReadWeather30 read30 = new ReadWeather30();
            read30.readWeather30(sqlConnection.CreateConnection());

            //create ReadWeather object
            //read complete weather history
            //created sqlConnection is sent as parameter to establsih connection and execute query
            ReadWeather readW = new ReadWeather();
            readW.readWeather(sqlConnection.CreateConnection());
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

