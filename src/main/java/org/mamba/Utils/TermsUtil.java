package org.mamba.Utils;

import lombok.Getter;

/**
 * Stores the Terms of Service and Privacy Policy in HTML format.
 */
public class TermsUtil {
    @Getter
    private final static String terms = """
            <h1>Terms of Service and Privacy Policy</h1>
                        
            <p>Welcome to the DIICSU Room Booking System. We are committed to protecting your personal data and respecting your privacy. This document explains how we collect, process, store, and protect your personal data in compliance with <strong>General Data Protection Regulation (GDPR)</strong> and <strong>Personal Information Protection Law of the People's Republic of China (PIPL)</strong>. By using our services, you agree to the terms outlined in this policy. Please take the time to read it carefully.</p>
                        
            <h2>1. Data Collection and Purpose of Use</h2>
                        
            <p>We collect and process only the minimum amount of personal data necessary to provide and improve our services. The categories of data collected include:</p>
                        
            <ul>
              <li><strong>Identity Information:</strong> Email address, name, phone number.</li>
              <li><strong>Booking Information:</strong> Room information, booking data & time.</li>
            </ul>
                        
            <p>We collect and process your data for the following purposes:</p>
                        
            <ul>
              <li><strong>Booking Management:</strong> To create, modify, and cancel bookings.</li>
              <li><strong>Notification and Communication:</strong> To send messages to you via system notification or email, and other essential updates.</li>
              <li><strong>Service Improvement:</strong> To analyze room usage patterns and optimize resource allocation (anonymized data may be used for analysis).</li>
              <li><strong>Legal Compliance:</strong> To comply with applicable laws and safeguard the system from unauthorized use.</li>
            </ul>
                        
            <p>We do not collect sensitive data such as biometric data, location information, or financial information unless explicitly required and with your informed consent.</p>
                        
            <h2>2. Data Storage Period</h2>
                        
            <p>We store your personal data only for as long as necessary to fulfill the purposes outlined above:</p>
                        
            <ul>
              <li><strong>Booking Data:</strong> Retained for 3 months after the booking date (for backup or anonymized data analysis), after which the data is completely deleted.</li>
              <li><strong>Account Information:</strong> Retained until the account is deactivated or the user requests deletion.</li>
            </ul>
                        
            <p>In cases where data is used for statistical or analytical purposes, it will be anonymized to prevent identification.</p>
                        
            <h2>3. User Rights and Legal Basis</h2>
                        
            <p>You have the following rights under GDPR (Article 12-23) and PIPL (Article 44-50), which we fully respect:</p>
                        
            <ul>
              <li><strong>Right to Know and Decide:</strong> You have the right to know how your data is processed and make decisions regarding it.</li>
              <li><strong>Right to Access:</strong> You can request a copy of your personal data.</li>
              <li><strong>Right to Rectification:</strong> You can request corrections to any inaccurate or incomplete information.</li>
              <li><strong>Right to Erasure (“Right to be Forgotten”):</strong> You may request deletion of your personal data.</li>
              <li><strong>Right to Withdraw Consent:</strong> You may withdraw consent or object to data processing based on legitimate interests at any time, which will not affect the legality of prior processing.</li>
              <li><strong>Right to Restriction of Processing:</strong> You can request limited processing of your data under certain conditions.</li>
              <li><strong>Right to Data Portability:</strong> You may request to receive your personal data in a structured, commonly used, and machine-readable format.</li>
            </ul>
                        
            <h2>4. Data Security</h2>
                        
            <p>We implement strict security measures to protect your personal data against unauthorized access, alteration, disclosure, or destruction:</p>
                        
            <ul>
              <li><strong>Data Encryption:</strong> Data is encrypted using industry-standard protocols for storage and for transmission.</li>
              <li><strong>Access Control:</strong> We apply Role-Based Access Control (RBAC) to limit access to authorized personnel only.</li>
            </ul>
                        
            <h2>5. Cross-Border Data Transfer</h2>
                        
            <p>Under the PIPL regulation, if data is transferred from China mainland (excluding Hong Kong SAR, Macao SAR and Taiwan) to other regions, we comply with security assessments or obtain explicit user consent where required.</p>
                        
            <h2>6. Data Sharing and Third-Party Processing</h2>
                        
            <p>To provide essential services, we may share your personal data with trusted third parties under strict confidentiality and legal requirements. We may work with the following third parties:</p>
                        
            <ul>
              <li><strong>Cloud Service Providers:</strong> To securely store data and provide system infrastructure.</li>
              <li><strong>Email Service Providers:</strong> To send notifications and reservation-related emails.</li>
            </ul>
                        
            <p>We ensure that all third-party processors comply with GDPR and PIPL by signing a Data Processing Agreement (DPA). Third parties may only process data for specified purposes and under strict security guidelines.</p>
                        
            <p>We do not sell, trade, or share your personal data with third parties for marketing purposes.</p>
                        
            <h2>7. Data Breach Notification and Response</h2>
                        
            <p>We have established procedures to identify, assess, and respond to data breaches in compliance with regulatory requirements.</p>
                        
            <p>In the event of a data breach, we will notify the appropriate regulatory authorities within 72 hours and inform affected users through phone number/email address that users have set up in this system if the breach poses a high risk to their rights. We will also promptly report incidents to the Cyberspace Administration of China (CAC) where necessary.</p>
                        
            <h2>8. Contact Us</h2>
                        
            <p>If you have any questions, concerns, or requests related to your personal data or this policy, please do not hesitate to contact us:</p>
                        
            <p><strong>Email:</strong> 2542682@dundee.ac.uk</p>
                        
            <p><strong>Phone:</strong> +86 199 0170 3156</p>
                        
            <p><strong>Address:</strong></p>
                        
            <p>Dundee International Institute</p>
                        
            <p>Central South University Xiaoxiang Campus</p>
                        
            <p>No. 405 Middle Xiaoxiang Road, Yuelu District, Changsha, Hunan Province, China</p>
            """;
}
