package edu.unc.comp301.a08adapter;

import java.io.InputStream;
import java.util.Properties;

public class Main {
  public static void main(String[] args) {
    // OpenRoutesService API Key
    Properties cfg = new Properties();
    try (InputStream in = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
      cfg.load(in);
    } catch (Exception e) {
      System.out.println(e);
    }
    String apiKey = cfg.getProperty("ors.api.key");

    UNCBuildingApiImpl buildings = new UNCBuildingApiImpl();
    buildings.add("sitterson hall", "computer science");
    buildings.add("wilson library", "big main library");
    buildings.add("student rec center", "gym");
    buildings.add("student union", "bojangles");
    buildings.add("craige north", "last year dorm");

    try {
      WalkingDirectionsService wds = new WalkingDirectionsService(apiKey);
      WalkingDirectionsServiceAdapter adapter = new WalkingDirectionsServiceAdapter(wds, buildings);
      adapter.getDirections("craige north", "wilson library");
    } catch (Exception e) {

    }
  }
}
