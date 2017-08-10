package com.chenesseau.denis.spyfall;

import android.widget.Toast;

/**
 * Created by Denis on 31/07/2017.
 */

public class Player {

    private String name;
    private String phoneNumber;


    public Player(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return this.getName() + " || " + this.getPhoneNumber();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
