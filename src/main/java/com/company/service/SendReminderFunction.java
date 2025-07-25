package com.company.service;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SendReminderFunction implements RequestHandler<ScheduledEvent, String> {

    private static final String QUOTE_API_URL = "https://zenquotes.io/api/random";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final DynamoDbClient dynamoDb = DynamoDbClient.create();
    private final SesClient sesClient = SesClient.create();
    private final String tableName = System.getenv("TABLE_NAME");
    private final String senderEmail = System.getenv("SENDER_EMAIL");


    @Override
    public String handleRequest(ScheduledEvent scheduledEvent, Context context) {
        context.getLogger().log("Running SendReminderFunction");

        try {
            List<Map<String, AttributeValue>> reminders = getAllReminders();

            ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
            String currentTime = nowUtc.format(TIME_FORMATTER);
            DayOfWeek currentDay = nowUtc.getDayOfWeek();

            for (Map<String, AttributeValue> item : reminders) {
                String sendTime = item.get("send_time").s();
                String days = item.get("days").s();
                String note = item.get("note").s();
                String email = item.get("email").s();

                // For production, use this condition:
                // if (!shouldSendReminder(sendTime, days, currentTime, currentDay)) continue;

                String quote = fetchQuote();

                sendEmail(email, note, quote);
                context.getLogger().log("Email sent to " + email);
            }
            return "All matching reminders processed successfully";

        } catch (Exception ex) {
            context.getLogger().log("Error:" + ex.getMessage() + "\\n");
            return "Failed to send reminders.";

        }
    }

    private List<Map<String, AttributeValue>> getAllReminders() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

        ScanResponse scanResponse = dynamoDb.scan(scanRequest);
        return scanResponse.items();
    }


    private boolean shouldSendReminder(String sendTime, String days, String currentTime, DayOfWeek currentDay) {
        return sendTime.equals(currentTime) && days.toUpperCase().contains(currentDay.name());
    }


    private String fetchQuote() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(QUOTE_API_URL).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String json = reader.lines().collect(Collectors.joining());

            int quoteStart = json.indexOf("\"q\":\"") + 5;
            int quoteEnd = json.indexOf("\"", quoteStart);
            return json.substring(quoteStart, quoteEnd); // TODO replace with a real JSON parser
        }
    }

    private void sendEmail(String toAddress, String reminder, String quote) {
        Destination destination = Destination.builder()
                                             .toAddresses(toAddress)
                                             .build();

        Message message = Message.builder()
                .subject(Content.builder().data("Your Smart Reminder").charset("UTF-8").build())
                .body(Body.builder()
                        .text(Content.builder().data(buildTextBody(reminder, quote)).charset("UTF-8").build())
                        .html(Content.builder().data(buildHtmlBody(reminder, quote)).charset("UTF-8").build())
                        .build())
                .build();

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                                                        .destination(destination)
                                                        .message(message)
                                                        .source(senderEmail)
                                                        .build();

        sesClient.sendEmail(emailRequest);
    }

    private String buildTextBody(String reminder, String quote) {
        return String.format("""
                Hi there,

                Reminder: %s
                Quote of the day: %s

                - Smart Reminder App
                """, reminder, quote);
    }

    private String buildHtmlBody(String reminder, String quote) {
        return String.format("""
                <html>
                  <body style="font-family: Arial, sans-serif; background-color: #f9fafb; padding: 30px;">
                    <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 25px 30px; border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.05);">
                      <h2 style="color: #4f46e5; font-family: Arial, sans-serif; margin-top: 0;">Smart Reminder</h2>

                      <p style="font-size: 16px; color: #111827;"><strong>Note:</strong> %s</p>

                      <div style="margin-top: 20px; padding: 15px; background-color: #eef6ff; border-left: 4px solid #3b82f6; border-radius: 6px;">
                        <p style="margin: 0; font-size: 14px; color: #0c4a6e;"><strong>Quote of the day:</strong></p>
                        <p style="font-style: italic; color: #1e3a8a; font-size: 14px; margin: 5px 0 0;">%s</p>
                      </div>

                      <hr style="margin-top: 30px; border: none; border-top: 1px solid #e5e7eb;" />
                      <p style="font-size: 12px; color: #9ca3af; margin-top: 10px;">Smart Reminder App</p>
                    </div>
                  </body>
                </html>
                """, reminder, quote);
    }
}
