package com.smalls.javafxappointmentmanager.model;

/**
 * state or province
 */
public class AdministrativeDivision {

    private final int id;

    private final String name;

    private final int countryId;

    public AdministrativeDivision(int id, String name, int countryId) {
        this.id = id;
        this.name = name;
        this.countryId = countryId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCountryId() {
        return countryId;
    }
}
