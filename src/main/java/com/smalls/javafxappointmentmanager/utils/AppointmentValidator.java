package com.smalls.javafxappointmentmanager.utils;

import com.smalls.javafxappointmentmanager.model.Appointment;
import javafx.collections.ObservableMap;

import java.time.LocalDateTime;

public class AppointmentValidator {

    //for a new appointment
    public static boolean isAppointmentConflict(
            LocalDateTime start,
            LocalDateTime end,
            int clientId,
            ObservableMap<Integer, Appointment> appointments
    ) {
        return isAppointmentConflict(
                start,
                end,
                clientId,
                -1,
                appointments
        );
    }

    //for appointment to be modified
    public static boolean isAppointmentConflict(
            LocalDateTime start,
            LocalDateTime end,
            int clientId,
            int appointmentId,
            ObservableMap<Integer, Appointment> appointments
    ) {
        for (Appointment a : appointments.values()) {
            if (a.getId() == appointmentId) {
                continue; //don't compare appointment to itself
            }

            if (clientId == a.getClientId()) {
                LocalDateTime aStart = a.getStart().toLocalDateTime();
                LocalDateTime aEnd = a.getEnd().toLocalDateTime();
                if (hasConflict(start, end, aStart, aEnd)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasConflict(
            LocalDateTime start1,
            LocalDateTime end1,
            LocalDateTime start2,
            LocalDateTime end2
    ) {
        return (start1.isBefore(end2) || start1.isEqual(end2)) &&
                (end1.isAfter(start2) || end1.isEqual(start2));
    }
}
