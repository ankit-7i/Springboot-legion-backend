package com.demo.model;

public class Resume {

    private String name;
    private String role;
    private String location;
    private String skills;

    public Resume(String name, String role, String location, String skills) {
        this.name = name;
        this.role = role;
        this.location = location;
        this.skills = skills;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public String getLocation() { return location; }
    public String getSkills() { return skills; }
}