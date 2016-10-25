package eu.betaas.service.securitymanager.trustmanager.serviceproxy;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {

    public Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("Kernel32", Kernel32.class);

    /**
     * @see http://msdn2.microsoft.com/en-us/library/aa373232.aspx
     */
    public class SYSTEM_POWER_STATUS extends Structure {
        public byte ACLineStatus;
        public byte BatteryFlag;
        public byte BatteryLifePercent;
        public byte SystemStatusFlag;
        public byte Reserved1;
        public int BatteryLifeTime;
        public int BatteryFullLifeTime;

        @Override
        protected List<String> getFieldOrder() {
            ArrayList<String> fields = new ArrayList<String>();
            fields.add("ACLineStatus");
            fields.add("BatteryFlag");
            fields.add("BatteryLifePercent");  
            fields.add("SystemStatusFlag");
            fields.add("BatteryLifeTime");
            fields.add("BatteryFullLifeTime");
            fields.add("Reserved1");
            return fields;
        }

        /**
         * The AC power status
         */
        public String getACLineStatusString() {
            switch (ACLineStatus) {
                case (0): return "Offline";
                case (1): return "Online";
                default: return "Unknown";
            }
        }

        /**
         * The battery charge status
         */
        public String getBatteryFlagString() {
            switch (BatteryFlag) {
                case (1): return "High, more than 66 percent";
                case (2): return "Low, less than 33 percent";
                case (4): return "Critical, less than five percent";
                case (8): return "Charging";
                case ((byte) 128): return "No system battery";
                default: return "Unknown";
            }
        }

        /**
         * The percentage of full battery charge remaining
         */
        public String getBatteryLifePercent() {
            return (BatteryLifePercent == (byte) 255) ? "Unknown" : BatteryLifePercent + "%";
        }
        
        /**
         * The status of battery saver
         */
        public String getSystemStatusFlagString() {
            switch (SystemStatusFlag) {
                case (0): return "Not engaged";
                case (1): return "Engaged";
                default: return "Unknown";
            }
        }

        /**
         * The number of seconds of battery life remaining
         */
        public String getBatteryLifeTime() {
            return (BatteryLifeTime == -1) ? "Unknown" : BatteryLifeTime + " seconds";
        }

        /**
         * The number of seconds of battery life when at full charge
         */
        public String getBatteryFullLifeTime() {
            return (BatteryFullLifeTime == -1) ? "Unknown" : BatteryFullLifeTime + " seconds";
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ACLineStatus: " + getACLineStatusString() + "\n");
            sb.append("Battery Flag: " + getBatteryFlagString() + "\n");
            sb.append("Battery Life: " + getBatteryLifePercent() + "\n");
            sb.append("System Status Flag: " + getSystemStatusFlagString() + "\n");
            sb.append("Battery Left: " + getBatteryLifeTime() + "\n");
            sb.append("Battery Full: " + getBatteryFullLifeTime() + "\n");
            return sb.toString();
        }
        
        
    }

    /**
     * Fill the structure.
     */
    public int GetSystemPowerStatus(SYSTEM_POWER_STATUS result);    
    
}