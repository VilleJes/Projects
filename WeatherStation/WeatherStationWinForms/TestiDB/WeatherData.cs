using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TestiDB
{
    class WeatherData
    {
        string LUXreader;
        string TEMPreader;
        string HUMreader;
        string DATEreader;

        public void setLUXreader(string pLUXreader)
        {
            LUXreader = pLUXreader;
        }

        public string getLUXreader()
        {
            return LUXreader;
        }
        public void setTEMPreader(string pTEMPreader)
        {
            TEMPreader = pTEMPreader;
        }

        public string getTEMPreader()
        {
            return TEMPreader;
        }
        public void setHUMreader(string pHUMreader)
        {
            HUMreader = pHUMreader;
        }

        public string getHUMreader()
        {
            return HUMreader;
        }
    }
}
