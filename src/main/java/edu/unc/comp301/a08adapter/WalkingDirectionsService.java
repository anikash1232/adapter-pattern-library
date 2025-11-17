package edu.unc.comp301.a08adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class WalkingDirectionsService {
  private final String baseURL = "https://api.openrouteservice.org/v2/directions/foot-walking";
  private final String apiKey;
  private final OkHttpClient client = new OkHttpClient();

  public WalkingDirectionsService(String apiKey) {
    this.apiKey = apiKey;
  }

  public String createURL(
      double startLongitude, double startLatitude, double endLongitude, double endLatitude) {
    HttpUrl url =
        HttpUrl.parse(baseURL)
            .newBuilder()
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("start", startLatitude + "," + startLongitude)
            .addQueryParameter("end", endLatitude + "," + endLongitude)
            .addQueryParameter("steps", "true")
            .build();

    return url.toString();
  }

  protected Request buildRequest(String url) {
    return new Request.Builder().url(url).get().build();
  }

  protected String sendRequest(Request request) throws IOException {
    Response resp = client.newCall(request).execute();

    if (!resp.isSuccessful() || resp.body() == null) {
      throw new IOException();
    }

    return resp.body().string();
  }

  protected DirectionsResult parseDirections(String json) {

    JSONObject obj = new JSONObject(json);
    JSONArray features = obj.getJSONArray("features");
    if (features.isEmpty()) {
      return null;
    }

    JSONObject firstFeature = features.getJSONObject(0);
    JSONObject properties = firstFeature.getJSONObject("properties");
    JSONArray segments = properties.getJSONArray("segments");

    List<Step> steps = new ArrayList<>();
    double totalDistance = 0.0;
    double totalTime = 0.0;

    for (int i = 0; i < segments.length(); i++) {
      JSONObject segment = segments.getJSONObject(i);
      JSONArray stepsArray = segment.getJSONArray("steps");

      for (int j = 0; j < stepsArray.length(); j++) {
        JSONObject step = stepsArray.getJSONObject(j);

        String instruction = step.getString("instruction");
        double distance = step.getDouble("distance");
        double time = step.getDouble("duration");
        steps.add(new Step(instruction, distance, time));

        totalDistance += distance;
        totalTime += time;
      }
    }

    return new DirectionsResult(steps, totalDistance, totalTime);
  }

  public void getWalkingDirections(
      double startLongitude, double startLatitude, double endLongitude, double endLatitude) {
    try {
      String url = createURL(startLatitude, endLatitude, startLongitude, endLongitude);
      Request request = buildRequest(url);
      String json = sendRequest(request);

      DirectionsResult results = parseDirections(json);
      List<Step> steps = results.getSteps();

      for (int i = 0; i < steps.size(); i++) {
        Step step = steps.get(i);
        System.out.println(
            (i + 1)
                + step.getInstruction()
                + " - "
                + "("
                + step.getDistance()
                + " meters, "
                + step.getDuration()
                + " seconds)");
      }

      System.out.println(
          "Total distance: "
              + results.getTotalDistance()
              + " m Total Time: "
              + results.getTotalDuration());
    } catch (IOException e) {
      System.out.println("There was an error with getting the walking directions.");
    }
  }
}
