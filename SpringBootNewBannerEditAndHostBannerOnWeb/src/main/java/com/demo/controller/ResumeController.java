package com.demo.controller;





import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.service.ResumeParserService;

@RestController
public class ResumeController {

    private final ResumeParserService service;

    public ResumeController(ResumeParserService service) {
        this.service = service;
    }

    @GetMapping("/api/resume")
    public String getResume() {
        return service.readResume();
    }
}