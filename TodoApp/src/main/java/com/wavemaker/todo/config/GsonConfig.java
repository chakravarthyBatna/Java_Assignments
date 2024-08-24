package com.wavemaker.todo.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;
import java.time.LocalTime;

public class GsonConfig {
    public static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Register custom serializers and deserializers
        gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                new com.google.gson.JsonPrimitive(src.toString())
        );
        gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                LocalDate.parse(json.getAsString())
        );

        gsonBuilder.registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) ->
                new com.google.gson.JsonPrimitive(src.toString())
        );
        gsonBuilder.registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) ->
                LocalTime.parse(json.getAsString())
        );

        return gsonBuilder.create();
    }
}

