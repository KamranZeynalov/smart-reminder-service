package com.company.service;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;

public class SendReminderFunction implements RequestHandler<ScheduledEvent, String> {

    @Override
    public String handleRequest(ScheduledEvent scheduledEvent, Context context) {
        return "";
    }
}
