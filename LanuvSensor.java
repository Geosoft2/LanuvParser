/**
 * This class provides metadata regarding the Lanuv sensors needed for the SOS
 * 
 * @author Sven M.
 * 
 */
import java.util.ArrayList;


public class LanuvSensor {
	
	static ArrayList<String> infoListWeseler = fillInfoListWeseler();	
	static ArrayList<String> infoListGeist = fillInfoListGeist();
	
	//String procedure
	private static String procedure_id_ozon = "urn:ogc:object:feature:Sensor:lanuv-ozon-sensor";
	private static String procedure_id_no = "urn:ogc:object:feature:Sensor:lanuv-no-sensor";
	private static String procedure_id_no2 = "urn:ogc:object:feature:Sensor:lanuv-no2-sensor";
	private static String procedure_id_so2 = "urn:ogc:object:feature:Sensor:lanuv-so2-sensor";
	private static String procedure_id_pm10 = "urn:ogc:object:feature:Sensor:lanuv-pm10-sensor";
	
	//String offerings
	private static String o3Offering = "O3_CONCENTRATION";
	private static String noOffering = "NO_CONCENTRATION";
	private static String no2Offering = "NO2_CONCENTRATION";
	private static String so2Offering = "SO2_CONCENTRATION";
	private static String pm10Offering = "PM10_CONCENTRATION";
	
	//String phenomenon_description
	private static String phenomenon_description_ozon = "Ozon-1";
	private static String phenomenon_description_no = "NO-1";
	private static String phenomenon_description_no2 = "NO2-1";
	private static String phenomenon_description_so2 = "SO2-1";
	private static String phenomenon_description_pm10 = "PM10-1";
	
	//String unit
	private static String unit_ozon = "microg/m3";
	private static String unit_no = "microg/m3";
	private static String unit_no2 = "microg/m3";
	private static String unit_so2 = "microg/m3";
	private static String unit_pm10 = "microg/m3";
	
	
	//String phenomen_id
	private static String phenomen_id_ozon = "urn:x-ogc:def:phenomenon:OGC:o3:uom:microg/m3";
	private static String phenomen_id_no = "urn:x-ogc:def:phenomenon:OGC:no:uom:microg/m3";
	private static String phenomen_id_no2 = "urn:x-ogc:def:phenomenon:OGC:no2:uom:microg/m3";
	private static String phenomen_id_so2 = "urn:x-ogc:def:phenomenon:OGC:so2:uom:microg/m3";
	private static String phenomen_id_pm10 = "urn:x-ogc:def:phenomenon:OGC:pm10:uom:microg/m3";
	
	//String mit FOI
	public static ArrayList<String> fillInfoListWeseler(){
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.add("Weseler"); //feature_of_interest_id
		tmp.add("Lanuv Station Weseler"); // feature_of_interest_name
		tmp.add("Weseler Str. "); // feature_of_interest_description
		tmp.add("7.619444"); // Längengrad:	 7° 37min 10sec
		tmp.add("51.953333"); // Breitengrad:	 51° 57min 12sec
		tmp.add("http://www.lanuv.nrw.de/luft/messorte/steckbriefe/vms2.htm"); // schema_link
		return tmp;
	}
	
	public static ArrayList<String> fillInfoListGeist(){
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.add("Geist"); //feature_of_interest_id
		tmp.add("Lanuv Station Geist"); // feature_of_interest_name
		tmp.add("Geist Str. "); // feature_of_interest_description
		tmp.add("7.612222"); // Längengrad:	7° 36min 44sec
		tmp.add("51.937778"); // Breitengrad:	51° 56min 16sec
		tmp.add("http://www.lanuv.nrw.de/luft/messorte/steckbriefe/msge.htm"); // schema_link
		return tmp;
	}

	
	//Getters and Setters...
	public static String getProcedure_id_ozon() {
		return procedure_id_ozon;
	}

	public static String getProcedure_id_no() {
		return procedure_id_no;
	}

	public static String getProcedure_id_no2() {
		return procedure_id_no2;
	}

	public static String getProcedure_id_so2() {
		return procedure_id_so2;
	}

	public static String getProcedure_id_pm10() {
		return procedure_id_pm10;
	}
	public static String getNoOffering() {
		return noOffering;
	}

	public static String getNo2Offering() {
		return no2Offering;
	}

	public static String getSo2Offering() {
		return so2Offering;
	}

	public static String getPm10Offering() {
		return pm10Offering;
	}

	public static String getO3Offering() {
		return o3Offering;
	}

	public static String getPhenomenon_description_ozon() {
		return phenomenon_description_ozon;
	}

	public static String getPhenomenon_description_no() {
		return phenomenon_description_no;
	}

	public static String getPhenomenon_description_no2() {
		return phenomenon_description_no2;
	}

	public static String getPhenomenon_description_so2() {
		return phenomenon_description_so2;
	}

	public static String getPhenomenon_description_pm10() {
		return phenomenon_description_pm10;
	}

	public static String getUnit_ozon() {
		return unit_ozon;
	}

	public static String getUnit_no() {
		return unit_no;
	}

	public static String getUnit_no2() {
		return unit_no2;
	}

	public static String getUnit_so2() {
		return unit_so2;
	}

	public static String getUnit_pm10() {
		return unit_pm10;
	}


	public static String getPhenomen_id_ozon() {
		return phenomen_id_ozon;
	}

	public static String getPhenomen_id_no() {
		return phenomen_id_no;
	}

	public static String getPhenomen_id_no2() {
		return phenomen_id_no2;
	}

	public static String getPhenomen_id_so2() {
		return phenomen_id_so2;
	}

	public static String getPhenomen_id_pm10() {
		return phenomen_id_pm10;
	}	
}
