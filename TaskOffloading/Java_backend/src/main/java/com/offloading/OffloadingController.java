package com.offloading;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OffloadingController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "healthy", "backend", "Java Spring Boot");
    }

    @PostMapping("/optimize")
    public Map<String, Object> optimize(
            @RequestParam(defaultValue = "PSO") String algorithm,
            @RequestParam(defaultValue = "20") int tasksCount) {
        PsoOptimizer optimizer = new PsoOptimizer(tasksCount);
        Map<String, Object> result = optimizer.runPSO(30, 20);
        result.put("algorithm", algorithm.toUpperCase());
        result.put("tasks_count", tasksCount);
        return result;
    }
}