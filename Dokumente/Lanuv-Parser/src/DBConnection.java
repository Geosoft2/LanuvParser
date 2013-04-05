import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.io.IOException;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
 
/**
 * Establish the DB connection and handle all the DB stuff.
 * Have methods to get information from the DB and methods to save data in the DB
 * 
 * @author Sven M
 */

public class DBConnection {
	
	private static Logger logger = iniLogger();
	 
	private Properties properties = new Properties();
	
    private static Connection conn = null;
 
    //Url
    private String dbUrl;
 
    // Databaseuser
    private String dbUser;
 
    // Databasepassword
    private String dbPassword;
 

    /**
	 * Constructor for connection object that establishes connection to local
	 * psql databse using user, pw and url from config.properties.
	 *
	 */
    private DBConnection() {
        logger.info("Establish the Database connection");
    	try {
    		properties.load(DBConnection.class.getResourceAsStream("config.properties"));
    		
    		this.dbUser = (String) properties.getProperty("db_username");
    		this.dbPassword = (String) properties.getProperty("db_password");
    		this.dbUrl = (String) properties.getProperty("db_url");
    				
    		//Load the Databankdriver
            Class.forName("org.postgresql.Driver");
             
            //create the connection to the JDBC-ODBC-Bridge
            conn = DriverManager.getConnection("jdbc:postgresql:"+ dbUrl, dbUser, dbPassword);
        
    	} catch (ClassNotFoundException e) {
    		logger.warn("DB-Driver not found", e);
    		e.printStackTrace();
    		logger.info("Cancel Programm");
			System.exit(1);
    		
        } catch (SQLException e) {
        	logger.warn("Connection not posible", e);
            e.printStackTrace();
            logger.info("Cancel Programm");
			System.exit(1);

		} catch (FileNotFoundException e) {
			logger.warn("File not found", e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
     
    
    /**
	 * Method that get the database Instance
	 */
    private static Connection getInstance()
    {
        if(conn == null)
            new DBConnection();
        return conn;
    }
    
    /**
	 * Method that closes the database connection
	 */
    public static void disconnect() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			logger.warn("Disconnect not posible", e);
			e.printStackTrace();
		}
	}
 
    
    /**
     * Method that return the last saved Timestamp of one Lanuv-Station.
     * If there is no "last timestamp" it returns the timestamp of seven days ago.
     * 
     * @param station name of the Lanuv-Station as a String
     * @return a int[] with year, month, day, hour and min
     */
    public static int[] getLastTimeStamp(String station){
    	//create the int[] to return the time as int
    	int[] dateSet = {};
    	//connect to DB
    	conn = getInstance();
    	//create calendar data-typ
    	Calendar lastDate = Calendar.getInstance();
    	
        if(conn != null)
        {
        	//create DB statement
            Statement query;
            try {
                query = conn.createStatement();

                //Get results from the DB
                String sql = "SELECT max(time_stamp) FROM observation WHERE feature_of_interest_id='" + station +"'";
                ResultSet result = query.executeQuery(sql);
                result.next();
                
                //commit the DB result to the java timestamp
                java.sql.Timestamp timeStamp = result.getTimestamp(1);
                if (timeStamp != null){
                	java.sql.Date date = new java.sql.Date(timeStamp.getTime());
                    
                    lastDate.setTime(date);
                    
                    //commit the date data to the int[]
                    int y = lastDate.get(Calendar.YEAR);
                    int m = lastDate.get(Calendar.MONTH) + 1;
                    int d = lastDate.get(Calendar.DATE);
                    int h = lastDate.get(Calendar.HOUR_OF_DAY);
                    int min = lastDate.get(Calendar.MINUTE);
                    
                    int[] tmp = {y, m, d, h, min};
                    dateSet = tmp;
                }
                else if (timeStamp == null){
                	//if there is no last timestamp in the db than the db is empty and the programm return the last 7 days
                	lastDate.add(Calendar.DATE, -7);
                	
                	int y = lastDate.get(Calendar.YEAR);
                    int m = lastDate.get(Calendar.MONTH) + 1;
                    int d = lastDate.get(Calendar.DATE);
                    int h = lastDate.get(Calendar.HOUR_OF_DAY);
                    int min = lastDate.get(Calendar.MINUTE);
                    
                    int[] tmp = {y, m, d, h, min};
                    dateSet = tmp;
                    
                }
                 
                
            } catch (SQLException e) {
            	logger.warn("Can't connect to the DB", e);
                e.printStackTrace();
                logger.info("Cancel Programm");
				System.exit(1);
            }
        }
        //return the int[] with the last values
        return dateSet;
    }
    
    /**
     * Print all data of the obersavtion table.
     */
    public static void printDataList() 
    {
        conn = getInstance();
         
        if(conn != null)
        {
            // create db statement
            Statement query;
            try {
                query = conn.createStatement();
 
                // get the result sets from the db
                String sql = "SELECT time_stamp, procedure_id, feature_of_interest_id, phenomenon_id," +
                		" offering_id, text_value, numeric_value, spatial_value, observation_id, mime_type"
                		+ " FROM observation"
                        + " ORDER BY observation_id";
                ResultSet result = query.executeQuery(sql);
 
                // print all results
                while (result.next()) {
                    String time_stamp = result.getString("time_stamp");
                    String procedure_id = result.getString("procedure_id");
                    String feature_of_interest_id = result.getString("feature_of_interest_id");
                    String phenomen_id = result.getString("phenomenon_id");
                    String offering_id = result.getString("offering_id");
                    String text_value = result.getString("text_value");
                    String numeric_value = result.getString("numeric_value");
                    String spatial_value = result.getString("spatial_value");
                    String oberservation_id = result.getString("observation_id");
                    String mime_type = result.getString("mime_type");
                    String name = time_stamp + ", " + procedure_id + ", " + feature_of_interest_id + ", " + phenomen_id +
                    		", " + offering_id + ", " + text_value +  ", " + numeric_value + ", " + spatial_value + 
                    		", " + oberservation_id + ", " + mime_type;
                    System.out.println(name);
                }
            } catch (SQLException e) {
            	logger.warn("Can't connect to the Database", e);
                e.printStackTrace();
            }
        }
    }
     
    
    /**
     * Method that insert a new Dataset in the DB. Also call up the writeQuality method
     * 
     * @param time_stamp Timestamp as a String
     * @param procedure_id procedure_id as a String
     * @param feature_of_interest_id FOI as a String
     * @param phenomen_id phenomenon as a String
     * @param offering_id offering_id as a String
     * @param text_value measured value as a String
     * @param numeric_value measured value as a double
     */
    public static void insertData(String time_stamp, String procedure_id, String feature_of_interest_id, String phenomen_id, String offering_id, String text_value, double numeric_value)
    {
        conn = getInstance();
         
        if(conn != null)
        {
            // Create DB statement
            Statement query;
            try {
                query = conn.createStatement();
                
                String sqlC = "SELECT time_stamp, procedure_id, feature_of_interest_id, phenomenon_id, offering_id FROM observation " +
                		"WHERE time_stamp='" + time_stamp + "' AND procedure_id='" + procedure_id +"' AND " +
                				"feature_of_interest_id='"+ feature_of_interest_id+"' AND phenomenon_id='"+ phenomen_id + 
                				"' AND offering_id='" + offering_id+"'";
                
                ResultSet rs = query.executeQuery(sqlC);
                
                if(!rs.next())
                {
                	String sql = "INSERT INTO observation(time_stamp, procedure_id, feature_of_interest_id, phenomenon_id," +
                    		" offering_id, text_value, numeric_value)" +
                    		" VALUES('" + time_stamp + "', '" + procedure_id +"', '" + feature_of_interest_id +
                    		"', '" + phenomen_id +"', '" + offering_id +"', '" + text_value + "', '" + numeric_value + "')";
                    
                	
                    query.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        			//save the keys for linking to quality table
        			ResultSet keyset = query.getGeneratedKeys();
        			keyset.next();
        			//save the observation id
        			int oID = keyset.getInt("observation_id");
        			
                    writeQuality(oID);
                }
                
            } catch (SQLException e) {
            	logger.warn("Can't execute the DBCommand", e);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Method that link the quality of the saved value into the quality-table
     * @param observation_id observation_id of the linked data
     * @throws SQLException
     */
	public static void writeQuality(int observation_id) throws SQLException{
		String query = "INSERT INTO quality (quality_name, quality_unit, quality_value, quality_type, observation_id) VALUES ('outlier','not_tested/yes/no','not_tested','category','"+observation_id+"');";
		Statement stmt = conn.createStatement();
		stmt.execute(query);
	}
	
	/**
	 * Add a new feature in the DB
	 * 
	 * @param feedID unique feed_ID as a String
	 * @param infoList Information about the new Feature as a infoList with information about: feature_of_interest_id, 
	 * feature_of_interest_name, feature_of_interest_description, coordinates, schema_link
	 */
	public static void addNewFeature(String feedID, ArrayList<String> infoList){
		conn = getInstance();
		boolean inDB = true;
		String query = "SELECT COUNT(feature_of_interest_id) FROM feature_of_interest WHERE feature_of_interest_id='"+ feedID +"'";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			//move iterator one step
			rs.next();
			if (rs.getInt("count")!=0) {
				inDB = true;
			} else {
				inDB = false;
			}
		} catch (SQLException e) {
			logger.warn("Can't connect to the DB", e);
			e.printStackTrace();
		}
		if (!inDB){
			query = "INSERT INTO feature_of_interest (feature_of_interest_id, feature_of_interest_name, " +
					"feature_of_interest_description, geom, feature_type, schema_link) VALUES " +
					"('"+infoList.get(0)+"', '"+infoList.get(1)+"', '"+infoList.get(2)+"', " +
							"ST_GeomFromText('POINT("+ infoList.get(4) + " " + infoList.get(3) +")', 4326)," +
									"'sa:SamplingPoint', 'http://xyz.org/reference-url2.html');";
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(query);
			} catch (SQLException e){ 
				logger.warn("Can't execute the DB command", e);
				e.printStackTrace();
			}
			
		}
	}
	
	
    
	/**
	 * Check if there is already the phenomenon in the DB and if not add it.
	 * 
	 * @param phenomenon_id the phenomenon_id as a String
	 * @param phenomenon_description the phenomenon_description as a String
	 * @param unit the unit of the measurement as a String
	 * @param valuetype the valuetype as a String
	 */
    public static void fillPhenomen (String phenomenon_id, String phenomenon_description, String unit, String valuetype) {
    	conn = getInstance();
    	if(conn != null){
    		boolean phenomenonInDatabase = true;
    		Statement query;
    		try{
    			query = conn.createStatement();
    			
    			String sql =  "SELECT COUNT(phenomenon_id) FROM phenomenon WHERE phenomenon_id='"+ phenomenon_id +"';";
    			
    			ResultSet rs = query.executeQuery(sql);
    			rs.next();
    			
    			if (rs.getInt("count") != 0) {
    				phenomenonInDatabase = true;
    			} else {
    				phenomenonInDatabase = false;
    			}
    			if (phenomenonInDatabase) {

    				sql = "INSERT INTO phenomenon VALUES ('" + phenomenon_id
    						+ "', '" + phenomenon_description + "', '" + unit + "','"
    						+ valuetype + "');";
    				try {
    					query = conn.createStatement();
    					query.execute(sql);
    				} catch (SQLException e) {
    					logger.warn("Can't execute the DB command", e);
    					e.printStackTrace();
    				}
    			}
    		}
    		catch(SQLException e){
    			logger.warn("Can't connect to the DB.", e);
    			e.printStackTrace();
    		}
    		
    	}
    }

    /**
     * Link to two tables
     * 
     * @param tableName name of the new table
     * @param firstCol the name of the first column
     * @param secondCol the name of the second column
     * @param firstKey the first key for the link
     * @param secondKey the second key for the link
     */
    public static void linkTwoTables(String tableName, String firstCol, String secondCol, String firstKey, String secondKey){
    	conn = getInstance();
    	// if link is not yet established
		if (!isLinkEstablished(tableName, firstCol, secondCol, firstKey, secondKey)){
			String query = "INSERT INTO "+tableName+" VALUES ('"+firstKey+"' , '"+secondKey+"');";
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(query);
			} catch (SQLException e) {
				logger.warn("Can't execute the DB command.", e);
				e.printStackTrace();
			}
		}
		
    }
	
    /**
     * Add a new Offering in the offering table if its not already in.
     * @param offering_id the offering_id as a String
     * @param offering_name the offering_name as a String
     */
    public static void addOffering(String offering_id, String offering_name){
    	conn = getInstance();
    	if (!DBConnection.isOfferingInDatabase(offering_id)) {
			String query = "INSERT INTO offering VALUES ('" + offering_id+"','"+offering_name+"');";
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(query);
			} catch (SQLException e) {
				logger.warn("Can't execute the DB command.", e);
				e.printStackTrace();
			}
		}
	}
	
    
    /**
     * Check if the offering_id is already in the DB
     * 
     * @param offering_id the offering_id to check
     * @return a boolean that shows if the offering is in the DB or not
     */
	public static boolean isOfferingInDatabase(String offering_id) {
		conn = getInstance();
		boolean offeringInDatabase = true;
		String query = "SELECT COUNT(offering_id) FROM offering WHERE offering_id='"+offering_id+"';";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt("count") != 0) {
				offeringInDatabase = true;
			} else {
				offeringInDatabase = false;
			}
		} catch (SQLException e) {
			logger.warn("Can't execute the DB command.", e);
			e.printStackTrace();
		}
		return offeringInDatabase;
	}
	

	/**
	 * Add a new procedure in the procedure table if its not already in.
	 * @param procedure_id the name of the procedure as a String
	 */
	public static void addProcedure(String procedure_id){
		conn = getInstance();
		// only add phenomenon to database if not yet added
		if (!DBConnection.isProcedureInDatabase(procedure_id)) {
			String query = "INSERT INTO procedure(procedure_id) VALUES ('" + procedure_id+"');";
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(query);
			} catch (SQLException e) {
				logger.warn("Can't execute the DB command.", e);
				e.printStackTrace();
			}
		}
	}
	
	/**
     * Check if the procedure_id is already in the DB
     * 
	 * @param procedure_id the procedure_id to check
	 * @return a boolean that shows if the offering is in the DB or not
	 */
	public static boolean isProcedureInDatabase(String procedure_id) {
		conn = getInstance();
		boolean procedureInDatabase = true;
		String query = "SELECT COUNT(procedure_id) FROM procedure WHERE procedure_id='"+procedure_id+"';";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt("count") != 0) {
				procedureInDatabase = true;
			} else {
				procedureInDatabase = false;
			}
		} catch (SQLException e) {
			logger.warn("Can't execute the DB command.", e);
			e.printStackTrace();
		}
		return procedureInDatabase;
	}
	

	/**
	 * Add a new phenomenon in the procedure table if its not already in.
	 * 
	 * @param phenomenon_id the name of the phenomenon as a String
	 * @param phenomenon_description phenomenon_description as a String
	 * @param unit the measured unit
	 * @param valuetype the measured valuetyp
	 */
	public static void addPhenomenon(String phenomenon_id, String phenomenon_description, String unit, String valuetype) {
		conn = getInstance();
		if (!DBConnection.isPhenomenonInDatabase(phenomenon_id)) {
			String query = "INSERT INTO phenomenon VALUES ('" + phenomenon_id
					+ "', '" + phenomenon_description + "', '" + unit + "','"
					+ valuetype + "');";
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(query);
			} catch (SQLException e) {
				logger.warn("Can't execute the DB command.", e);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Check if the phenomenon_id is already in the DB
	 * @param phenomenon_id the phenomenon_id to check
	 * @return a boolean that shows if the offering is in the DB or not
	 */
	public static boolean isPhenomenonInDatabase(String phenomenon_id) {
		conn = getInstance();
		boolean phenomenonInDatabase = true;
		String query = "SELECT COUNT(phenomenon_id) FROM phenomenon WHERE phenomenon_id='"+phenomenon_id+"';";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt("count") != 0) {
				phenomenonInDatabase = true;
			} else {
				phenomenonInDatabase = false;
			}
		} catch (SQLException e) {
			logger.warn("Can't execute the DB command.", e);
			e.printStackTrace();
		}
		return phenomenonInDatabase;
	}
	
	/**
	 * Method that checks the information about the establishment of the Link
	 * 
	 * @param tableName the table name 
	 * @param firstCol the first column
	 * @param secondCol the second column
	 * @param firstKey the first key
	 * @param secondKey the second key
	 * @return a boolean that shows if the link is established or not
	 */
	public static boolean isLinkEstablished(String tableName, String firstCol, String secondCol, String firstKey, String secondKey){
		conn = getInstance();
		boolean linkEstablished = true;
		String query = "SELECT COUNT("+firstCol+") FROM "+tableName+" WHERE "+firstCol+"= '"+firstKey+"' AND "+secondCol+"='"+secondKey+"';";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			if (rs.getInt("count") != 0) {
				linkEstablished = true;
			} else {
				linkEstablished = false;
			}
		} catch (SQLException e) {
			logger.warn("Can't execute the DB command.", e);
			e.printStackTrace();
		}
		return linkEstablished;
	}
	
	/**
	 * Method that initiate the Logger
	 */
	static private Logger iniLogger() {
		Logger log  = Logger.getLogger(DBConnection.class);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("log4j.properties");
		PropertyConfigurator.configure(url);
		return log;
	}

}
