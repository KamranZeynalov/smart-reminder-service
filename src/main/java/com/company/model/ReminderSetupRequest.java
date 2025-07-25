package com.company.model;

import java.util.List;

public class ReminderSetupRequest {
    private String email;
    private String sendTime;
    private String reminder;
    private List<String> days;

    public String getEmail() {
        return email;
    }

    public String getSendTime() {
        return sendTime;
    }

    public String getReminder() {
        return reminder;
    }

    public List<String> getDays() {
        return days;
    }
}
