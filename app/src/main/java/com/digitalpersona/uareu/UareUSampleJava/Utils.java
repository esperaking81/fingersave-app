package com.digitalpersona.uareu.UareUSampleJava;

public class Utils {

    public Utils() {

    }

    public void log(String log, Object... args) {
        System.out.println(String.format("LOG -- " + log, args));
    }

}
