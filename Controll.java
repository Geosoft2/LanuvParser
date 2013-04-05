import java.util.Calendar;

/*
 * Class for creating the URLs and calling up the LanuvParser. 
 *
 * 
 * created on 03.02.2013 by Sven Mattauch
 * edited on 17.01.2013 by Sven Mattauch
 */

public class Controll {
	
	public static void main (String[] args){

		String mt = calUrl();
		String[] urlw =  new String[] {weslerUrl(mt)};
		String[] urlg =  new String[] {geistUrl(mt)};
		try {
			System.out.println("Wesler:");
			LanuvParser.main(urlw);
			System.out.println("Geist:");
			LanuvParser.main(urlg);
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
	
	
	static public String calUrl () {
		Calendar aCal = Calendar.getInstance();
		int tag = aCal.get(Calendar.DATE);
		int monat = (aCal.get(Calendar.MONTH) + 1);
		String sTag;
		String sMonat;
		
		switch (tag){
		case 1: case 2: case 3: case 4: case 5: case 6: case 7:
		case 8: case 9:
			sTag = "0" + Integer.toString(tag);
			break;
		default:
			sTag = Integer.toString(tag);
			break;
		}
		
		switch (monat){
		case 1: case 2: case 3: case 4: case 5: case 6: case 7:
		case 8: case 9:
			sMonat = "0" + Integer.toString(monat);
			break;
		default:
			sMonat = Integer.toString(monat);
			break;
		}
					
		return sMonat + sTag;
	}
	
	
	static public String weslerUrl (String s ){
		//WeselerStr Lanuv-Station
		String wUrl = "http://www.lanuv.nrw.de/luft/temes/" + s + "/VMS2.htm";
		return wUrl;
	}
	
	static public String geistUrl (String s ){
		//Geiststr Lanuv-Station
		String gUrl = "http://www.lanuv.nrw.de/luft/temes/" + s + "/MSGE.htm";
		return gUrl;
	}

}
