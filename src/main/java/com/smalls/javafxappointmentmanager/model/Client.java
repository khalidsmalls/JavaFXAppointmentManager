package com.smalls.javafxappointmentmanager.model;

public class Client {

    private final int id;

    private final String name;

    private final String address;

    private final String postalCode;

    private final String phone;

    private final int divisionId;

    public Client(
            int id,
            String name,
            String address,
            String postalCode,
            String phone,
            int divisionId
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public int getDivisionId() {
        return divisionId;
    }
}
