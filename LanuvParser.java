/**
 * The class is needed to parse the measured values from the Lanuv-Page.
 * The parsed values are saved by the Measurement class.
 *
 * @author Sven M
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LanuvParser {
	
	
	private static Logger logger = iniLogger();
	
	public static void main (String[] args){
	
	}
	
	
	/**
	 * Function to handle the values of the Lanuv-Station and to save them into an arrayList.
	 * This list can be used to save the values in a database
	 * 
	 * @param s is the name of a Lanuv-Station as a String
	 * @return an ArrayList with the Measurements of the Station. 
	 */
	public static ArrayList<Measurement> lanuvMeasurement(String s) {
		logger.info("Save Values from the Lanuv-Website to an ArrayList");
        //Add the String "SPLIT" to separate the Measurement Values from the Page
		String[] loadTab = s.split("SPLIT");
		// Create an ArrayList to save the measured values
        ArrayList<Measurement> nMeasurementValues = new ArrayList<Measurement>();
        
        //Use a for-loop plus patterns to get the values out of the String of the Website.
        for (String token:loadTab) {
        		//First get the Time-Value out of the String
        		if(Pattern.matches("Zeit:([0-9])([0-9]):([0-9])([0-9]).*?", token)){
        			Measurement nMWValue = new Measurement();
        			nMeasurementValues.add(nMWValue);
        			//Use the token.split to separete the hours from the minutes
        			String[] time = token.split(":");
        			// Cast the Strings to Integer and save it
        			nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setHour(Integer.parseInt(time[1]));
        			nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setMin(Integer.parseInt(time[2]));
        			//Save the Time
        			nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setTime(time[1] + ":" + time[2]);      			
        			
        			
        			/*
        			 * Gets the values form the String and save them in the Measurement class 
        			 * String structure:
        			 * Zeit:00:00: Ozon:0: NO: 0: NO2: 0: LTEM: 0: WRI:  0:WGES:  0: RFEU:0: SO2: 0:  Staub/PM10: 0
        			 * [0] [1] [2] [3] [4] [5][6] [7] [8] [9]  [10][11] [12][13] [14][15][16][17] [18] [19]      [20]
        			 */
        			//Check if there is a ozon Value and save it
        			if(Pattern.matches(".*?Ozon:[0-9]+.*?", token)){
        				nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setOzon(Integer.parseInt(time[4]));
        			}
        			//Check if there is a NO Value and save it
        			if(Pattern.matches(".*?NO:[0-9]+.*?", token)){
        				nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setNo(Integer.parseInt(time[6]));
        			}
        			//Check if there is a NO2 Value and save it
        			if(Pattern.matches(".*?NO2:[0-9]+.*?", token)){
        				nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setNo2(Integer.parseInt(time[8]));
        			}
        			//Check if there is a LTEM Value and save it
        			if(Pattern.matches(".*?LTEM:[0-9]+.*?", token)){
        				nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setLtem(Integer.parseInt(time[10]));
        			}
        			//Check if there is a WRI Value and save it
        			if(Pattern.matches(".*?WRI:[0-9]+.*?", token)){
        				nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setWri(Integer.parseInt(time[12]));
        			}
        			//Check if there is a WGES Value and save it
        			if(Pattern.matches(".*?WGES:[0-9]+.*?", token)){
        				nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setWges(Integer.parseInt(time[14]));
        			}
        			//Check if there is a RFEU Value and save it
        			if(Pattern.matches(".*?RFEU:[0-9]+.*?", token)){
        				nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setRfeu(Integer.parseInt(time[16]));
        			}
        			//Check if there is a SO2 Value and save it
        			if(Pattern.matches(".*?SO2:[0-9]+.*?", token)){
        				nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setSo2(Integer.parseInt(time[18]));
        			}
        			//Check if there is a PM10 Value and save it
        			if(Pattern.matches(".*?Staub/PM10:[0-9]+.*?", token)){
        				nMeasurementValues.get(nMeasurementValues.indexOf(nMWValue)).setPm10(Integer.parseInt(time[20]));
        			}        			
        		}else
        			//Without a time-value the Programm cant save the Values from the page.
        			logger.info("No time-value, no measured data to save.");
        }
        //Return the ArrayList with the saved values.
        return nMeasurementValues;
	}


	/**
	 * Method to save the information from the ArrayList in the Database. 
	 * 
	 * @param myMValues ArrayList with the measured values from the Lanuv-Station
	 * @param station name of the Lanuv-Station as a String
	 * @param month the month of the measurement
	 * @param day the day of the measurement
	 * @param first control boolean
	 */
	public static void saveMeasurement (ArrayList<Measurement> myMValues, String station, String month, String day, boolean first ) {
		//Create boolean values to avoid that the date is saved twice. Also to advance that the Date is saved the right time
		boolean save = first;
		boolean checked = false;
		
		//Save the Values from the ArrayList to the DB with the DBConnection.insertData method
		for (Measurement m : myMValues) {
			if(save){
				// Save the measured values that both station collects. Only Save values that not NULL
				if(m.getNo() > -1){
					//logger.info(Controll.year + "-" + month + "-" + day + " " + m.getTime() + ":00 "+ m.getNo() + " " + station + LanuvSensor.getPhenomen_id_no());
					DBConnection.insertData( Controll.year + "-" + month + "-" + day + " " + m.getTime() + ":00",
							LanuvSensor.getProcedure_id_no(), station, LanuvSensor.getPhenomen_id_no(), LanuvSensor.getNoOffering(),
	    					Double.toString(m.getNo()), m.getNo());
				}
				if(m.getNo2() > -1){
					//logger.info(Controll.year + "-" + month + "-" + day + " " + m.getTime() + ":00" + m.getNo2()+LanuvSensor.getPhenomen_id_no2());
					DBConnection.insertData( Controll.year + "-" + month + "-" + day + " " + m.getTime() + ":00",
							LanuvSensor.getProcedure_id_no2(), station, LanuvSensor.getPhenomen_id_no2(), LanuvSensor.getNo2Offering(),
	    					Double.toString(m.getNo2()), m.getNo2());
				}
				if(m.getpm10() > -1) {
					DBConnection.insertData( Controll.year + "-" + month + "-" + day + " " + m.getTime() + ":00",
							LanuvSensor.getProcedure_id_pm10(), station, LanuvSensor.getPhenomen_id_pm10(), LanuvSensor.getPm10Offering(),
	    					Double.toString(m.getpm10()), m.getpm10());
				}
				
				//The Geist-Station is collecting more date than the Weseler-Station. Also only save date that is not NULL
				if(station == "Geist"){
					if(m.getSo2() > -1){
						DBConnection.insertData( Controll.year + "-" + month + "-" + day + " " + m.getTime() + ":00",
								LanuvSensor.getProcedure_id_so2(), station, LanuvSensor.getPhenomen_id_so2(), LanuvSensor.getSo2Offering(),
		    					Double.toString(m.getSo2()), m.getSo2());
					}
					
					if(m.getOzon() > -1){
						DBConnection.insertData( Controll.year + "-" + month + "-" + day + " " + m.getTime() + ":00",
		    					LanuvSensor.getProcedure_id_ozon(), station, LanuvSensor.getPhenomen_id_ozon(), LanuvSensor.getO3Offering(),
		        					Double.toString(m.getOzon()), m.getOzon());
					}
					
				}
			}
			//Break Terms for the for-loop
			//Break terms for "today"
			if (Controll.hour +1 == m.getHour() && !checked){
				if(Controll.min == m.getMin()){
					save = true;
					checked = true;
				}
			}
			//Break term for all else
			else if((Controll.hour )  == 23 && Controll.min == 30 && !checked){
				save = true;
				checked = true;
			}
			
		}
	}

	/**
	 * Method that connect to a URL of a Lanuv-Station and parse the needed information from it.
	 *  
	 * @param lanuvURL the URL of the Lanuv-Station
	 * @return String with the measured values
	 * @throws IOException
	 */
	public static String urlParser (String lanuvURL) throws IOException{ 
	//funktion void parser (URL + Station)
			//Create Strings to save the website-code and filter the need values
			String sourceLine;
			String tabelle = "";
					
			//Committed URL
			URL adress = new URL(lanuvURL);
			
			//Set Buffers
			InputStreamReader pageInput = new InputStreamReader(adress.openStream());
			BufferedReader source = new BufferedReader(pageInput);

			//Create Tags to identify the measured values form the page
			String[] indicator = {"Zeit:","\n",":Ozon:",":NO:",":NO2:",":LTEM:",":WRI:",":WGES:",
									":RFEU:",":SO2:",":Staub/PM10:", "\n"};

			//Use the previously created Tags
			int i = 0;
			while ((sourceLine = source.readLine()) != null){
				Pattern tag = Pattern.compile("<td.*?class.*?>");
				Matcher mtag = tag.matcher(sourceLine);
				if (mtag.matches()){
					tabelle += indicator[i] + sourceLine + "\n";
					i++;
					i = i%11;
				}
			}
			
			//Different regular expressions to delete all the unused code from the HTML-Code
			Pattern tag = Pattern.compile("<.*?>");
	        Matcher mtag = tag.matcher(tabelle);
	        while (mtag.find()) tabelle = mtag.replaceAll("");
	        
	        Pattern sChar = Pattern.compile("&.*?;");
	        Matcher msChar = sChar.matcher(tabelle);
	        while (msChar.find()) tabelle = msChar.replaceAll("");

	        Pattern trenn = Pattern.compile("Zeit:");
	        Matcher mtrenn = trenn.matcher(tabelle);
	        while (mtrenn.find()) tabelle = mtrenn.replaceAll("SPLITZeit:");
	        
	        Pattern nLine = Pattern.compile("\\s");
	        Matcher mnLine = nLine.matcher(tabelle);
	        while (mnLine.find()) tabelle = mnLine.replaceAll("");
	        
	        //close the buffers
	        pageInput.close();
	        source.close();
	        //return the coleceted values
	        return tabelle;
	
	}

	/**
	 * Method to check and fill the phenomena-data in the Database.  Use methods from the DBConnection class.
	 */
	public static void addLanuvPhenomena(){
		//co
		DBConnection.addPhenomenon(LanuvSensor.getPhenomen_id_no(), "nitric oxide", "µg/m³", "numericType");
		//humidity
		DBConnection.addPhenomenon(LanuvSensor.getPhenomen_id_no2(), "nitrogen dioxide", "µg/m³", "numericType");
		//NO2
		DBConnection.addPhenomenon(LanuvSensor.getPhenomen_id_so2(), "sulfur dioxide", "µg/m³", "numericType");
		//temperature
		DBConnection.addPhenomenon(LanuvSensor.getPhenomen_id_ozon(), "ozon", "µg/m³", "numericType");
		//O3
		DBConnection.addPhenomenon(LanuvSensor.getPhenomen_id_pm10(), "pm10", "µg/m³", "numericType");
	}

	/**
	 * Method to check and fill the procedures-data in the Database. Use methods from the DBConnection class.
	 */
	public static void addLanuvProcedures(){
		//co procedure //"urn:ogc:object:feature:Sensor:AQE:co-sensor"
		DBConnection.addProcedure(LanuvSensor.getProcedure_id_no());
		//humidity procedure //"urn:ogc:object:feature:Sensor:AQE:humidity-sensor"
		DBConnection.addProcedure(LanuvSensor.getProcedure_id_so2());
		//NO2 procedure // "urn:ogc:object:feature:Sensor:AQE:no2-sensor"
		DBConnection.addProcedure(LanuvSensor.getProcedure_id_no2());
		//temperature procedure // "urn:ogc:object:feature:Sensor:AQE:temperature-sensor"
		DBConnection.addProcedure(LanuvSensor.getProcedure_id_pm10());
		//O3 procedure // "urn:ogc:object:feature:Sensor:AQE:o3-sensor"
		DBConnection.addProcedure(LanuvSensor.getProcedure_id_ozon());
	}


	/**
	 * Method to check and fill the offering-data in the Database. Use methods from the DBConnection class.
	 */
	public static void addLanuvOfferings(){
		//co offering // "CO_CONCENTRATION"
		DBConnection.addOffering(LanuvSensor.getNoOffering(),"Concentration of nitric oxide in the air");
		//humidity offering // "AIR_HUMIDITY"
		DBConnection.addOffering(LanuvSensor.getSo2Offering(), "Concentration of sulfur dioxide in the air");
		//NO2 offering // "NO2_CONCENTRATION"
		DBConnection.addOffering(LanuvSensor.getNo2Offering(), "Concentration of nitrogen dioxide in the air");
		//temperature offering // "TEMPERATURE"
		DBConnection.addOffering(LanuvSensor.getPm10Offering(), "Concentration of PM10 in the air");
		//O3 offering // "O3_CONCENTRATION"
		DBConnection.addOffering(LanuvSensor.getO3Offering(), "Concentration of ozone dioxide in the air");
	}
	

	/**
	 * Method to link the phenomenon-data to the offering-data in the Database. Use methods from the DBConnection class.
	 */
	public static void linkPhenomenonToOffering(){
		String tableName = "phen_off";
		String firstCol = "phenomenon_id";
		String secondCol = "offering_id";
		//co 
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getPhenomen_id_no(), LanuvSensor.getNoOffering());
		//humidity 
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getPhenomen_id_so2(), LanuvSensor.getSo2Offering());
		//NO2 
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getPhenomen_id_no2(), LanuvSensor.getNo2Offering());
		//temperature
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getPhenomen_id_pm10(), LanuvSensor.getPm10Offering());
		//O3
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getPhenomen_id_ozon(), LanuvSensor.getO3Offering());
	}


	/**
	 * Method to link the procedure-data to the offering-data in the Database. Use methods from the DBConnection class.
	 */
	public static void linkProcedureToOffering(){
		String tableName = "proc_off";
		String firstCol = "procedure_id";
		String secondCol = "offering_id";
		//co 
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_no(), LanuvSensor.getNoOffering());
		//humidity
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_so2(), LanuvSensor.getSo2Offering());
		//NO2 
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_no2(), LanuvSensor.getNo2Offering());
		//temperature 
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_pm10(), LanuvSensor.getPm10Offering());
		//O3
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_ozon(), LanuvSensor.getO3Offering());
	}
	

	/**
	 * Method to link the procedure-data to the phenomenon-data in the Database. Use methods from the DBConnection class.
	 */
	public static void linkProcedureToPhenomenon(){
		String tableName = "proc_phen";
		String firstCol = "procedure_id";
		String secondCol = "phenomenon_id";
		//co procedure
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_no(), LanuvSensor.getPhenomen_id_no());
		//humidity procedure
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_so2(), LanuvSensor.getPhenomen_id_so2());
		//NO2 procedure
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_no2(), LanuvSensor.getPhenomen_id_no2());
		//temperature procedure
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_pm10(), LanuvSensor.getPhenomen_id_pm10());
		//O3 procedure
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_ozon(), LanuvSensor.getPhenomen_id_ozon());
	}
	

	/**
	 * Method to link the feature_of_interest-data to the offering-data in the Database. Use methods from the DBConnection class.
	 */
	public static void linkFeatureOfInterestToOfferings(String feedID){
		String tableName = "foi_off";
		String firstCol = "feature_of_interest_id";
		String secondCol = "offering_id";
		//co offering
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, feedID, LanuvSensor.getNoOffering());
		//humidity offering
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, feedID, LanuvSensor.getSo2Offering());
		//NO2 offering
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, feedID, LanuvSensor.getNo2Offering());
		//temperature offering
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, feedID, LanuvSensor.getPm10Offering());
		//O3 offering
		DBConnection.linkTwoTables(tableName, firstCol, secondCol, feedID, LanuvSensor.getO3Offering());
	}
	

	/**
	 * Method to link the procedure-data to the feature_of_interest-data in the Database. Use methods from the DBConnection class.
	 */
	public static void linkProceduresToFeatureOfInterest(String feedID){
			String tableName = "proc_foi";
			String firstCol = "procedure_id";
			String secondCol = "feature_of_interest_id";
			//co procedure
			DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_no(), feedID);
			//humidity procedure
			DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_so2(), feedID);
			//NO2 procedure
			DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_no2(), feedID);
			//temperature procedure
			DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_pm10(), feedID);
			//O3 procedure
			DBConnection.linkTwoTables(tableName, firstCol, secondCol, LanuvSensor.getProcedure_id_ozon(), feedID);
	}
	
	/**
	 * Method that initiate the Logger
	 */
	static private Logger iniLogger() {
		Logger log  = Logger.getLogger(LanuvParser.class);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("log4j.properties");
		PropertyConfigurator.configure(url);
		return log;
	}
}