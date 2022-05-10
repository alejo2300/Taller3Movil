package com.example.login;

public class UserClass {
    String email, name, lastname, password, docId;
    Double latitude, longitude;
    boolean available;

    public UserClass() {
    }

    public UserClass(String email, String name, String lastname, String password, String docId, Double latitude, Double longitude) {
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.docId = docId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = false;
    }

    public UserClass(String email, String name, String lastname, String password, String docId) {
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.password = password;
        this.docId = docId;
        this.latitude = null;
        this.longitude = null;
        this.available = false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
