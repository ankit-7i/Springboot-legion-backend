package com.example.demo;



import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JavaToJson {

    public static void main(String[] args) throws IOException {

        Passport pp = new Passport();
        pp.setId(11);
        pp.setName("Suresh");
        pp.setAddress("Chennai");

        ObjectMapper mapper = new ObjectMapper();

        // ✅ Java Object → JSON
        mapper.writerWithDefaultPrettyPrinter()
              .writeValue(new File("passport.json"), pp);

        System.out.println("✅ Converted Java Object to JSON successfully!");
    }
}
