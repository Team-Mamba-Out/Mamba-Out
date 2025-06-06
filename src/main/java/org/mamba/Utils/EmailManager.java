package org.mamba.Utils;

import org.mamba.entity.Record;
import org.mamba.entity.Room;
import org.mamba.service.impl.RoomServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Component
public class EmailManager {

    @Autowired
    private static RoomServiceImpl roomService;
    // Sender email address
    private static final String SMTP_SENDER_USERNAME = "1607329575@qq.com"; // NEVER MODIFY!!!
    private static final String SMTP_SENDER_PASSWORD = "vkykpbjednzsfibc";  // NEVER MODIFY!!!

    private static final DateTimeFormatter notificationFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Sends an email that notifies the user that the booking is successful.
     * NOTICE: Whether the room requires approval or not will result in different replies.
     *
     * @param userEmail           the user's email
     * @param record              the corresponding record
     * @param roomRequireApproval if the corresponding room requires approval or not
     */
    public static void sendBookSuccessfulEmail(String userEmail, Record record, boolean roomRequireApproval) {
        // Error detection only
        if (!roomRequireApproval && record.getCorrespondingRoom().isRequireApproval()) {
            throw new RuntimeException("ERROR: This room requires approval but roomRequireApproval is wrongfully set to false.");
        } else if (roomRequireApproval && !record.getCorrespondingRoom().isRequireApproval()) {
            throw new RuntimeException("ERROR: This room does not require approval but roomRequireApproval is wrongfully set to true.");
        }

        // Set email server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");            // enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // enable TLS
        props.put("mail.smtp.host", "smtp.qq.com");     // email server address
        props.put("mail.smtp.port", "587");             // email server port

        // Create new session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_SENDER_USERNAME, SMTP_SENDER_PASSWORD);
            }
        });

        try {
            // Create a new email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_SENDER_USERNAME, "DIICSU Room Booking System")); // Set sender // Set sender
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail)); // Set receiver

            if (roomRequireApproval) {
                // This room requires approval
                message.setSubject("Booking request sent"); // Set subject

                String requireApprovalText = "Hi,\n\n"
                        + "We have received your request for reservation for "
                        + record.getCorrespondingRoom().getRoomName() + ".\n\n"
                        + "Your requested booking time: From "
                        + record.getStartTime().format(notificationFormatter) + " to "
                        + record.getEndTime().format(notificationFormatter) + ".\n\n"
                        + "Administrators will review your request as soon as possible. "
                        + "Thanks for your understanding.";

                message.setText(requireApprovalText);  // Set text
            } else {
                // Generate Outlook calendar link
                String link = CalendarManager.generateEventLink(record.getStartTime(), record.getEndTime(), record.getCorrespondingRoom().getRoomName());

                // This room does not require approval
                message.setSubject("Your booking is successful"); // Set subject

                String noApprovalText = "Hi,\n\n"
                        + "We have received your reservation for "
                        + record.getCorrespondingRoom().getRoomName() + ".\n\n"
                        + "Your booking time: From "
                        + record.getStartTime().format(notificationFormatter) + " to "
                        + record.getEndTime().format(notificationFormatter) + ". "
                        + "You can add it to your Outlook calendar via this link: " + link + "\n\n"
                        + "As a special reminder, you must check in promptly 10 minutes before the start of your booking "
                        + "or it will be recorded as a breach of contract, "
                        + "which may affect your subsequent bookings.";

                message.setText(noApprovalText);  // Set text
            }

            // Send email
            Transport.send(message);

            System.out.println("EMAIL SENT SUCCESSFULLY");

        } catch (MessagingException e) {
            throw new RuntimeException("EMAIL SENDING FAILURE: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an email that notifies the user that the booking approval is APPROVED.
     * NOTICE: This function should only be called for those rooms that require approval.
     *
     * @param userEmail the user's email
     * @param record    the corresponding record
     */
    public static void sendRequestApprovedEmail(String userEmail, Record record) {
        // Error check only
        if (!record.getCorrespondingRoom().isRequireApproval()) {
            throw new RuntimeException("ERROR: This room does not require approval!");
        }

        // Set email server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");            // enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // enable TLS
        props.put("mail.smtp.host", "smtp.qq.com");     // email server address
        props.put("mail.smtp.port", "587");             // email server port

        // Create new session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_SENDER_USERNAME, SMTP_SENDER_PASSWORD);
            }
        });

        try {
            // Create a new email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_SENDER_USERNAME, "DIICSU Room Booking System")); // Set sender // Set sender
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail)); // Set receiver
            message.setSubject("Your booking request is approved"); // Set subject

            // Generate Outlook calendar link
            String link = CalendarManager.generateEventLink(record.getStartTime(), record.getEndTime(), record.getCorrespondingRoom().getRoomName());

            String requestApprovedText = "Hi,\n\n"
                    + "This is a reminder that your booking request for "
                    + record.getCorrespondingRoom().getRoomName() + " has been approved.\n\n"
                    + "Your booking time: From "
                    + record.getStartTime().format(notificationFormatter) + " to "
                    + record.getEndTime().format(notificationFormatter) + ". "
                    + "You can add it to your Outlook calendar via this link: " + link + "\n\n"
                    + "This means your booking is now in effect."
                    + "As a special reminder, you must check in promptly 10 minutes before the start of your booking "
                    + "or it will be recorded as a breach of contract, "
                    + "which may affect your subsequent bookings.";

            message.setText(requestApprovedText);  // Set text

            // Send email
            Transport.send(message);

            System.out.println("EMAIL SENT SUCCESSFULLY");

        } catch (MessagingException e) {
            throw new RuntimeException("EMAIL SENDING FAILURE: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an email that notifies the user that the booking approval is REJECTED.
     * NOTICE: This function should only be called for those rooms that require approval.
     *
     * @param userEmail the user's email
     * @param record    the corresponding record
     */
    public static void sendRequestRejectedEmail(String userEmail, Record record) {
        Room room = roomService.getRoomById(record.getRoomId());
        // Error check only
        if (room.isRequireApproval()) {
            throw new RuntimeException("ERROR: This room does not require approval!");
        }

        // Set email server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");            // enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // enable TLS
        props.put("mail.smtp.host", "smtp.qq.com");     // email server address
        props.put("mail.smtp.port", "587");             // email server port

        // Create new session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_SENDER_USERNAME, SMTP_SENDER_PASSWORD);
            }
        });

        try {
            // Create a new email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_SENDER_USERNAME, "DIICSU Room Booking System")); // Set sender // Set sender
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail)); // Set receiver
            message.setSubject("Your booking request is rejected"); // Set subject

            String requestRejectedText = "Hi,\n\n"
                    + "This is a reminder that your booking request for "
                    + room.getRoomName() + " has been rejected.\n\n"
                    + "This means that your booking record will be removed. "
                    + "We apologize for any inconvenience this may cause you.";

            message.setText(requestRejectedText);  // Set text

            // Send email
            Transport.send(message);

            System.out.println("EMAIL SENT SUCCESSFULLY");

        } catch (MessagingException e) {
            throw new RuntimeException("EMAIL SENDING FAILURE: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an email that notifies the user to check in.
     *
     * @param userEmail the user's email
     * @param record    the corresponding record
     */
    public static void sendCheckInEmail(String userEmail, Record record) {
        // Set email server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");            // enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // enable TLS
        props.put("mail.smtp.host", "smtp.qq.com");     // email server address
        props.put("mail.smtp.port", "587");             // email server port

        // Create new session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_SENDER_USERNAME, SMTP_SENDER_PASSWORD);
            }
        });

        try {
            // Create a new email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_SENDER_USERNAME, "DIICSU Room Booking System")); // Set sender // Set sender
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail)); // Set receiver
            message.setSubject("Please check in for your booking"); // Set subject

            String almostTimeText = "Hi,\n\n"
                    + "This is a kindly reminder that you may now check in on our mini program for your booking at "
                    + record.getCorrespondingRoom().getRoomName() + ".\n\n"
                    + "Check in will close at the start of your booking time. "
                    + "Failure to check in could affect your subsequent bookings.\n\n"
                    + "Your booking time: From "
                    + record.getStartTime().format(notificationFormatter) + " to "
                    + record.getEndTime().format(notificationFormatter) + ".\n\n"
                    + "Thanks for your understanding.";

            message.setText(almostTimeText);  // Set text

            // Send email
            Transport.send(message);

            System.out.println("EMAIL SENT SUCCESSFULLY");

        } catch (MessagingException e) {
            throw new RuntimeException("EMAIL SENDING FAILURE: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an email that notifies the user that the booking has been cancelled/reassigned.
     *
     * @param userEmail          the user's email
     * @param reassignSuccessful if the reassignment is successful or not
     */

    public static void sendRecordCancelledEmail(String userEmail, boolean reassignSuccessful) {
        // Set email server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");            // enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // enable TLS
        props.put("mail.smtp.host", "smtp.qq.com");     // email server address
        props.put("mail.smtp.port", "587");             // email server port

        // Create new session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_SENDER_USERNAME, SMTP_SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_SENDER_USERNAME, "DIICSU Room Booking System")); // Set sender // Set sender
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail)); // Set receiver

            if (reassignSuccessful) {
                // Create a new email
                message.setSubject("Your booking has been modified"); // Set subject

                String modifiedText = "Hi,\n\n"
                        + "We are sorry to inform you that you have a booking record that has been modified.\n\n"
                        + "Please check the updated details on the website or mini program. "
                        + "We apologize for any inconvenience this may cause you.";

                message.setText(modifiedText);  // Set text
            } else {
                // Create a new email
                message.setSubject("Your booking has been cancelled"); // Set subject

                String cancelledText = "Hi,\n\n"
                        + "We are sorry to inform you that you have a booking record that has been cancelled. "
                        + "Details can be checked on the website or mini program.\n\n"
                        + "This means that your corresponding booking record will be removed. "
                        + "We apologize for any inconvenience this may cause you.";

                message.setText(cancelledText);  // Set text
            }
            // Send email
            Transport.send(message);

            System.out.println("EMAIL SENT SUCCESSFULLY");

        } catch (MessagingException e) {
            throw new RuntimeException("EMAIL SENDING FAILURE: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an email that notifies the user that the booking is almost over.
     *
     * @param userEmail the user's email
     * @param record    the corresponding record
     */
    public static void sendBookingAlmostOverEmail(String userEmail, Record record) {
        // Set email server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");            // enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // enable TLS
        props.put("mail.smtp.host", "smtp.qq.com");     // email server address
        props.put("mail.smtp.port", "587");             // email server port

        // Create new session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_SENDER_USERNAME, SMTP_SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_SENDER_USERNAME, "DIICSU Room Booking System")); // Set sender // Set sender
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail)); // Set receiver

            // Create a new email
            message.setSubject("Your booking will end soon"); // Set subject

            String bookingAlmostOverText = "Hi,\n\n"
                    + "This is a kind reminder that your booking for " + record.getCorrespondingRoom().getRoomName()
                    + " has only ten minutes left.\n\n"
                    + "If you need to, you can extend your booking on the webpage or mini program. "
                    + "If you are not seeking for an extension, "
                    + "please take care to leave in a timely manner so as not to disrupt others' use.";

            message.setText(bookingAlmostOverText);  // Set text
            // Send email
            Transport.send(message);

            System.out.println("EMAIL SENT SUCCESSFULLY");

        } catch (MessagingException e) {
            throw new RuntimeException("EMAIL SENDING FAILURE: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * test only, do not call from outside
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        Record testRecord = new Record();
        Room testRoom = new Room();

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(2);

        testRecord.setStartTime(startTime);
        testRecord.setEndTime(endTime);

        testRoom.setRoomName("Laoda's 208");
        testRecord.setCorrespondingRoom(testRoom);

        sendBookingAlmostOverEmail("2542682@dundee.ac.uk", testRecord);
    }
}