package com.demo.weatheragent;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.Scanner;
import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@Profile("cli")                    // ← only runs with --spring.profiles.active=cli
public class CliRunner implements CommandLineRunner {

    private final AgentService agentService;

    public CliRunner(AgentService agentService) {
        this.agentService = agentService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Streaming Weather Agent (CLI) ===");
        System.out.println("Try: Hyderabad / London / New York");
        System.out.println("Type 'quit' to exit.\n");

        try (Scanner sc = new Scanner(System.in, UTF_8)) {
            while (true) {
                System.out.print("You > ");
                String input = sc.nextLine().trim();
                if ("quit".equalsIgnoreCase(input)) break;
                if (input.isEmpty()) continue;

                System.out.print("Agent > ");
                agentService.ask(input).blockingForEach(event -> {
                    String text = agentService.extractText(event);
                    if (!text.isEmpty()) {
                        System.out.print(text);
                        System.out.flush();
                    }
                });
                System.out.println("\n");
            }
        }
        System.out.println("Goodbye!");
    }
}