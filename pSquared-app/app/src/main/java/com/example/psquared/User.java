package com.example.psquared;

public class User {
    public String id, type;

    // dummy constructor
    public User() {

    }

    public User (String idIn, String typeIn) {
        id = idIn;
        type = typeIn;
    }
    public String getId() {
        System.out.println(id);
        return id;

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

