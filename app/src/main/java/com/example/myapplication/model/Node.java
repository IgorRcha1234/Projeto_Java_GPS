
package com.example.myapplication.model;

public class Node {

    private String id;
    private float x;
    private float y;

    public Node(String id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
