package com.offloading;

import java.util.*;

public class PsoOptimizer {
    private int numTasks;
    private double[][] tasks;

    public PsoOptimizer(int numTasks) {
        this.numTasks = numTasks;
        this.tasks = new double[numTasks][3];
        Random rand = new Random(42);
        for (int i = 0; i < numTasks; i++) {
            tasks[i][0] = 10 + rand.nextDouble() * 190;
            tasks[i][1] = 50 + rand.nextDouble() * 950;
            tasks[i][2] = 20 + rand.nextDouble() * 180;
        }
    }

    public double[] evaluateFitness(int[] individual) {
        double totalLatency = 0.0;
        double totalEnergy = 0.0;

        for (int i = 0; i < numTasks; i++) {
            double cpu = tasks[i][0];
            double data = tasks[i][1];
            int target = individual[i];

            double lat = 0, eng = 0;
            switch (target) {
                case 0: lat = cpu / 0.5; eng = cpu * 0.1; break;
                case 1: lat = (data / 100.0) + (cpu / 2.0); eng = cpu * 0.05; break;
                case 2: lat = (data / 50.0) + (cpu / 5.0); eng = cpu * 0.03; break;
                case 3: lat = (data / 10.0) + (cpu / 15.0); eng = cpu * 0.01; break;
            }
            totalLatency += lat;
            totalEnergy += eng;
        }
        return new double[]{totalLatency, totalEnergy};
    }

    public Map<String, Object> runPSO(int iterations, int swarmSize) {
        Random rand = new Random();
        int[][] positions = new int[swarmSize][numTasks];
        double[][] velocities = new double[swarmSize][numTasks];
        int[][] pBest = new int[swarmSize][numTasks];
        double[] pBestScores = new double[swarmSize];

        int[] gBest = new int[numTasks];
        double gBestScore = Double.MAX_VALUE;

        for (int i = 0; i < swarmSize; i++) {
            for (int j = 0; j < numTasks; j++) {
                positions[i][j] = rand.nextInt(4);
                velocities[i][j] = rand.nextDouble() * 2 - 1;
                pBest[i][j] = positions[i][j];
            }
            double score = evaluateFitness(positions[i])[0];
            pBestScores[i] = score;
            if (score < gBestScore) {
                gBestScore = score;
                gBest = positions[i].clone();
            }
        }

        List<Double> convergence = new ArrayList<>();
        for (int it = 0; it < iterations; it++) {
            for (int i = 0; i < swarmSize; i++) {
                for (int j = 0; j < numTasks; j++) {
                    velocities[i][j] = 0.5 * velocities[i][j] 
                        + rand.nextDouble() * (pBest[i][j] - positions[i][j]) 
                        + rand.nextDouble() * (gBest[j] - positions[i][j]);
                    int newPos = (int) Math.round(positions[i][j] + velocities[i][j]);
                    positions[i][j] = Math.max(0, Math.min(3, newPos));
                }
                double score = evaluateFitness(positions[i])[0];
                if (score < pBestScores[i]) {
                    pBest[i] = positions[i].clone();
                    pBestScores[i] = score;
                    if (score < gBestScore) {
                        gBestScore = score;
                        gBest = positions[i].clone();
                    }
                }
            }
            convergence.add(gBestScore);
        }

        double[] finalMetrics = evaluateFitness(gBest);
        Map<String, Object> result = new HashMap<>();
        result.put("best_solution", gBest);
        result.put("latency", Math.round(finalMetrics[0] * 100.0) / 100.0);
        result.put("energy", Math.round(finalMetrics[1] * 100.0) / 100.0);
        result.put("convergence_history", convergence);
        return result;
    }
}