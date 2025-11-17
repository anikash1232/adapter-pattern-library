package edu.unc.comp301.a08adapter;

import java.util.List;

public interface UNCBuildingApi {
  Building getBuilding(String name);

  List<String> getAllBuildingNames();

  void getDirections(String startBuilding, String endBuilding);
}
