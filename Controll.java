import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 * Startpoint of the Programm.
 * Class for creating the URLs and calling up the LanuvParser.class 
 *
 * @author Sven M
 */

public class Controll {
	
	//Logger
	private static Logger logger = iniLogger();
	
	//Dateinformation for controll the parser
	public static Calendar aCal = Calendar.getInstance();
	public static int day = aCal.get(Calendar.DATE);
	public static int month = (aCal.get(Calendar.MONTH) + 1);
	public static int year = aCal.get(Calendar.YEAR);
	public static String sDay = calDay(day);
	public static String sMonth = calMonth (month);
	
	public static int hour = 0;
	public static int min = 0;
	
	//Needed Boolean to give the parser information when to parse
	private static boolean first = false;
	
	/**
	 * Main-Method. Initiate the Parsing of the Lanuv-Stations and fill the database with the metadata if its needed.
	 */
	public static void main (String[] args){
		logger.info("Programm start");
		//Try-Block
		try {
			//Fill the Database with the metadata information
			logger.info("Start try-Block");
			logger.info("Check and initial the DB tables");
			DBConnection.addNewFeature("Weseler", LanuvSensor.infoListWeseler);
			DBConnection.addNewFeature("Geist", LanuvSensor.infoListGeist);
			LanuvParser.addLanuvPhenomena();
			LanuvParser.addLanuvProcedures();
			LanuvParser.addLanuvOfferings();
			LanuvParser.linkPhenomenonToOffering();
			LanuvParser.linkProcedureToOffering();
			LanuvParser.linkProcedureToPhenomenon();
			LanuvParser.linkProceduresToFeatureOfInterest("Weseler");
			LanuvParser.linkProceduresToFeatureOfInterest("Geist");
			LanuvParser.linkFeatureOfInterestToOfferings("Weseler");
			LanuvParser.linkFeatureOfInterestToOfferings("Geist");
			logger.info("Finished initial DB");
			
			//Start the parser
			logger.info("Start parsing:");
			//Start parsing the Wesler-Lanuv-Station
			parseUrl("Weseler");
			//Finished Weseler-Lanuv-Station and start with the Geist-Lanuv-Station
			parseUrl("Geist");
			logger.info("Finished parsing");
			//DBConnection.printDataList(); //Method to show all data in the observation tabelle
			logger.info("Finished try-block");
		} catch (IOException e) {
			logger.warn("Unsolved Problem");
			e.printStackTrace();
		}
		//Programm end
		logger.info("End program");
		
	}
	

	/**
	 * Method that cast a Integer to a String and add a zero of the number is smaller as 10
	 * Is needed to create the url of the Lanuv-Station.
	 * @param d is a day of a month as an Integer
	 * @return d as a String with a added zero if d < 10
	 */
	public static String calDay(int d){
		String sDay;
		switch (d){
		case 1: case 2: case 3: case 4: case 5: case 6: case 7:
		case 8: case 9:
			sDay = "0" + Integer.toString(d);
			break;
		default:
			sDay = Integer.toString(d);
			break;
		}
		return sDay;
	}
	

	/**
	 * Method that cast a Integer to a String and add a zero of the number is smaller as 10
	 * Is needed to create the url of the Lanuv-Station.
	 * @param m is a month of a year as an Integer
	 * @return m as a String with a added zero if m < 10
	 */
	public static String calMonth (int m) {
		String sMonth;
		
		switch (m){
		case 1: case 2: case 3: case 4: case 5: case 6: case 7:
		case 8: case 9:
			sMonth = "0" + Integer.toString(m);
			break;
		default:
			sMonth = Integer.toString(m);
			break;
		}
		return sMonth;
		
	}
	
	/**
	 * Method that is the starting-point to parse a Lanuv-Station.
	 * Calls up all the Parser-Methods for all dates.
	 * @param station is the Name of the Lanuv Station as a String
	 * @throws Exception
	 */
	public static void parseUrl (String station) throws IOException {
		//get the last saved timestamp for the Lanuv-Station
		int[] dbDate = DBConnection.getLastTimeStamp(station);
		int y = dbDate[0];
		int m = dbDate[1]; 
		int d = dbDate[2];
		hour = dbDate[3];
		min = dbDate[4];

		//get the days of a month
		int countD = daysOfMonth(y, m, d);
		//create the date-String and call up the parser
		if (!first){
			String date = calMonth(m) + calDay(d);
			startParser(station, date, m, d, first);
			first = true;
			d++;
			//stops the parser if the days of the month is reached
			if (d >= countD){
				m++;
				countD = daysOfMonth(y, m, d);
				d = 1;
			}
		}
		
		//parse as long as the month is the actually month
		while (m <= month){
			if (m < month){
					while (d <= countD){
					String date = calMonth(m) + calDay(d);
					startParser(station, date, m, d, first);
					if (d == countD){
						m++;
						d = 1;
						countD = daysOfMonth(y, m, d);
						break;
					}
					d++;
				}
			}
			else if(m == month){
				while (d <= day){
					String date = calMonth(m) + calDay(d);
					startParser(station, date, m, d, first);
					if (d == day){
						m++;
					}
					d++;
				}
				break;
			}
		}
		first = false;
	}

	
	/**
	 * Method that call up the right methods for the Lanuv-Station that should be parsed.
	 * Also save the Measured values in a ArrayList and write the results in the Database. 
	 * @param station is the Name of the Lanuv-Station as a String
	 * @param date is the date String with added zeros if needed.
	 * @param m is the last saved month
	 * @param d is the last saved day
	 * @param first is a control boolean
	 * @throws Exception
	 */
	public static void startParser (String station, String date, int m, int d, boolean first) throws IOException {
		ArrayList<Measurement> ltmp;
		if (station == "Weseler")
		{
			logger.info("Weseler: ");
			String urlw = weselerUrl(date);
			String tmp = LanuvParser.urlParser(urlw);
			ltmp = LanuvParser.lanuvMeasurement(tmp);
			LanuvParser.saveMeasurement(ltmp, "Weseler", calMonth(m), calDay(d), first);
		}
		else if (station == "Geist"){
			logger.info("Geist: ");
			String urlg = geistUrl(date);
			String tmp = LanuvParser.urlParser(urlg);
			ltmp = LanuvParser.lanuvMeasurement(tmp);
			LanuvParser.saveMeasurement(ltmp, "Geist", calMonth(m), calDay(d), first);
		}
		else {
			logger.warn("No Valid lanuv-Station");
			System.out.println("No valid lanuv-station");
		}
		
	}
	
	//gibt anzahl der tage des monats zurück.
	/**
	 * Method that returns the days of a month
	 * @param y is the year as a int
	 * @param m is the month as a int
	 * @param d is the day as a int
	 * @return returns the number of days of the month.
	 */
	public static int daysOfMonth (int y, int m, int d) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(y, m-1, d);
		int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return days;
	}
	
	/**
	 * Method that create the url of the Weseler-Lanuv-Station
	 * @param s is the Name of the Lanuv Station as a String
	 * @return returns the URL as a String
	 */
	static public String weselerUrl (String s ){
		//WeselerStr Lanuv-Station
		String wUrl = "http://www.lanuv.nrw.de/luft/temes/" + s + "/VMS2.htm";
		logger.info("Weseler Lanuv-Station URL: " + wUrl);
		return wUrl;
	}
	

	/**
	 * Method that create the url of the Geist-Lanuv-Station
	 * @param s is the Name of the Lanuv Station as a String
	 * @return returns the URL as a String
	 */
	static public String geistUrl (String s ){
		//Geiststr Lanuv-Station
		String gUrl = "http://www.lanuv.nrw.de/luft/temes/" + s + "/MSGE.htm";
		logger.info("Geist Lanuv-Station URL: " + gUrl);
		return gUrl;
	}
	

	/**
	 * Method that initiate the Logger
	 */
	static private Logger iniLogger() {
		Logger log  = Logger.getLogger(Controll.class);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("log4j.properties");
		PropertyConfigurator.configure(url);
		return log;
	}
}
