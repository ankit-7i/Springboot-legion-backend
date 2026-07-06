package com.example.demo;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Passport {

    private int id;
    private String name;
    private String address;

    public Passport() {
        // default constructor required for JAXB
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Passport [id=" + id + ", name=" + name + ", address=" + address + "]";
    }
}
