/*
 * The class for saving and controlling of the parsed measured value by the Lanuv-Page.
 * The class shows a row of measurings at a certain time.
 * 
 * 
 * created on 11.12.2012 by Sven Mattauch
 * edited on 02.01.2013 by Sven Mattauch 
 * edited on 17.01.2013 by Sven Mattauch
 */
public class Measurement {
	
	/*
	 * Double data types for saving of the measured values.
	 */
	private double ltem = 0;
	private double wri = 0;
	private double wges = 0;
	private double rfeu = 0;
	private double ozon = 0;
	private double no2 = 0;
	private double no = 0;
	private double so2 = 0;
	private double pm10 = 0;
	private String time = "00:00";
	
	
	/*
	 * All needed set- and get-methods for the data types.
	 */
	
	double getLtem (){
		return ltem;
	}
	double getWri(){
		return wri;
	}
	double getWges(){
		return wges;
	}
	double getRfeu(){
		return rfeu;
	}
	double getOzon(){
		return ozon;
	}
	double getSo2(){
		return so2;
	}
	double getNo2(){
		return no2;
	}
	double getNo(){
		return no;
	}
	double getpm10(){
		return pm10;
	}
	String getTime(){
		return time;
	}
	
	void setTime(String x){
		time = x;
	}
	void setOzon(double x){
		ozon = x;
	}
	void setSo2(double x){
		so2 = x;
	}
	void setNo2(double x){
		no2 = x;
	}
	void setNo(double x){
		no = x;
	}
	void setPm10(double x){
		pm10 = x;
	}
	void setLtem (double x){
		ltem = x;
	}
	void setWri(double x){
		wri = x;
	}
	void setRfeu(double x){
		rfeu = x;
	}
	void setWges (double x) {
		wges = x;
	}

}
