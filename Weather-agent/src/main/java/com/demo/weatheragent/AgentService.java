package com.demo.weatheragent;

import com.google.adk.agents.RunConfig;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    private InMemoryRunner runner;
    private Session session;

    private final RunConfig sseConfig = RunConfig.builder()
        .streamingMode(RunConfig.StreamingMode.SSE)
        .build();

    @PostConstruct
    public void init() {
        runner = new InMemoryRunner(WeatherAgent.ROOT_AGENT);
        session = runner.sessionService()
            .createSession(runner.appName(), "user-001")
            .blockingGet();
    }

    public Flowable<Event> ask(String question) {
        Content msg = Content.fromParts(Part.fromText(question));
        return runner.runAsync(session.userId(), session.id(), msg, sseConfig);
    }

    public String extractText(Event event) {
        return event.content()
            .flatMap(c -> c.parts()
                .map(parts -> parts.stream()
                    .map(p -> p.text().orElse(""))
                    .reduce("", String::concat)))
            .orElse("");
    }
}