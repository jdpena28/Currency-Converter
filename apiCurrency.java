
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;



public class apiCurrency{
    // essential URL structure is built using constants 
	// this object is used for executing requests to the (REST) API
	static CloseableHttpClient httpClient = HttpClients.createDefault();
	static String time;
	static double getPHP,USD,AUD,EUR,AED,PHP;
	// John Henrich
	public static void sendConvertRequest(){
		HttpGet get = new HttpGet("http://api.currencylayer.com/live?access_key=f95ec8d207000aa7784e2b0da98e1abc&currencies=PHP,AUD,EUR,AED&format=1");
		try {	
			CloseableHttpResponse response =  httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity));
			///Australian Dollar AUD
			///EURO	EUR
			//Dirham AED
			long unixTime = jsonObject.getInt("timestamp");
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			time =  Instant.ofEpochSecond(unixTime).atZone(ZoneId.of("GMT+8"))
					.format(format);
			EUR = jsonObject.getJSONObject("quotes").getDouble("USDEUR");
			AED = jsonObject.getJSONObject("quotes").getDouble("USDAED");
			USD = jsonObject.getJSONObject("quotes").getDouble("USDPHP");
			response.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException{
		Scanner scanner = new Scanner(System.in);
		sendConvertRequest();
		httpClient.close();
		System.out.println("-------------------------------------------------");
		System.out.println("#\t     REALTIME CURRENCY CONVERTER\t#");
		System.out.println("-------------------------------------------------");
		System.out.println("Updated: " + time);
		System.out.println("-------------------------------------------------");
		System.out.print("PESO(PHP): ");
		getPHP = scanner.nextDouble();
		scanner.close();
		System.out.println("-------------------------------------------------");

		USD usd = new USD(getPHP,USD);
		Thread usdThread = new Thread(usd);
		usdThread.start();

		Currency eur = new Currency (getPHP,EUR,USD,"Euro (EUR): $");
		Thread eurThread = new Thread(eur);
		eurThread.start();

		Currency aed = new Currency (getPHP,AED,USD,"Dirham (AED): د.إ");
		Thread aedThread = new Thread(aed);
		aedThread.start();
		
	}
}

class Currency  implements Runnable{
	String message = "";
	double php = 0;
	public Currency(double php, double currency,double usd, String message){
		double conversion = (1/usd) * currency; 
		this.php = php * conversion;
		this.message = message;
	}
	public void run() {
		System.out.println(this.message+ Math.round(this.php*100.0)/100.0);
	}

}

class USD implements Runnable{
	double php = 0;
	public USD(double php,double usd) {
		double conversion = 1/usd;
		this.php = php * conversion;
	}
	public void run() {
		System.out.println("US Dollars (USD): " +"$"+ Math.round(this.php*100.0)/100.0);
	}
}


