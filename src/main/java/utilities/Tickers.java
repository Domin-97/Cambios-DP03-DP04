
package utilities;

import org.joda.time.LocalDateTime;

public class Tickers {

	public static String getTickerFixUpTicker(final int i) {
		String result = "";
		final int a = (LocalDateTime.now().hashCode() % 1000 + i % 1000);
		result = result + "futt" + LocalDateTime.now() + a + Math.random() * 1000;
		return result;
	}
}
