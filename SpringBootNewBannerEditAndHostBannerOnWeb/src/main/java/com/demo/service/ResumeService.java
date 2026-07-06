package com.demo.service;

import org.springframework.stereotype.Service;

import com.demo.model.Resume;

@Service
public class ResumeService {

    public Resume getResume() {

        return new Resume(
                "ANKIT ROUT",
                "Full Stack Java Developer",
                "Bhubaneswar, Odisha",
                "Java, Spring Boot, Microservices, React, Docker, AWS"
        );
    }
}