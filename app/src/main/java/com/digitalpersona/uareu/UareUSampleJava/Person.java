package com.digitalpersona.uareu.UareUSampleJava;

import java.io.Serializable;

public class Person implements Serializable {
    public Person(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    private String name;
    private String surname;
}
