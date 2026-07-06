package com.example.demo;

import java.io.File;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class Test {

    public static void main(String[] args) throws Exception {

        Passport pp = new Passport();
        pp.setId(11);
        pp.setName("Suresh");
        pp.setAddress("Chennai");

        // Create JAXB context
        JAXBContext context = JAXBContext.newInstance(Passport.class);

        // ✅ Java Object → XML (Marshalling)
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        File file = new File("passport.xml");
        marshaller.marshal(pp, file);

        System.out.println("✅ Converted Java Object to XML successfully!");

        // ✅ XML → Java Object (Unmarshalling)
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Passport obj = (Passport) unmarshaller.unmarshal(file);

        System.out.println("✅ Converted XML to Java Object successfully!");
        System.out.println(obj);
    }
}
