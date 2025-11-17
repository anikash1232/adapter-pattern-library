package edu.unc.comp301.a08adapter;

import java.util.List;

public class WalkingDirectionsServiceAdapter implements UNCBuildingApi {

  private final WalkingDirectionsService wds;
  private final UNCBuildingApi impl;

  public WalkingDirectionsServiceAdapter(WalkingDirectionsService wds, UNCBuildingApi impl) {
    this.wds = wds;
    this.impl = impl;
  }

  @Override
  public Building getBuilding(String name) {
    return impl.getBuilding(name);
  }

  @Override
  public List<String> getAllBuildingNames() {
    return impl.getAllBuildingNames();
  }

  @Override
  public void getDirections(String startBuilding, String endBuilding) {
    Building start = impl.getBuilding(startBuilding);
    Building end = impl.getBuilding(endBuilding);

    wds.getWalkingDirections(
        start.getLongitude(), start.getLatitude(),
        end.getLongitude(), end.getLatitude());
  }
}
