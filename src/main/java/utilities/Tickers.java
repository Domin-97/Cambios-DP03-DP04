
package utilities;

import java.util.UUID;

import org.joda.time.LocalDateTime;

public class Tickers {

	public static String generateTicker() {
		String result = "";
		LocalDateTime now = LocalDateTime.now();
		String year = ""+now.getYear();
		year = year.substring(2);
		result = result + year + now.getMonthOfYear() + now.getDayOfMonth() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
		return result;
	}
}
