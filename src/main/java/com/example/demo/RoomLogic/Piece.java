package com.example.demo.RoomLogic;

public class Piece {
    private String color;
    private boolean isQueen;

    public Piece(String color) {
        this.color = color;
        this.isQueen = false;
    }

    public String getColor() { return color; }
    public boolean isQueen() { return isQueen; }
    public void makeQueen() { this.isQueen = true; }
}
