package org.mamba.Utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CalendarManager {
    /**
     * Generates a URL link that can be pushed to Outlook calendar.
     *
     * @param start    start time of the booking
     * @param end      end time of the booking
     * @param roomName the room that the user booked
     * @return the URL
     */
    public static String generateEventLink(LocalDateTime start, LocalDateTime end, String roomName) {
        // Time zone setting
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        // Transform from LDT to ZDT
        ZonedDateTime startZDT = start.atZone(zoneId);
        ZonedDateTime endZDT = end.atZone(zoneId);

        // Set the datetime format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter notificationFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Set calendar event's subject/body/location
        String subject = "Room booking";
        String body = "Use the room you reserved, from " + startZDT.format(notificationFormatter) +
                " to " + endZDT.format(notificationFormatter) + ".";

        // Generate URL and return it
        return String.format(
                "https://outlook.office.com/calendar/0/deeplink/compose?path=/calendar/action/compose" +
                        "&startdt=%s&enddt=%s&subject=%s&body=%s&location=%s",
                startZDT.format(formatter),
                endZDT.format(formatter),
                URLEncoder.encode(subject, StandardCharsets.UTF_8),
                URLEncoder.encode(body, StandardCharsets.UTF_8),
                URLEncoder.encode(roomName, StandardCharsets.UTF_8)
        );
    }

    /**
     * MAIN FUNCTION FOR TESTING ONLY - NEVER CALL FROM OUTSIDE
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        /* TEST DATA DO NOT MODIFY */
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(2);

        LocalDateTime testStart = startTime;
        LocalDateTime testEnd = endTime;
        String testRoomName = "Mamba Out Room";

        // Test
        System.out.println(generateEventLink(testStart, testEnd, testRoomName));
    }
}
