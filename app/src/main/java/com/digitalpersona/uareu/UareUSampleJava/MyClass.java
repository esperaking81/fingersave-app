package com.digitalpersona.uareu.UareUSampleJava;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class MyClass {

    private static void launch() {
        try {
            URL url = new URL("http://localhost:5000/save?id=FMD-" + new Random().nextLong());

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "*/*");
            con.setRequestProperty("Host", "localhost:5000");
            con.setRequestProperty("Connection", "keep-alive");

            con.setDoOutput(true);

            HashMap<String, Object> data = new HashMap<>();
            data.put("name", "Espera AWO");
            data.put("age", "21");
            data.put("profession", "Mobile Developper");

            ObjectMapper objectMapper = new ObjectMapper();

            String jsonInputString = null;
            try {
                jsonInputString = objectMapper.writeValueAsString(data);
                System.out.println("JSON " + jsonInputString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
                System.out.println("Input lenght: " + input.length);
            }

            int code = con.getResponseCode();
            System.out.println("Response code => " + code);
            System.out.println("Request message => " + con.getResponseMessage());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void launchReq() {
        String url = "http://localhost:5000/save";
        long id = new Random().nextLong();
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);

        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "Espera AWO");
        data.put("age", "21");
        data.put("profession", "Mobile Developper");
    }

    public static void main(String[] args) {
        String dir = System.getProperty("user.home");
        String fName = "data.txt";
        String absPath = dir + File.separator + fName;
        try (FileOutputStream fous = new FileOutputStream(absPath)) {
            ObjectOutputStream ous = new ObjectOutputStream(fous);
            Person person = new Person("AWO", "ESPERA");

            ous.writeObject(person);
            ous.flush();
            ous.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
