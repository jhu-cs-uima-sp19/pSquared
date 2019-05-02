package com.example.psquared;

public class User {
    public String id, type, active;

    // dummy constructor
    public User() {

    }

    public User (String idIn, String typeIn, String activeIn) {
        id = idIn;
        type = typeIn;
        active = activeIn;
    }
    public String getId() {
        System.out.println(id);
        return id;

    }
    public void setActive(String active) {
        this.active = active;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        System.out.println(type);
        return type;
    }



}

