package com.smalls.javafxappointmentmanager.model;

public class ContactReportRecord {

    private final int id;

    private final String contact;

    private final int numAppointments;

    private final double totalAppointmentHrs;

    public ContactReportRecord(
            int id,
            String contact,
            int numAppointments,
            double totalAppointmentHrs
    ) {
        this.id = id;
        this.contact = contact;
        this.numAppointments = numAppointments;
        this.totalAppointmentHrs = totalAppointmentHrs;
    }

    public int getId() {
        return id;
    }

    public String getContact() {
        return contact;
    }

    public int getNumAppointments() {
        return numAppointments;
    }

    public double getTotalAppointmentHrs() {
        return totalAppointmentHrs;
    }
}
