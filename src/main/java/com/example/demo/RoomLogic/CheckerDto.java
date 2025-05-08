package com.example.demo.RoomLogic;

public class CheckerDto {
    private int row;
    private int col;
    private String type; // "White", "Black", "WhiteQueen", "BlackQueen"

    public CheckerDto(int row, int col, String type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public String getType() { return type; }
}