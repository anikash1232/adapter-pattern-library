# UNC Walking Directions

Walking directions between buildings on the UNC-Chapel Hill campus, built by adapting a
generic routing service to a campus-specific API using the adapter pattern.

## What it does

Ask for directions between two campus buildings by name — "Sitterson" to "Davis Library" —
and get back turn-by-turn walking steps. The underlying routing service knows nothing about
UNC; it works in latitude and longitude. The adapter bridges that gap: it resolves building
names to coordinates through a geocoder, delegates routing, and returns results in terms
the campus API speaks.

- Look up any campus building by name, or list all known buildings
- Turn-by-turn walking directions between any two of them
- Live geocoding through the Nominatim (OpenStreetMap) service
- Structured results — each route is a list of steps with distances

## Architecture

```
UNCBuildingApi                    campus-facing interface
  getBuilding(name)
  getAllBuildingNames()
  getDirections(start, end)

UNCBuildingApiImpl                the building registry
WalkingDirectionsService          generic lat/long routing - knows nothing about UNC
WalkingDirectionsServiceAdapter   implements UNCBuildingApi, delegates to the routing service

NominatimGeocoder                 building name -> coordinates, via OpenStreetMap
Building, Location, Step          domain types
DirectionsResult                  an ordered list of steps
```

The adapter holds both a `WalkingDirectionsService` and a `UNCBuildingApi` implementation.
Calls that are purely about buildings pass straight through to the registry; `getDirections`
is where the translation happens — names become coordinates, coordinates become a route, and
the route comes back as campus-shaped `Step` objects. Neither side had to change to work
with the other, which is the whole point of the pattern.

## Running it

Requires Java 17+ and Maven. Geocoding requires network access.

```bash
mvn clean compile exec:java
```
