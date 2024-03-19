package com.smalls.javafxappointmentmanager.utils;

import com.smalls.javafxappointmentmanager.model.Appointment;
import com.smalls.javafxappointmentmanager.model.AppointmentDTO;
import javafx.collections.ObservableMap;

import java.time.OffsetDateTime;

public class AppointmentValidator {

    public static boolean isAppointmentConflict(
            Appointment appointment,
            ObservableMap<Integer, AppointmentDTO> appointments
    ) {
        for (AppointmentDTO a : appointments.values()) {
            if (a.getId() == appointment.getId()) {
                continue; //don't compare appointment to itself
            }

            if (appointment.getClientId() == a.getClientId()) {
                OffsetDateTime aStart = a.getStart();
                OffsetDateTime aEnd = a.getEnd();
                if (hasConflict(appointment.getStart(), appointment.getEnd(), aStart, aEnd)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasConflict(
            OffsetDateTime start1,
            OffsetDateTime end1,
            OffsetDateTime start2,
            OffsetDateTime end2
    ) {
        return (start1.isBefore(end2) || start1.isEqual(end2)) &&
                (end1.isAfter(start2) || end1.isEqual(start2));
    }
}
