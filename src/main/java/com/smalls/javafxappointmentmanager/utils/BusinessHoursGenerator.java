package com.smalls.javafxappointmentmanager.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.*;

/**
 * generate <code>ObservableList</code> of <code>LocalTime</code>
 * objects ranging from 8am to 4pm EST in 15 minute increments
 */
public class BusinessHoursGenerator {

    public static ObservableList<LocalTime> generateBusinessHours() {
        ZoneId estZoneId = ZoneId.of("America/New_York");

        ObservableList<LocalTime> businessHours = FXCollections.observableArrayList();

        //8am EST
        ZonedDateTime currentTime = ZonedDateTime.of(
                LocalDate.now(),
                LocalTime.of(8, 0),
                estZoneId
        );

        //4pm EST
        ZonedDateTime closingTime = ZonedDateTime.of(
                LocalDate.now(),
                LocalTime.of(22, 0),
                estZoneId
        );

        while (currentTime.isBefore(closingTime)) {
            businessHours.add(
                    currentTime.withZoneSameInstant(
                            ZoneId.systemDefault()
                    ).toLocalTime()
            );
            currentTime = currentTime.plusMinutes(15);
        }

        return businessHours;
    }
}
