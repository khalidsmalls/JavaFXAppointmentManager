package com.smalls.javafxappointmentmanager.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class AppointmentReportRecord {

    private final int appointmentId;

    private final String description;

    private final String location;

    private final String type;

    private final LocalDate date;

    private final OffsetDateTime start;

    private final OffsetDateTime end;

    private final String client;

    private final String address;

    private final String postalCode;

    private final String division;

    private final String country;

    private final String phone;

    private final int contactId;

    private final String contact;

    public AppointmentReportRecord(
            int appointmentId,
            String description,
            String location,
            String type,
            LocalDate date,
            OffsetDateTime start,
            OffsetDateTime end,
            String client,
            String address,
            String postalCode,
            String division,
            String country,
            String phone,
            int contactId,
            String contact
    ) {
        this.appointmentId = appointmentId;
        this.description = description;
        this.location = location;
        this.type = type;
        this.date = date;
        this.start = start;
        this.end = end;
        this.client = client;
        this.address = address;
        this.postalCode = postalCode;
        this.division = division;
        this.country = country;
        this.phone = phone;
        this.contactId = contactId;
        this.contact = contact;
    }

    public int getAppointmentId() {
        return appointmentId;
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

    public LocalDate getDate() {
        return date;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public String getClient() {
        return client;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getDivision() {
        return division;
    }

    public String getCountry() {
        return country;
    }

    public String getPhone() {
        return phone;
    }

    public int getContactId() {
        return contactId;
    }

    public String getContact() {
        return contact;
    }
}
