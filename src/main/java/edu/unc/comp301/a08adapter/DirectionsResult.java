package edu.unc.comp301.a08adapter;

import java.util.List;

public class DirectionsResult {
  private final List<Step> steps;
  private final double totalDistance;
  private final double totalDuration;

  public DirectionsResult(List<Step> steps, double totalDistance, double totalDuration) {
    this.steps = steps;
    this.totalDistance = totalDistance;
    this.totalDuration = totalDuration;
  }

  public List<Step> getSteps() {
    return steps;
  }

  public double getTotalDistance() {
    return totalDistance;
  }

  public double getTotalDuration() {
    return totalDuration;
  }
}
