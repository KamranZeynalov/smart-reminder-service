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
            ReminderSetupRequest input = objectMapper.readValue(event.getBody(), ReminderSetupRequest.class);

            Map<String, AttributeValue> item = new HashMap<>();
            item.put("email", AttributeValue.fromS(input.getEmail()));
            item.put("location", AttributeValue.fromS(input.getLocation()));
            item.put("sendTime", AttributeValue.fromS(input.getSendTime()));
            item.put("reminder", AttributeValue.fromS(input.getReminder()));

            dynamoDb.putItem(PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build());

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("User reminder info saved successfully");

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("Internal server error: " + e.getMessage());
        }
    }
}
