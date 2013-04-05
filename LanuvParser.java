/*
 * The class is needed to parse the measured values from the Lanuv-Page.
 * The parsed values are saved by the "Messwerte"-class.
 * 
 * 
 * TODO: Switch-case, Exception-handling
 * 
 * Überlegung weiter vorgehen:  - Datenbankhandler-Klasse schreiben. Methode nutzen um die Werte aus mMesswerte in die Datenbank zu speichern.
 * 								
 * 								- Dankbankhandler-Klasse nutzen um letzten Zeitstempel zu erhalten -> wird für Controll-Klasse benötigt um
 * 								  die URL zu bestimmen.
 * 
 * created on 11.12.2012 by Sven Mattauch
 * edited on 02.01.2013 by Sven Mattauch
 * edited on 17.01.2013 by Sven Mattauch
 */
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanuvParser {
	public static void main (String[] args) throws IOException{
		
		//Strings zum Auslesen und weiter verarbeiten der Webseite
		String sourceLine;
		String tabelle = "";
				
		//Auszulesende Webseite. Später per Main-Methoden aufruf veränderbar.
		URL adress = new URL(args[0]);
		
		//Buffer werden gesetzt
		InputStreamReader pageInput = new InputStreamReader(adress.openStream());
		BufferedReader source = new BufferedReader(pageInput);

		
		//Bezeicher für die Auszulesenden Werte. Werden in der while schleife verwendet
		String[] Bezeichner = {"Zeit:","\n",":Ozon:",":NO:",":NO2:",":LTEM:",":WRI:",":WGES:",
								":RFEU:",":SO2:",":Staub/PM10:", "\n"};


		// while schleife liest die benötigten Teile der Webseite aus und
		// und gibt den werten den entsprechenden Bezeichner
		int i = 0;
		while ((sourceLine = source.readLine()) != null){
			Pattern tag = Pattern.compile("<td.*?class.*?>");
			Matcher mtag = tag.matcher(sourceLine);
			if (mtag.matches()){
				tabelle += Bezeichner[i] + sourceLine + "\n";
				i++;
				i = i%11;
			}
		}
		
		
		// Verschiedene Reguläre Ausdrücke um die lesbarkeit zu erhöhen
		Pattern tag = Pattern.compile("<.*?>");
        Matcher mtag = tag.matcher(tabelle);
        while (mtag.find()) tabelle = mtag.replaceAll("");
        
        Pattern sChar = Pattern.compile("&.*?;");
        Matcher msChar = sChar.matcher(tabelle);
        while (msChar.find()) tabelle = msChar.replaceAll("");

        Pattern trenn = Pattern.compile("Zeit:");
        Matcher mtrenn = trenn.matcher(tabelle);
        while (mtrenn.find()) tabelle = mtrenn.replaceAll("TRENNZeit:");
        
        Pattern nLine = Pattern.compile("\\s");
        Matcher mnLine = nLine.matcher(tabelle);
        while (mnLine.find()) tabelle = mnLine.replaceAll("");
        
        // Print the clean content & close the Readers
   		//System.out.println(tabelle);
        ArrayList<Measurement> mMesswerte = lanuvMesswerte(tabelle);
        for (Measurement m : mMesswerte) {
        	System.out.println(
        			"Zeit: " + m.getTime() + 
        			" Ozon: " + m.getOzon() +
        			" NO: " + m.getNo() + 
        			" NO2: " + m.getNo2() + 
        			" LTEM: " + m.getLtem() + 
        			" WRI: " + m.getWri() +
        			" WGES: " + m.getWges() +
        			" RFEU: " + m.getRfeu() + 
        			" SO2: " + m.getSo2() +
        			" Staub/PM10: " + m.getpm10() );
        }
        pageInput.close();
        source.close();
		
	}
	
	
	/*
	 * Function to handle the values of the list and to save them into an arrayList.
	 * This list can be used to save the values in a database
	 */
	public static ArrayList<Measurement> lanuvMesswerte(String s) {
        String[] loadTab = s.split("TRENN");
        ArrayList<Measurement> nMesswerte = new ArrayList<Measurement>();
        
        for (String token:loadTab) {
        	//System.out.println(token);
        		if(Pattern.matches("Zeit:([0-9])([0-9]):([0-9])([0-9]).*?", token)){
        			Measurement nMWert = new Measurement();
        			nMesswerte.add(nMWert);
        			String[] time = token.split(":");
        			nMesswerte.get(nMesswerte.indexOf(nMWert)).setTime(time[1] + ":" + time[2]);      			
        			//System.out.println(nMesswerte.get(nMesswerte.indexOf(nMWert)).getTime());
        			
        			
        			//Switchcase umbasteln???.... Werte überprüfen
        			//Werte abfrage + speicherung
        			/*
        			 * Zeit:00:00: Ozon:0: NO: 0: NO2: 0: LTEM: 0: WRI:  0:WGES:  0: RFEU:0: SO2: 0:  Staub/PM10: 0
        			 * [0] [1] [2] [3] [4] [5][6] [7] [8] [9]  [10][11] [12][13] [14][15][16][17] [18] [19]      [20]
        			 */
        			if(Pattern.matches(".*?Ozon:[0-9]+.*?", token)){
        				//System.out.println(time[4]);
        				nMesswerte.get(nMesswerte.indexOf(nMWert)).setOzon(Integer.parseInt(time[4]));
        			}if(Pattern.matches(".*?NO:[0-9]+.*?", token)){
        				//System.out.println(time[6]);
        				nMesswerte.get(nMesswerte.indexOf(nMWert)).setNo(Integer.parseInt(time[6]));
        			}if(Pattern.matches(".*?NO2:[0-9]+.*?", token)){
        				//System.out.println(time[8]);
        				nMesswerte.get(nMesswerte.indexOf(nMWert)).setNo2(Integer.parseInt(time[8]));
        			}if(Pattern.matches(".*?LTEM:[0-9]+.*?", token)){
        				//System.out.println(time[10]);
        				nMesswerte.get(nMesswerte.indexOf(nMWert)).setLtem(Integer.parseInt(time[10]));
        			}if(Pattern.matches(".*?WRI:[0-9]+.*?", token)){
        				//System.out.println(time[12]);
        				nMesswerte.get(nMesswerte.indexOf(nMWert)).setWri(Integer.parseInt(time[12]));
        			}if(Pattern.matches(".*?WGES:[0-9]+.*?", token)){
        				//System.out.println(time[14]);
        				nMesswerte.get(nMesswerte.indexOf(nMWert)).setWges(Integer.parseInt(time[14]));
        			}if(Pattern.matches(".*?RFEU:[0-9]+.*?", token)){
        				//System.out.println(time[16]);
        				nMesswerte.get(nMesswerte.indexOf(nMWert)).setRfeu(Integer.parseInt(time[16]));
        			}if(Pattern.matches(".*?SO2:[0-9]+.*?", token)){
        				//System.out.println(time[18]);
        				nMesswerte.get(nMesswerte.indexOf(nMWert)).setSo2(Integer.parseInt(time[18]));
        			}if(Pattern.matches(".*?Staub/PM10:[0-9]+.*?", token)){
        				//System.out.println(time[20]);
        				nMesswerte.get(nMesswerte.indexOf(nMWert)).setPm10(Integer.parseInt(time[20]));
        			}        			
        		}else
        			System.out.println("Keine Messwerte vorhanden.");
        }
        return nMesswerte;
	}
		
}