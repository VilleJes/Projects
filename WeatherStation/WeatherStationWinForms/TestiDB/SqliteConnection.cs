using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.SQLite;


namespace TestiDB
{
    class SqliteConnection
    {
        public SQLiteConnection CreateConnection()
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
    }
}
