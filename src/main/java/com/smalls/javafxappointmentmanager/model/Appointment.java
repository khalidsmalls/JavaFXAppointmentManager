package com.smalls.javafxappointmentmanager.model;

import java.time.OffsetDateTime;

public class Appointment {

    private final int id;

    private final String description;

    private final String location;

    private final String type;

    private final OffsetDateTime start;

    private final OffsetDateTime end;

    private final int clientId;

    private final int userId;

    private final int contactId;

    public Appointment(
            int id,
            String description,
            String location,
            String type,
            OffsetDateTime start,
            OffsetDateTime end,
            int clientId,
            int userId,
            int contactId
    ) {
        this.id = id;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.clientId = clientId;
        this.userId = userId;
        this.contactId = contactId;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public int getClientId() {
        return clientId;
    }

    public int getUserId() {
        return userId;
    }

    public int getContactId() {
        return contactId;
    }
}
