package edu.unc.comp301.a08adapter;

public class Step {
  private final String instruction;
  private final double distance;
  private final double duration;

  public Step(String instruction, double distance, double duration) {
    this.instruction = instruction;
    this.distance = distance;
    this.duration = duration;
  }

  public String getInstruction() {
    return instruction;
  }

  public double getDistance() {
    return distance;
  }

  public double getDuration() {
    return duration;
  }
}
