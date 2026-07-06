package com.demo.weatheragent;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.FunctionTool;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.json.JSONObject;

public class WeatherAgent {

    public static final BaseAgent ROOT_AGENT = initAgent();

    private static BaseAgent initAgent() {
        return LlmAgent.builder()
            .name("weather-agent")
            .model("gemini-2.5-flash")
            .instruction("""
                You are a professional weather assistant like a weather app.
                When a user asks about weather for any city or location worldwide,
                call the getWeather tool with that city name.

                Always present results in this structured format:

                📍 Location: [City, Country]
                🌡️ Temperature: [temp]°C (feels like [feels_like]°C)
                🌤️ Condition: [condition]
                💧 Humidity: [humidity]%
                💨 Wind: [wind_speed] km/h [wind_direction]
                👁️ Visibility: [visibility] km
                ☔ Umbrella needed: Yes/No
                🌅 Sunrise: [sunrise] | 🌇 Sunset: [sunset]
                📊 Today: High [temp_max]°C / Low [temp_min]°C
                ☀️ UV Index: [uv_index]

                Suggestions:
                - What to wear based on temperature and conditions
                - Activity recommendations (good/bad day for outdoor activities)
                - Health advice (UV warnings, humidity comfort)
                - Travel tip for that city

                If city is not found, politely ask the user to check the spelling.
                Always be friendly, detailed and helpful like a premium weather app.
                """)
            .tools(FunctionTool.create(WeatherAgent.class, "getWeather"))
            .build();
    }

    @Schema(description = "Get real-time weather data for any city in the world")
    public static Map<String, Object> getWeather(
        @Schema(name = "city", description = "City name to get weather for") String city
    ) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // Step 1: Geocoding — city name → lat/lon
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name="
                + city.replace(" ", "+")
                + "&count=1&language=en&format=json";

            HttpRequest geoReq = HttpRequest.newBuilder()
                .uri(URI.create(geoUrl)).GET().build();

            HttpResponse<String> geoRes = client.send(
                geoReq, HttpResponse.BodyHandlers.ofString());

            JSONObject geoJson = new JSONObject(geoRes.body());

            if (!geoJson.has("results") || geoJson.getJSONArray("results").isEmpty()) {
                return Map.of("error", "City '" + city + "' not found. Please check the spelling.");
            }

            JSONObject location = geoJson.getJSONArray("results").getJSONObject(0);
            double lat      = location.getDouble("latitude");
            double lon      = location.getDouble("longitude");
            String cityName = location.getString("name");
            String country  = location.optString("country", "Unknown");
            String timezone = location.optString("timezone", "auto");

            // Step 2: Fetch live weather from Open-Meteo (free, no key needed)
            String weatherUrl = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + lat
                + "&longitude=" + lon
                + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,"
                + "weather_code,wind_speed_10m,wind_direction_10m,visibility,"
                + "precipitation,uv_index,is_day"
                + "&daily=sunrise,sunset,temperature_2m_max,temperature_2m_min,"
                + "precipitation_sum,uv_index_max"
                + "&timezone=" + timezone
                + "&forecast_days=1";

            HttpRequest weatherReq = HttpRequest.newBuilder()
                .uri(URI.create(weatherUrl)).GET().build();

            HttpResponse<String> weatherRes = client.send(
                weatherReq, HttpResponse.BodyHandlers.ofString());

            JSONObject wJson   = new JSONObject(weatherRes.body());
            JSONObject current = wJson.getJSONObject("current");
            JSONObject daily   = wJson.getJSONObject("daily");

            double temp       = current.getDouble("temperature_2m");
            double feelsLike  = current.getDouble("apparent_temperature");
            int    humidity   = current.getInt("relative_humidity_2m");
            double windSpeed  = current.getDouble("wind_speed_10m");
            int    windDir    = current.getInt("wind_direction_10m");
            double visibility = current.optDouble("visibility", 0) / 1000.0;
            double precip     = current.optDouble("precipitation", 0);
            double uvIndex    = current.optDouble("uv_index", 0);
            int    code       = current.getInt("weather_code");
            int    isDay      = current.getInt("is_day");

            String sunrise  = daily.getJSONArray("sunrise").getString(0);
            String sunset   = daily.getJSONArray("sunset").getString(0);
            double tempMax  = daily.getJSONArray("temperature_2m_max").getDouble(0);
            double tempMin  = daily.getJSONArray("temperature_2m_min").getDouble(0);
            double precipSum = daily.getJSONArray("precipitation_sum").getDouble(0);

            String condition    = decodeWeatherCode(code, isDay);
            String windCompass  = degreeToCompass(windDir);
            boolean umbrella    = precip > 0.5 || code >= 51;

            return Map.ofEntries(
                Map.entry("city",           cityName),
                Map.entry("country",        country),
                Map.entry("temperature_c",  round(temp)),
                Map.entry("feels_like_c",   round(feelsLike)),
                Map.entry("temp_max_c",     round(tempMax)),
                Map.entry("temp_min_c",     round(tempMin)),
                Map.entry("humidity_pct",   humidity),
                Map.entry("condition",      condition),
                Map.entry("wind_speed_kmh", round(windSpeed)),
                Map.entry("wind_direction", windCompass),
                Map.entry("visibility_km",  round(visibility)),
                Map.entry("precipitation_mm", round(precipSum)),
                Map.entry("uv_index",       round(uvIndex)),
                Map.entry("umbrella_needed", umbrella),
                Map.entry("sunrise",        sunrise.length() > 11 ? sunrise.substring(11) : sunrise),
                Map.entry("sunset",         sunset.length() > 11 ? sunset.substring(11) : sunset),
                Map.entry("is_day",         isDay == 1)
            );

        } catch (Exception e) {
            return Map.of("error", "Failed to fetch weather: " + e.getMessage());
        }
    }

    private static double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    private static String decodeWeatherCode(int code, int isDay) {
        return switch (code) {
            case 0  -> isDay == 1 ? "Clear sky ☀️" : "Clear night 🌙";
            case 1  -> "Mainly clear 🌤️";
            case 2  -> "Partly cloudy ⛅";
            case 3  -> "Overcast ☁️";
            case 45, 48 -> "Foggy 🌫️";
            case 51 -> "Light drizzle 🌦️";
            case 53 -> "Moderate drizzle 🌧️";
            case 55 -> "Dense drizzle 🌧️";
            case 61 -> "Slight rain 🌧️";
            case 63 -> "Moderate rain 🌧️";
            case 65 -> "Heavy rain 🌧️";
            case 71 -> "Slight snow 🌨️";
            case 73 -> "Moderate snow ❄️";
            case 75 -> "Heavy snow ❄️";
            case 77 -> "Snow grains 🌨️";
            case 80 -> "Slight showers 🌦️";
            case 81 -> "Moderate showers 🌧️";
            case 82 -> "Violent showers ⛈️";
            case 85, 86 -> "Snow showers 🌨️";
            case 95 -> "Thunderstorm ⛈️";
            case 96, 99 -> "Thunderstorm with hail ⛈️";
            default -> "Unknown";
        };
    }

    private static String degreeToCompass(int degree) {
        String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return dirs[(int) Math.round(degree / 45.0) % 8];
    }
}