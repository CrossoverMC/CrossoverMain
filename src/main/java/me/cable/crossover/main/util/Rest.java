package me.cable.crossover.main.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public final class Rest {

    public static final String HOST = "http://154.49.246.195:8080";

    public static @NotNull String getStatusMessage(@NotNull JSONObject jsonObject) {
        String str = (String) jsonObject.get("statusMessage");
        return str == null ? "500" : str;
    }

    public static void getRequest(@NotNull String endpoint, @Nullable Consumer<JSONObject> callback) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .build();

        handleRequest(callback, () -> client.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    public static void postRequest(@NotNull String endpoint,
                                   @NotNull Map<String, Object> body,
                                   @Nullable Consumer<JSONObject> callback) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(mapToBody(body)))
                .header("Content-Type", "application/json")
                .build();

        handleRequest(callback, () -> client.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    public static void postRequest(@NotNull String endpoint, @Nullable Consumer<JSONObject> callback) {
        postRequest(endpoint, Collections.emptyMap(), callback);
    }

    public static void putRequest(@NotNull String endpoint,
                                  @NotNull Map<String, Object> body,
                                  @Nullable Consumer<JSONObject> callback) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .PUT(HttpRequest.BodyPublishers.ofString(mapToBody(body)))
                .header("Content-Type", "application/json")
                .build();

        handleRequest(callback, () -> client.send(request, HttpResponse.BodyHandlers.ofString()));
    }

    public static void putRequest(@NotNull String endpoint, @Nullable Consumer<JSONObject> callback) {
        putRequest(endpoint, Collections.emptyMap(), callback);
    }

    private static void handleRequest(@Nullable Consumer<JSONObject> callback, @NotNull ResponseSupplier responseSupplier) {
        new Thread(() -> {
            JSONObject jsonObject;

            try {
                HttpResponse<String> response = responseSupplier.makeRequest();
                jsonObject = (JSONObject) new JSONParser().parse(response.body());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                jsonObject = new JSONObject();
            } catch (ParseException e) {
                jsonObject = new JSONObject();
            }

            if (callback != null) {
                callback.accept(jsonObject);
            }
        }).start();
    }

    private static @NotNull String mapToBody(@NotNull Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for (Entry<String, Object> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }

            sb.append('\"').append(entry.getKey()).append("\":\"").append(entry.getValue()).append('\"');
        }

        sb.append('}');
        return sb.toString();
    }

    @FunctionalInterface
    private interface ResponseSupplier {

        @NotNull HttpResponse<String> makeRequest() throws IOException, InterruptedException;
    }
}
