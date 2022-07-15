package com.pratikbutani.pdf.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pratikbutani.pdf.R;

import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailActivity extends AppCompatActivity {

    EditText userMail;
    Button sendBtn;

    String email = "abc@gmail.com";
    String password = "fajkdnfakjd";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        userMail = findViewById(R.id.username);
        sendBtn = findViewById(R.id.sendEmail);

        String filePath = getIntent().getStringExtra("filePath");
        sendBtn.setOnClickListener(v -> {
            String userEmail = userMail.getText().toString();
            if (userEmail.isEmpty()) {
                Toast.makeText(EmailActivity.this, "please enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            sendMail(userEmail, filePath);
        });

    }


    private void sendMail(String userEmail, String filePath) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
        // initialize email content
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));

            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(userEmail));
            message.setSubject("Order Details");

            String emailMessage = "Your Order Details.";
            message.setText(emailMessage);


            //3) create MimeBodyPart object and set your message text
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText("Your Order Details");

            //4) create new MimeBodyPart object and set DataHandler object to this object
            MimeBodyPart mimeBodyPart = new MimeBodyPart();

            String filename = "SendAttachment.pdf";//change accordingly
            DataSource source = new FileDataSource(filePath);
            mimeBodyPart.setDataHandler(new DataHandler(source));
            mimeBodyPart.setFileName(filename);


            //5) create Multipart object and add MimeBodyPart objects to this object
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            multipart.addBodyPart(mimeBodyPart);

            //6) set the multiplart object to the message object
            message.setContent(multipart);

            new SendMail().execute(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private class SendMail extends AsyncTask<Message, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EmailActivity.this, "Sending Email",
                    "Retrieve...", true, false);
        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                // when success
                Transport.send(messages[0]);
                return "Success";
            } catch (Exception e) {
                // when error
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // dismiss progressDialog
            progressDialog.dismiss();
            if (s.equals("Success")) {
                //when success
                String title = "email sent  ";


                Toast.makeText(EmailActivity.this, title, Toast.LENGTH_SHORT).show();

            } else {
                String mailNotSend = "email sending failed";
                Toast.makeText(EmailActivity.this, mailNotSend, Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void send(String filepath) {

        // Recipient's email ID needs to be mentioned.
        String to = "abc@gmail.com";

        // Sender's email ID needs to be mentioned
        final String from = "xyz@gmail.com";
        // final String username = "xyz";
        final String pass = "xyz123";
        // Assuming you are sending email from localhost
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.user", email);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        //Read more: http://mrbool.com/how-to-work-with-java-mail-api-in-android/27800#ixzz3E2T8ZbpJ


        // Get the default Session object.
        //Session session = Session.getDefaultInstance(properties);


        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });


        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText("This is message body");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filepath);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filepath);
            multipart.addBodyPart(messageBodyPart);


            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");

            // Send the complete message parts
            message.setContent(multipart);
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
