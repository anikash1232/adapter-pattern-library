package edu.unc.comp301.a08adapter;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class NominatimGeocoder {

  private final String baseURL = "https://nominatim.openstreetmap.org/search";
  private final OkHttpClient client = new OkHttpClient();
  private final String userAgent;

  public NominatimGeocoder(String userAgent){
    this.userAgent = userAgent;
  }

  public String createURL(String address){
    HttpUrl.Builder url = HttpUrl.parse(baseURL).newBuilder();
    url.addQueryParameter("q", address);
    url.addQueryParameter("format", "json");
    url.addQueryParameter("limit", "1");

    return url.toString();
  }

  protected Request buildRequest(String url) {
    return new Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .get()
            .build();
  }

  protected String sendRequest(Request request) throws IOException {
    try (Response resp = client.newCall(request).execute()) {
      if (resp == null || !resp.isSuccessful() || resp.body() == null) {
        throw new IOException("Bad response from Nominatim");
      }
      return resp.body().string();
    }
  }

  protected Location parseLocation(String json) throws IOException {
    JSONArray results = new JSONArray(json);

    if (results.isEmpty()) {
      return null;
    }

    JSONObject obj = results.getJSONObject(0);

    double lat = obj.getDouble("lat");
    double lon = obj.getDouble("lon");

    return new Location(lat, lon);
  }

  public Location geocode(String address) throws IOException {
    String url = createURL(address);
    Request req = buildRequest(url);
    String json = sendRequest(req);
    return parseLocation(json);
  }

}
