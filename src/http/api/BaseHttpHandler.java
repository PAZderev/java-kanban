package http.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.sun.net.httpserver.HttpExchange;
import managers.interfaces.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseHttpHandler {
    protected static final int ID_POS = 2;
    protected TaskManager taskManager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        this.taskManager = taskManager;
        gson = gsonBuilder
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                        LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                        context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .registerTypeAdapter(Duration.class, (JsonDeserializer<Duration>) (json, type, jsonDeserializationContext) ->
                        Duration.parse(json.getAsJsonPrimitive().getAsString()))
                .registerTypeAdapter(Duration.class, (JsonSerializer<Duration>) (src, typeOfSrc, context) ->
                        context.serialize(src.toString()))
                .create();
    }

    protected void sendText(HttpExchange h, String text, int code) throws IOException {
        System.out.println(text);
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        int code = 404;
        String response = "Task not Found";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        int code = 406;
        String response = "Task has interactions";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendMethodNotAllowed(HttpExchange h) throws IOException {
        int code = 405;
        String response = "Method not allowed";
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    public Gson getGson() {
        return gson;
    }
}
