package org.mamba.Utils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
public class RecordSerial {
    // Generates a 18-digit record serial and returns long type
    public static long generateOrderNumber() {
        // Obtain current date yyyyMMddHHmmss, 14-digit
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String datePart = sdf.format(new Date());

        // Generate random number 4-digit
        Random random = new Random();
        int randomPart = random.nextInt(10000); // Generate a random number between 0-9999

        // Combine into 18-digit serial number
        String orderNumberStr = datePart + String.format("%04d", randomPart);

        // Transform into long type
        return Long.parseLong(orderNumberStr);
    }
}
