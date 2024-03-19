package com.smalls.javafxappointmentmanager.model;

import java.time.OffsetDateTime;

public class AppointmentDTO {

    private final int appointmentId;

    private final int clientId;
    private final OffsetDateTime start;

    private final OffsetDateTime end;

    public AppointmentDTO(
            int appointmentId,
            int clientId,
            OffsetDateTime start,
            OffsetDateTime end
    ) {
        this.appointmentId = appointmentId;
        this.clientId = clientId;
        this.start = start;
        this.end = end;
    }

    public int getId() {
        return appointmentId;
    }

    public int getClientId() {
        return clientId;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }
}
