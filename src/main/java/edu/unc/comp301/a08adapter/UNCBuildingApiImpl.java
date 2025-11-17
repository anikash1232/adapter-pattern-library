package edu.unc.comp301.a08adapter;

import java.util.*;

public class UNCBuildingApiImpl implements UNCBuildingApi {
  private final Map<String, Building> buildings = new HashMap<>();

  @Override
  public Building getBuilding(String name) {
    if (name == null) {
      return null;
    }
    return buildings.get(name.toLowerCase());
  }

  @Override
  public List<String> getAllBuildingNames() {
    List<String> names = new ArrayList<>();
    for (Building building : buildings.values()) {
      names.add(building.getName());
    }

    return names;
  }

  @Override
  public void getDirections(String startBuilding, String endBuilding) {}

  public void add(String name, String description) {
    if (name == null || name.isEmpty()) {
      return;
    }
    buildings.put(name.toLowerCase(), new BuildingImpl(name, description));
  }
}
