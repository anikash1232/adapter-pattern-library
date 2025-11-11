# UNC Campus Geocoder - Adapter Design Pattern

## Introduction
In this assignment, you will build a simple UNC campus geocoder that gives detailed
walking directions between any two buildings on campus! We will rely on two publicly available APIs:
**Nominatim** (https://nominatim.org/release-docs/develop/) for geocoding and **OpenRoutesService** (https://openrouteservice.org/) for routing. Nominatim is a geocoder which takes
an address and returns its latitude and longitude. OpenRoutesService takes the coordinates
of two locations and gives walking directions. You will implement two classes that communicate with these public APIs,
create a custom `UNCBuildingApi` which
stores information about different campus buildings, and create an Adapter which links your
UNC API to OpensRoutesService. Ultimately, using your adapter, calling
`getDirections("sitterson hall", "hamilton hall")` from your UNCBuildingApi will print turn-by-turn instructions.

---

## Setup
Before writing any code, you must set up your project environment. We'll go through some
background information that'll help when implementing your service.

### **1. Maven Dependencies**
**Add the following Maven dependencies to your `pom.xml`:**

```xml
<!-- HTTP client for making calls -->
<dependency>
  <groupId>com.squareup.okhttp3</groupId>
  <artifactId>okhttp</artifactId>
  <version>4.11.0</version>
</dependency>

<!-- JSON parser for handling API responses -->
<dependency>
  <groupId>org.json</groupId>
  <artifactId>json</artifactId>
  <version>20231013</version>
</dependency>
```
These are required for making HTTP requests (OkHttp) and parsing the JSON responses (org.json).
OkHttp is a popular Java library for sending and receiving HTTP requests and responses. There
are several HTTP request methods like `GET`, `POST`, `PUT`, `DELETE` that perform different actions.
In this assignment, we are only interested in reading data form the APIs,
so we will only use `GET` requests. When making an API call,
the server sends you raw data most commonly in a format called
**JSON** (JavaScript Object Notation). JSON data looks very
similar to dictionaries or maps. A simple JSON response may look something like this:
```JSON
{
  "name": "Sitterson Hall",
  "lat": 35.9100,
  "lon": -79.0560
}
```
**JSON Parsing** is the process of taking this text
and "parsing" (converting) it into Java objects we can actually use. The ``org.json``
library you added is the tool we'll use for parsing JSON text.

### **2. OpenRoutesService (ORS) API Key**

To use the OpenRoutesService API, you will need a key for authentication.
1. Go to https://openrouteservice.org/
2. Create an account to get an API key (recommend looking over the documentation to understand how to use the API)
3. In your resources folder, create a file called config.properties
4. Add the following line: `ors.api.key=your-api-key` replacing with the key you just generated.
5. In your Main.java, add the following code:
```java
// OpenRoutesService API Key
Properties cfg = new Properties();
try (InputStream in = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
    cfg.load(in);
} catch (Exception e) {
    System.out.println(e);
}
String apiKey = cfg.getProperty("ors.api.key");
```


### **3. Git Ignore**
The `config.properties` file contains your secret API key and **should never be committed to Git**.
To prevent this, modify the `.gitignore` file in the root directory of your project.  Right now, it just has standard boiler plate code for most java projects.  At the bottom, make a comment using the '#' symbol and add the path to the config.properties.

https://www.reddit.com/r/GeminiAI/comments/1ngvbmk/lost_300_due_to_an_api_key_leak_from_vibe_coding/

---

## Apprentice
The first part of this assignment should be very simple. You will create
Java classes that hold the data returned by the APIs.
These classes will only include
private, immutable fields, a constructor to initialize them, and public getters.

For coordinates:
- `Location.java`:
   - `private final double latitude;`
   - `private final double longitude;`

For a single step given by ORS:
- `Step.java`:
   - `private final String instruction;`
   - `private final double distance;`
   - `private final double duration;`
   -
For a full list of directions between two locations:
- `DirectionsResult.java`:
   - `private final List<Step> steps;`
   - `private final double totalDistance;`
   - `private final double totalDuration;`

`BuildingImpl.java`:

A Building object represents a single campus building.
Create a class called `BuildingImpl` which implements
the `Building` interface. This class will use `NominatimGeocoder`
to find its own coordinates upon creation.

- **Fields**:
   - `private final String name;`
   - `private final String description;`
   - `private Location loc;`
   - `private final NominatimGeocoder geocoder = new NominatimGeocoder("UNCGeocoder (your_email@unc.edu)");` (Use your UNC email). This string is the User-Agent, an HTTP request header that identifies the application making the request.

Initialize those fields in your constructor. Create the
full address String which will be sent to the geocoder:
`String address = name + ", Chapel Hill, North Carolina";` For `loc`
use the `NominatimGeocoder` method `geocode` which returns a `Location` object. You will implement this method later.
Don't forget to place this in a try-catch since geocode may fail!

---

## Enchanter

`NominatimGeocoder.java`

This class is responsible for geocoding: converting a building name into geographic coordinates. It will do this by sending an HTTP request to Nominatim.

The service you will be communication with is detailed here:
https://nominatim.org/release-docs/latest/api/Search/

We will use Nominatim's search endpoint.

For the following code, keep a tab open to the okhttp3 documentation.  You are expected to look up how to use this new library to understand how it works:
https://square.github.io/okhttp/5.x/okhttp/okhttp3/

- **Fields**:
   - `private final OkHttpClient client = new OkHttpClient();`
   - `private final String userAgent;`

Note: A User-Agent is required by the API policy. Use something like
"UNCGeocoder (your_email@example.com)".

- **Constructor**:
   - Initialize your userAgent variable.


- **Methods**:
   - `public String createURL(String address)`: This method builds the complete URL for the API request.
      - Use OkHttp's `HttpUrl.parse(baseURL).newBuilder()` and add the following parameters: "q", "format" and "limit". "q" is a free-form query string that the API will search for. In our case, it's just the address. For "format" use "json", and for "limit" use "1" (we only want one address).
   - `protected Request buildRequest(String url)`: This method builds the HTTP request object.
      - Return a new `Resuest.Builder()` object.
      - Set the `.url(url)`
      - Set the header: `.header("User-Agent", userAgent)`
      - Build the request using .get().build()
   - `protected String sendRequest(Request request) throws IOException`: This method executes the request.
      - Use  `client.newCall(request).execute()`
      - Check if the response is not successful (`!resp.isSuccessful()`) or if it returned null `resp.body() == null`. If so, throw an `IOException`.
      - Return the raw JSON string: `resp.body().string()`
   - `protected Location parseLocation(String json) throws IOException`: This method will deal with parsing the JSON response and will return a location object.
      -  The response is a `JSONArray`. Create a new `JSONArray` which takes your String json.
      - If `results.isEmpty()`, return `null`.
      - Get the first result: `JSONObject object = results.getJSONObject(0);`
      - Extract the latitude and longitude using object.getDouble("lat") and object.getDouble("lon")
      - Return a Location object
   - `public Location geocode(String address) throws IOException`: This method is where everything is tied together. Use all the method you created above to return a Location object.

---

`WalkingDirectionsService.java`

This class is responsible for routing: getting step-by-step walking directions between two sets of coordinates.
It will send an HTTP request to the OpenRoutesService (ORS) API. The service you will be communication with is detailed here: https://openrouteservice.org/dev/#/api-docs/v2/directions/{profile}/get

- **Fields**:
   - `private final String apiKey;`
   - `private final OkHttpClient client = new OkHttpClient();`


- **Constructor**:
   - `public WalkingDirectionsService(String apiKey)` Initialized the apiKey field.


- **Methods**: This class will implement the methods `createURL`, `buildRequest`, `sendRequest` in a similar way as you did for NominatimGeocoder. Note that your parameters for createURL will be different. Hint: what does ORS need to give a set of directions between two locations? For your query parameters, use "api_key", "start", "end", and "steps" which should be set to "true". Looking at the documentation will help with writing this method. The other two methods should be the same as NominatimGeocoder.

   - `protected DirectionsResult parseDirections(String json)`: This method will act in a similar way to parseLocation but is a lot more complex.
      - Create a `new JSONObject(json)`.
      - Navigate the JSON structure to get to the "steps" array. This is nested deep: `obj.getJSONArray("features")` -> `getJSONObject(0)` -> `getJSONObject("properties")` -> `getJSONArray("segments")`
      - Initialize an empty List of steps and accumulators for `totalDistance` and `totalTime`.
      - The `segments` array contains its own `steps` arrays. You must loop through each segment, get its steps array, and then loop through those steps.
      - Inside the inner loop (for each step):
         - String instruction = step.getString("instruction");

         - double distance = step.getDouble("distance"); (in meters)

         - double duration = step.getDouble("duration"); (in seconds)

         - Create a new Step object and it to your list.

         - Increment to totalDistance and totalTime and the distance and duration of that step.
      - After the loop, return a `DirectionsResult` object.
   - `public void getWalkingDirections(...)`:
      - This method will act as the primary public method that puts it all together. Use all the methods you created above to get a `DirectionsResult` object.
      - Once you have the `DirectionsResult`, loop through its list of `Step` objects and print the directions in a clear, numbered format ("1. Turn left - (50.0 meters, 30.5 seconds)"). Please use this exact format.
      - After the loop, print the total distance and time. ("Total distance: 511.8 m" and "Total time: 368.6)
      - Be sure to catch Exception

---

## Sorcerer (tests hidden)

Now, it's time to create your custom UNC API and your adapter!

1. **`UNCBuildingApi`** will store campus buildings and
   later be used to print walking directions. This
   interface should have the methods:
   - `Building getBuilding(String name);` — takes a string like "Sitterson Hall" and returns a Building object
   - `List<String> getAllBuildingNames();` — returns a list of the names of all buildings we have store
   - `void getDirections(String startBuilding, String endBuilding);` — this is the method that will
     be used to print walking directions.

2. **Create a class called `UNCBuildingApiImpl`** which implements
   `UNCBuildingApi`. This class should have one field:
   `private final Map<String, Building> buildings = new HashMap<>();`. Implement the method getBuilding()
   and getAllBuildingNames() as needed. For get getDirections(), leave this method empty.
   You will not be writing any implementation for this method. We
   will simply assume that there exists some implementation that prints directions here. Later when we
   create our adapter, all work in this method will simply be delegated to the OpenRoutesService API.
   Finally, create a method called add which takes a name and a description and adds that building to
   your map. This method should return nothing.



---

### Creating the Adapter Class
Take a look at the WalkingDirectionsService class. This class has the method
getWalkingDirections which takes four arguments - a start latitude, start longitude,
end latitude, and end longitude. The method prints step-by-step directions from the start to
the end location as well as the total time and distance of travel. Our UNCBuildingApi has
a getDirections method currently with no implementation. We will create an adapter class for
WalkingDirectionsService and UNCBuildingApi. This class will implement UNCBuildingApi and
wrap an instance of WDS. Following the conventions of the Adapter design pattern, this class
will be called `WalkingDirectionsServiceAdapter`.

Since you already implemented `UNCBuildingApi`, wrap an object of `UNCBuildingApiImpl`. We do this because the methods getBuilding() and getAllBuildingNames() in our
adapter have the same implementation as the UNCBuildingApiImpl class we already wrote, and we
want to avoid code duplication. These two methods will delegate everything to the base api.

For getDirections, this is where the adapter design pattern will come in. This method will simply
delegate all work to the getWalkingDirections method belonging to the WDS object (our adaptee).
As is typical when using the adapter design pattern, you will need to do parameter conversions
as necessary. Think about how we can use building names to get latitude and longitude
coordinates.

---
### Try your Adapter!
1. In your main class, create a UNCBuildingImpl object.
2. Add a bunch of campus buildings. Example: `unc.add("sitterson hall", "computer science");`
   Note: you cannot get directions between two buildings unless you add them to your UNCBuildingApi
   object. Also, it is extremely important that you spell the building name correctly or you'll get
   issues when the Nominatim calls are made to get the coordinates.
3. In a try-catch block, create a WDS object and pass your API key.
4. Now, create an object of your adapter class and pass you WDS object and
   UNCBuildingApi object as arguments.
5. Call adapter.getDirections() and give two building names. Run your program, and you will
   see walking directions between the two buildings you gave. Get creative!




