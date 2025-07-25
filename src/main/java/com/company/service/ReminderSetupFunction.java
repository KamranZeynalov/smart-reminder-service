package com.company.service;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.company.model.ReminderSetupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

public class ReminderSetupFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DynamoDbClient dynamoDb = DynamoDbClient.create();
    private final String tableName = System.getenv("TABLE_NAME");


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            ReminderSetupRequest request = objectMapper.readValue(event.getBody(), ReminderSetupRequest.class);

            saveUserReminderToDynamoDB(request);

            return buildResponse(200, "User reminder info saved successfully");

        } catch (Exception ex) {
            context.getLogger().log("Error: " + ex.getMessage());
            return buildResponse(500, "Internal server error: " + ex.getMessage());
        }
    }

    private void saveUserReminderToDynamoDB(ReminderSetupRequest request) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("email", AttributeValue.fromS(request.getEmail()));
        item.put("note", AttributeValue.fromS(request.getReminder()));
        item.put("send_time", AttributeValue.fromS(request.getSendTime()));
        item.put("days", AttributeValue.fromS(String.join(",", request.getDays())));

        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());
    }

    private APIGatewayProxyResponseEvent buildResponse(int statusCode, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(body);
    }
}
