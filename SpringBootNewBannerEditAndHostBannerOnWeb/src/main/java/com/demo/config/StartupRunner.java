package com.demo.config;




import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.demo.service.ResumeService;

@Component
public class StartupRunner implements CommandLineRunner {

    private final ResumeService resumeService;

    public StartupRunner(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @Override
    public void run(String... args) {

        var r = resumeService.getResume();

        System.out.println("====================================");
        System.out.println("NAME : " + r.getName());
        System.out.println("ROLE : " + r.getRole());
        System.out.println("LOCATION : " + r.getLocation());
        System.out.println("SKILLS : " + r.getSkills());
        System.out.println("====================================");
    }
}