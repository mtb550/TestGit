package com.example.demo;

public class Feature {
    private final int id;
    private final String name;

    public Feature(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}