package com.demo.service;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ResumeParserService {

    public String readResume() {

        try {
            InputStream input =
                    new ClassPathResource("resume/Ankit_Rout_7682949708.pdf")
                    .getInputStream();

            PDDocument document = PDDocument.load(input);

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            document.close();
            return text;

        } catch (Exception e) {
            return "Unable to read Resume PDF";
        }
    }
}