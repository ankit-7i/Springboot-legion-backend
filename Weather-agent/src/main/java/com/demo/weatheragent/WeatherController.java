package com.demo.weatheragent;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@CrossOrigin(origins = "*")        // ← allows browser to call from any origin
public class WeatherController {

    private final AgentService agentService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public WeatherController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping(value = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter ask(@RequestBody String question) {
        SseEmitter emitter = new SseEmitter(60_000L);
        executor.submit(() -> {
            try {
                agentService.ask(question).blockingForEach(event -> {
                    String text = agentService.extractText(event);
                    if (!text.isEmpty()) {
                        emitter.send(SseEmitter.event().data(text));
                    }
                });
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    @GetMapping("/health")
    public String health() {
        return "WeatherMind Agent is running!";
    }
}