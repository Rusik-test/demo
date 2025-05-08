package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaderboard")
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "playername", nullable = false, length = 50)
    private String player1;

    @Column(name = "type", nullable = false, length = 50)
    private String type;


    public GameResult() {}

    public GameResult(String player1,String type) {
        this.player1 = player1;
        this.type = type;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlayer1() { return player1; }
    public void setPlayer1(String player1) { this.player1 = player1; }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
