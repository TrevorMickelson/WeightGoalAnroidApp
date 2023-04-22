package com.example.trevorsandroidapplication.objects;

import androidx.annotation.NonNull;

import java.util.UUID;

public class User {
    private final UUID uuid;
    private final String name;
    private final String password;
    private int weightGoal;

    public User(UUID uuid, String name, String password, int weightGoal) {
        this.uuid = uuid;
        this.name = name;
        this.password = password;
        this.weightGoal = weightGoal;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(int weightGoal) {
        this.weightGoal = weightGoal;
    }

    public boolean hasUserSetWeightGoal() {
        return weightGoal > 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
