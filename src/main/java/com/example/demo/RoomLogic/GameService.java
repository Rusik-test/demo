package com.example.demo.RoomLogic;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private final Map<String, Piece[][]> boards = new HashMap<>();
    private final Map<String, String> turnMap = new HashMap<>();

    public static class MoveResult {
        private final boolean valid;
        private final boolean becameQueen;
        private final int[] capturedChecker;
        private final String nextTurn;
        private final boolean gameOver;
        private final String winner;
        private final String errorMessage;

        public MoveResult(boolean valid, boolean becameQueen, int[] capturedChecker,
                          String nextTurn, boolean gameOver, String winner, String errorMessage) {
            this.valid = valid;
            this.becameQueen = becameQueen;
            this.capturedChecker = capturedChecker;
            this.nextTurn = nextTurn;
            this.gameOver = gameOver;
            this.winner = winner;
            this.errorMessage = errorMessage;
        }
        public boolean isValid() { return valid; }
        public boolean isBecameQueen() { return becameQueen; }
        public int[] getCapturedChecker() { return capturedChecker; }
        public String getNextTurn() { return nextTurn; }
        public boolean isGameOver() { return gameOver; }
        public String getWinner() { return winner; }
        public String getErrorMessage() { return errorMessage; }
    }

    public Piece[][] getOrCreateBoard(String gameId) {
        return boards.computeIfAbsent(gameId, k -> initializeBoard());
    }

    public String getCurrentTurn(String gameId) {
        return turnMap.getOrDefault(gameId, "white");
    }

    public MoveResult makeMove(String gameId, String playerName,String playerColor, int sx, int sy, int ex, int ey) {
        Piece[][] board = getOrCreateBoard(gameId);
        String currentTurn = getCurrentTurn(gameId);
        System.out.println("Current turn: " + currentTurn + ", playerColor: " + playerColor);

        if (!playerColor.equalsIgnoreCase(currentTurn)) {
            return new MoveResult(false, false, null, currentTurn, false, null,
                    "Not your turn: expected " + currentTurn + " but got " + playerColor);
        }

        if (!isInBounds(sx, sy) || !isInBounds(ex, ey))
            return new MoveResult(false, false, null, currentTurn, false, null, "Out of bounds");

        Piece piece = board[sx][sy];
        if (piece == null || !piece.getColor().equals(currentTurn))
            return new MoveResult(false, false, null, currentTurn, false, null, "No piece or wrong color");

        if (board[ex][ey] != null)
            return new MoveResult(false, false, null, currentTurn, false, null, "End cell not empty");

        if (mustCapture(board, currentTurn, sx, sy) && !isCaptureMove(board, sx, sy, ex, ey))
            return new MoveResult(false, false, null, currentTurn, false, null, "Must capture!");

        int dx = ex - sx, dy = ey - sy;
        boolean becameQueen = false;
        int[] captured = null;

        if (!piece.isQueen()) {
            if (Math.abs(dx) == 1 && Math.abs(dy) == 1 && isCorrectDirection(piece, dx)) {
                board[ex][ey] = piece;
                board[sx][sy] = null;
                becameQueen = checkForPromotion(piece, ex);
            } else if (Math.abs(dx) == 2 && Math.abs(dy) == 2) {
                int mx = sx + dx / 2, my = sy + dy / 2;
                Piece mid = board[mx][my];
                if (mid != null && !mid.getColor().equals(currentTurn)) {
                    board[ex][ey] = piece;
                    board[sx][sy] = null;
                    board[mx][my] = null;
                    captured = new int[]{mx, my};
                    becameQueen = checkForPromotion(piece, ex);
                } else {
                    return new MoveResult(false, false, null, currentTurn, false, null, "No valid capture");
                }
            } else {
                return new MoveResult(false, false, null, currentTurn, false, null, "Invalid move");
            }
        } else {
            if (Math.abs(dx) == Math.abs(dy)) {
                int stepX = Integer.signum(dx), stepY = Integer.signum(dy);
                int x = sx + stepX, y = sy + stepY;
                boolean capturedFlag = false;

                while (x != ex && y != ey) {
                    Piece p = board[x][y];
                    if (p != null) {
                        if (p.getColor().equals(currentTurn) || capturedFlag)
                            return new MoveResult(false, false, null, currentTurn, false, null, "Queen blocked");
                        capturedFlag = true;
                        captured = new int[]{x, y};
                    }
                    x += stepX;
                    y += stepY;
                }
                board[ex][ey] = piece;
                board[sx][sy] = null;
                if (captured != null) board[captured[0]][captured[1]] = null;
            } else {
                return new MoveResult(false, false, null, currentTurn, false, null, "Invalid queen move");
            }

        }

        String winner = checkWinner(board);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                String symbol = ".";
                if (p != null) {
                    if (p.getColor().equals("white")) symbol = p.isQueen() ? "WQ" : "W";
                    if (p.getColor().equals("black")) symbol = p.isQueen() ? "BQ" : "B";
                }
                System.out.print(symbol + "\t");
            }
            System.out.println();
        }

        boolean gameOver = winner != null;

        boolean continueTurn = false;
        if (captured != null && !becameQueen) {
            if (mustCapture(board, currentTurn, ex, ey)) {
                continueTurn = true;
            }
        }

        String nextTurn = gameOver ? null : (continueTurn ? currentTurn : (currentTurn.equals("white") ? "black" : "white"));
        if (!gameOver) turnMap.put(gameId, nextTurn);


        return new MoveResult(true, becameQueen, captured, nextTurn, gameOver, winner, null);
    }

    private boolean isCorrectDirection(Piece piece, int dx) {
        return (piece.getColor().equals("white") && dx == -1) || (piece.getColor().equals("black") && dx == 1);
    }

    private boolean checkForPromotion(Piece piece, int row) {
        if ((piece.getColor().equals("white") && row == 0) || (piece.getColor().equals("black") && row == 7)) {
            piece.makeQueen();
            return true;
        }
        return false;
    }

    private boolean mustCapture(Piece[][] board, String color, int row, int col) {
        Piece p = board[row][col];
        if (p != null && p.getColor().equals(color)) {
            if (!p.isQueen()) {
                int[][] directions = {
                        {-2, -2}, {-2, 2}, {2, -2}, {2, 2}
                };
                for (int[] dir : directions) {
                    int ex = row + dir[0], ey = col + dir[1];
                    int mx = row + dir[0] / 2, my = col + dir[1] / 2;
                    if (isInBounds(ex, ey) && board[ex][ey] == null) {
                        Piece middle = board[mx][my];
                        if (middle != null && !middle.getColor().equals(color)) {
                            return true;
                        }
                    }
                }
            } else {
                int[][] directions = {
                        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
                };
                for (int[] dir : directions) {
                    int stepX = dir[0], stepY = dir[1];
                    int cx = row + stepX, cy = col + stepY;
                    boolean foundEnemy = false;
                    while (isInBounds(cx + stepX, cy + stepY)) {
                        Piece mid = board[cx][cy];
                        if (mid != null) {
                            if (mid.getColor().equals(color)) break;
                            if (foundEnemy) break;
                            foundEnemy = true;
                        } else if (foundEnemy && board[cx][cy] == null) {
                            return true;
                        }
                        cx += stepX;
                        cy += stepY;
                    }
                }
            }
        }
        return false;
    }



    private boolean isCaptureMove(Piece[][] board, int sx, int sy, int ex, int ey) {
        if (!isInBounds(ex, ey)) return false;
        Piece start = board[sx][sy];
        if (start == null) return false;
        int dx = ex - sx, dy = ey - sy;

        if (!start.isQueen()) {
            if (Math.abs(dx) == 2 && Math.abs(dy) == 2) {
                int mx = sx + dx / 2, my = sy + dy / 2;
                Piece mid = board[mx][my];
                return mid != null && !mid.getColor().equals(start.getColor());
            }
        } else {
            if (Math.abs(dx) != Math.abs(dy)) return false;

            int stepX = Integer.signum(dx), stepY = Integer.signum(dy);
            int x = sx + stepX, y = sy + stepY;
            boolean capturedFlag = false;

            while (x != ex && y != ey) {
                Piece p = board[x][y];
                if (p != null) {
                    if (p.getColor().equals(start.getColor()) || capturedFlag)
                        return false;
                    capturedFlag = true;
                }
                x += stepX;
                y += stepY;
            }
            return capturedFlag;
        }
        return false;
    }


    private String checkWinner(Piece[][] board) {
        boolean whiteExists = false, blackExists = false;
        boolean whiteCanMove = false, blackCanMove = false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                if (p != null) {
                    if (p.getColor().equals("white")) whiteExists = true;
                    if (p.getColor().equals("black")) blackExists = true;
                }
            }
        }
        if (!whiteExists) return "black";
        if (!blackExists) return "white";

        int whiteCount = countPieces(board, "white");
        int blackCount = countPieces(board, "black");

        if (whiteCount == 1 && blackCount >= 3) return "black";
        if (blackCount == 1 && whiteCount >= 3) return "white";

        whiteCanMove = hasAnyValidMove(board, "white");
        blackCanMove = hasAnyValidMove(board, "black");

        if (!whiteCanMove && blackCanMove) return "black";
        if (!blackCanMove && whiteCanMove) return "white";
        if (!whiteCanMove && !blackCanMove) return "draw";


        return null;
    }
    public Piece[][] resetBoard(String gameId) {
        Piece[][] newBoard = initializeBoard();
        boards.put(gameId, newBoard);
        turnMap.put(gameId, "white");
        return newBoard;
    }


    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }
    private int countPieces(Piece[][] board, String color) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null && board[i][j].getColor().equals(color)) {
                    count++;
                }
            }
        }
        return count;
    }


    private Piece[][] initializeBoard() {
        System.out.println("JGUHGE INITIALIZATION");
        Piece[][] board = new Piece[8][8];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 != 0) board[i][j] = new Piece("black");
            }
        }
        for (int i = 5; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 != 0) board[i][j] = new Piece("white");
            }
        }
        return board;
    }
    private boolean hasAnyValidMove(Piece[][] board, String color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.getColor().equals(color)) {
                    if (piece.isQueen()) {
                        int[][] dirs = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                        for (int[] d : dirs) {
                            int x = i + d[0], y = j + d[1];
                            while (isInBounds(x, y)) {
                                if (board[x][y] == null || isCaptureMove(board, i, j, x, y)) return true;
                                if (board[x][y] != null) break;
                                x += d[0];
                                y += d[1];
                            }
                        }
                    } else {
                        int dir = piece.getColor().equals("white") ? -1 : 1;
                        int[][] steps = {{dir, -1}, {dir, 1}, {2 * dir, -2}, {2 * dir, 2}};
                        for (int[] s : steps) {
                            int x = i + s[0], y = j + s[1];
                            if (isInBounds(x, y)) {
                                if (Math.abs(s[0]) == 1 && board[x][y] == null) return true;
                                if (Math.abs(s[0]) == 2 && isCaptureMove(board, i, j, x, y)) return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
