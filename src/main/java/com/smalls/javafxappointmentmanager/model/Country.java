package com.smalls.javafxappointmentmanager.model;

public class Country {

    private final int id;

    private final String name;

    public Country(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
