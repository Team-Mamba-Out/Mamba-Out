package org.mamba.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.mamba.Utils.JwtUtil;
import org.mamba.entity.Result;
import org.mamba.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/verify")
public class VerificationController {
    @Autowired
    private UserService userService;
    private static final Map<String, LocalDateTime> timeLimitInfoMap = new HashMap<>();
    private static final Map<String, String> codeInfoMap = new HashMap<>();
    private static final Map<String, Integer> failedAttemptsMap = new HashMap<>();
    private static final Map<String, LocalDateTime> lastRequestTimeMap = new HashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final int REQUEST_INTERVAL_SECONDS = 60;
    private static final String SMTP_SENDER_USERNAME = "1607329575@qq.com"; // NEVER MODIFY!!!
    private static final String SMTP_SENDER_PASSWORD = "vkykpbjednzsfibc";  // NEVER MODIFY!!!

    /**
     * Starts a verification process by generating verification code.
     *
     * @param email the user's email
     */
    @RequestMapping("/send")
    public static Result startVerify(@RequestParam() String email) {
        if (!email.endsWith("@dundee.ac.uk")) {
            return Result.error("Please enter a valid UoD email!");
        }

        LocalDateTime now = LocalDateTime.now();
        if (lastRequestTimeMap.containsKey(email) && now.isBefore(lastRequestTimeMap.get(email).plusSeconds(REQUEST_INTERVAL_SECONDS))) {
            throw new RuntimeException("Error: You can request at most one code each minute. Please wait if you want a new code.");
        }

        SecureRandom random = new SecureRandom();
        String codeString = String.format("%06d", random.nextInt(1000000));

        timeLimitInfoMap.put(email, now);
        codeInfoMap.put(email, codeString);
        lastRequestTimeMap.put(email, now);
        failedAttemptsMap.put(email, 0);

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
            message.setFrom(new InternetAddress(SMTP_SENDER_USERNAME)); // Set sender
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email)); // Set receiver

            message.setSubject("Your verification code"); // Set subject

            String requireApprovalText = "Hi,\n\n"
                    + "You are using this email address for DIICSU room booking system user authentication.\n\n"
                    + "Your verification code is: "
                    + codeString + "\n\n"
                    + "This code will expire in 5 minutes. \n\n"
                    + "If you believe this email is not relevant to you, please ignore. "
                    + "As long as this code is not compromised, your account remains secure.";

            message.setText(requireApprovalText);  // Set text

            // Send email
            Transport.send(message);

            System.out.println("EMAIL SENT SUCCESSFULLY");

        } catch (MessagingException e) {
            throw new RuntimeException("EMAIL SENDING FAILURE: " + e.getMessage());
        }

        return Result.success("Verification code has been successfully sent. Check spam folder if not found.");
    }

    /**
     * Verifies the code the user inputs.
     *
     * @param email     the user's email
     * @param codeInput the code string that the user inputs
     * @return if authentication is successful or not
     */
    @RequestMapping("/validate")
    public Result verifyCode(@RequestParam() String email, @RequestParam() String codeInput) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime generateTimeStored = timeLimitInfoMap.get(email);

        if (generateTimeStored == null || now.isAfter(generateTimeStored.plusMinutes(5))) {
            return Result.error("Verification expired.");
        }

        int attempts = failedAttemptsMap.getOrDefault(email, 0);
        if (attempts >= MAX_ATTEMPTS) {
            return Result.error("Ran out of attempt times.");
        }

        if (constantTimeCompare(codeInput, codeInfoMap.get(email))) {
            failedAttemptsMap.remove(email);
            Integer uid = userService.getUserId(email);
            String name = userService.getUserName(uid);
            Map<String,Object> claims = new HashMap<>();
            claims.put("uid",uid);
            claims.put("email",email);
            claims.put("name",name);
            String token = JwtUtil.getToken(claims);
            return Result.success(token);
        }
        failedAttemptsMap.put(email, attempts + 1);
        return Result.error("Incorrect code. " + (5 - failedAttemptsMap.get(email)) + " attempts left.");
    }
    @RequestMapping("/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        System.out.println(token);
        Map<String,Object> claims = JwtUtil.parseToken(token);
        Integer uid = (Integer) claims.get("uid");
        Map<String, Object> result = userService.getUserInfo(uid);
        return Result.success(result);
    }
    /**
     * Compares two string in constant time. This is to prevent timing attack.
     *
     * @param a a string
     * @param b another string
     * @return compare result
     */
    private static boolean constantTimeCompare(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(), b.getBytes());
    }
}
