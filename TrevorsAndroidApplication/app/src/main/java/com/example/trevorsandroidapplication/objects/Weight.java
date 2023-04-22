package com.example.trevorsandroidapplication.objects;

import java.util.UUID;

public class Weight {
    private final UUID uuid;
    private final int weight;
    private final String date;

    public Weight(UUID uuid, int weight, String date) {
        this.uuid = uuid;
        this.weight = weight;
        this.date = date;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getWeight() {
        return weight;
    }

    public String getDate() {
        return date;
    }
}
