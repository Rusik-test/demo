package com.example.demo.RoomLogic;

import com.example.demo.service.ScoreService;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.types.AblyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    private final AblyRealtime ably;
    private final GameService gameService;
    @Autowired
    public GameController(GameService gameService) throws AblyException {
        this.ably = new AblyRealtime("Gcm66A.wu-rhA:rZ7OMih3kWt1XPyIXYTJG2cbEvc2uLMgw3CS2LdtwXw");
        this.gameService = gameService;
    }
    @GetMapping("/state")
    public ResponseEntity<?> getGameState(@RequestParam String gameId) {
        Piece[][] board = gameService.getOrCreateBoard(gameId);

        List<CheckerDto> checkers = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Piece piece = board[i][j];
                if (piece != null) {
                    String type = piece.getColor().equals("white")
                            ? (piece.isQueen() ? "WhiteQueen" : "White")
                            : (piece.isQueen() ? "BlackQueen" : "Black");
                    checkers.add(new CheckerDto(i, j, type));
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("checkers", checkers);
        response.put("turn", gameService.getCurrentTurn(gameId));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/move")
    public ResponseEntity<?> handleMove(@RequestBody MoveRequest moveRequest) {
        System.out.println(" POST /api/game/move JE");

        try {

            if (moveRequest == null || moveRequest.getMove() == null) {
                return ResponseEntity.status(400).body(Map.of("error", "Invalid move payload"));
            }
            System.out.println("GameId: " + moveRequest.getGameId());
            System.out.println("Player: " + moveRequest.getPlayerName());
            System.out.println("Move: from (" +
                    moveRequest.getMove().getStartX() + "," +
                    moveRequest.getMove().getStartY() + ") to (" +
                    moveRequest.getMove().getEndX() + "," +
                    moveRequest.getMove().getEndY() + ")");
            System.out.println("🧩 MOVE REQUEST: " + moveRequest);

            GameService.MoveResult result = gameService.makeMove(
                    moveRequest.getGameId(),
                    moveRequest.getPlayerName(),
                    moveRequest.getPlayerColor(),
                    moveRequest.getMove().getStartX(),
                    moveRequest.getMove().getStartY(),
                    moveRequest.getMove().getEndX(),
                    moveRequest.getMove().getEndY()
            );
            String channelName = "game-room-" + moveRequest.getGameId();
            if (result.isValid()) {
                Map<String, Object> movePayload = new HashMap<>();
                movePayload.put("move", moveRequest.getMove());
                movePayload.put("playerName", moveRequest.getPlayerName());
                movePayload.put("becameQueen", result.isBecameQueen());
                movePayload.put("captured", result.getCapturedChecker());

                ably.channels.get(channelName).publish("move", movePayload);

                Map<String, Object> turnPayload = new HashMap<>();
                turnPayload.put("nextTurn", result.getNextTurn());
                ably.channels.get(channelName).publish("turn", turnPayload);

            }

            Map<String, Object> response = new HashMap<>();
            response.put("valid", result.isValid());
            response.put("becameQueen", result.isBecameQueen());
            response.put("captured", result.getCapturedChecker());
            response.put("error", result.getErrorMessage());
            response.put("nextTurn", result.getNextTurn());
            if (result.isGameOver()) {
                if ("draw".equals(result.getWinner())) {
                    response.put("type", "draw");
                } else {
                    String winner = result.getWinner() != null ? result.getWinner() : "Player";
                    response.put("type", "win");
                    response.put("winner", winner);
                    if (result.getWinner() == null) {
                        System.err.println("Winer " + winner);
                    }
                }
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    @RequestMapping(value = "/move", method = RequestMethod.GET)
    public ResponseEntity<?> handleMoveGetFallback() {
        System.out.println("bez POST: /api/game/move");
        return ResponseEntity.status(405).body("Method Not Allowed");
    }
    @GetMapping("/reset")
    public ResponseEntity<?> resetGame(@RequestParam String gameId) {
        Piece[][] newBoard = gameService.resetBoard(gameId);
        return ResponseEntity.ok().build();
    }


}
