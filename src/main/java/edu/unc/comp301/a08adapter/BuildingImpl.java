package edu.unc.comp301.a08adapter;

public class BuildingImpl implements Building {
  private final String name;
  private final String description;
  private final NominatimGeocoder geocoder = new NominatimGeocoder("UNCGeocoder (anikash@unc.edu)");
  private Location loc;

  public BuildingImpl(String name, String description) {
    this.name = name;
    this.description = description;

    String address = name + ", Chapel Hill, North Carolina";

    try {
      this.loc = geocoder.geocode(address);
    } catch (Exception e) {

    }
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public double getLatitude() {
    return loc.getLatitude();
  }

  @Override
  public double getLongitude() {
    return loc.getLongitude();
  }

  public Location getLocation() {
    return loc;
  }
}
