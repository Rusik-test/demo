<template>
  <div class="board-container">
    <div class="status-bar">
      <p class="turn-indicator">{{ currentTurn === 'White' ? 'Your move' : 'Bots move' }}</p>
      <p class="message">{{ gameMessage }}</p>
    </div>
    <div class="board">
      <img src="@/assets/Board.png" alt="Game Board" class="board-img" />
      <button @click="goHome" class="action-button back-button">Back to Home</button>

      <img
          v-for="(checker, index) in checkers"
          :key="index"
          :src="getCheckerImage(checker.type, checker.isKing)"
          class="checker"
          :class="{ 'selected': selectedChecker === index }"
          :style="{
          left: checker.col * cellSize + borderOffset + 'px',
          top: checker.row * cellSize + borderOffset + 'px'
        }"
          @click="handleCheckerClick(index)"
      />

      <div
          v-for="(move, index) in possibleMoves"
          :key="'move-' + index"
          class="possible-move"
          :style="{
          left: move.col * cellSize + borderOffset + 'px',
          top: move.row * cellSize + borderOffset + 'px'
        }"
          @click="makeMove(move)"
      ></div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      cellSize: 60,
      borderOffset: 16,
      checkers: [
        { row: 0, col: 1, type: "Black", isKing: false },
        { row: 0, col: 3, type: "Black", isKing: false },
        { row: 0, col: 5, type: "Black", isKing: false },
        { row: 0, col: 7, type: "Black", isKing: false },
        { row: 1, col: 0, type: "Black", isKing: false },
        { row: 1, col: 2, type: "Black", isKing: false },
        { row: 1, col: 4, type: "Black", isKing: false },
        { row: 1, col: 6, type: "Black", isKing: false },
        { row: 2, col: 1, type: "Black", isKing: false },
        { row: 2, col: 3, type: "Black", isKing: false },
        { row: 2, col: 5, type: "Black", isKing: false },
        { row: 2, col: 7, type: "Black", isKing: false },

        { row: 5, col: 0, type: "White", isKing: false },
        { row: 5, col: 2, type: "White", isKing: false },
        { row: 5, col: 4, type: "White", isKing: false },
        { row: 5, col: 6, type: "White", isKing: false },
        { row: 6, col: 1, type: "White", isKing: false },
        { row: 6, col: 3, type: "White", isKing: false },
        { row: 6, col: 5, type: "White", isKing: false },
        { row: 6, col: 7, type: "White", isKing: false },
        { row: 7, col: 0, type: "White", isKing: false },
        { row: 7, col: 2, type: "White", isKing: false },
        { row: 7, col: 4, type: "White", isKing: false },
        { row: 7, col: 6, type: "White", isKing: false }
      ],
      selectedChecker: null,
      possibleMoves: [],
      currentTurn: 'White',
      gameMessage: 'Your move! Choose a checker.',
      mustCapture: false,
      captureChain: false,
      lastCapturedChecker: null
    };
  },
  methods: {
    getCheckerImage(type, isKing) {
      if (type === "Black") {
        return isKing ? require("@/assets/Black_Queen.png") : require("@/assets/Black.png");
      } else {
        return isKing ? require("@/assets/White_Queen.png") : require("@/assets/White.png");
      }
    },
    goHome() {
      this.$router.push('/');
    },

    handleCheckerClick(index) {
      const checker = this.checkers[index];

      if (checker.type === this.currentTurn) {
        this.selectedChecker = index;
        this.showPossibleMoves();
      }
    },

    showPossibleMoves() {
      this.possibleMoves = [];
      if (this.selectedChecker === null) return;

      const checker = this.checkers[this.selectedChecker];
      const captureOptions = this.findCaptureOptions(checker);

      if (captureOptions.length > 0) {
        this.possibleMoves = captureOptions;
        this.mustCapture = true;
      } else if (!this.mustCapture) {
        this.possibleMoves = this.findMoveOptions(checker);
      }
    },

    findMoveOptions(checker) {
      const moves = [];
      const { row, col, type, isKing } = checker;

      const directions = type === 'White' ? [
        { rowDir: -1, colDir: -1 },
        { rowDir: -1, colDir: 1 }
      ] : [
        { rowDir: 1, colDir: -1 },
        { rowDir: 1, colDir: 1 }
      ];

      const kingDirections = [
        { rowDir: -1, colDir: -1 },
        { rowDir: -1, colDir: 1 },
        { rowDir: 1, colDir: -1 },
        { rowDir: 1, colDir: 1 }
      ];

      const dirsToCheck = isKing ? kingDirections : directions;

      for (const dir of dirsToCheck) {
        const newRow = row + dir.rowDir;
        const newCol = col + dir.colDir;

        if (newRow < 0 || newRow > 7 || newCol < 0 || newCol > 7) continue;

        if (!this.getCheckerAt(newRow, newCol)) {
          moves.push({ row: newRow, col: newCol });
        }
        if (isKing) {
          let r = newRow + dir.rowDir;
          let c = newCol + dir.colDir;

          while (r >= 0 && r <= 7 && c >= 0 && c <= 7) {
            if (this.getCheckerAt(r, c)) break;
            moves.push({ row: r, col: c });
            r += dir.rowDir;
            c += dir.colDir;
          }
        }
      }

      return moves;
    },

    findCaptureOptions(checker) {
      const captures = [];
      const { row, col, type, isKing } = checker;

      const directions = [
        { rowDir: -1, colDir: -1 },
        { rowDir: -1, colDir: 1 },
        { rowDir: 1, colDir: -1 },
        { rowDir: 1, colDir: 1 }
      ];

      for (const dir of directions) {
        if (!isKing) {
          const opponentRow = row + dir.rowDir;
          const opponentCol = col + dir.colDir;
          const landingRow = opponentRow + dir.rowDir;
          const landingCol = opponentCol + dir.colDir;

          if (opponentRow >= 0 && opponentRow <= 7 &&
              opponentCol >= 0 && opponentCol <= 7 &&
              landingRow >= 0 && landingRow <= 7 &&
              landingCol >= 0 && landingCol <= 7) {

            const opponentChecker = this.getCheckerAt(opponentRow, opponentCol);

            if (opponentChecker && opponentChecker.type !== type && !this.getCheckerAt(landingRow, landingCol)) {
              captures.push({
                row: landingRow,
                col: landingCol,
                captureRow: opponentRow,
                captureCol: opponentCol
              });
            }
          }
        } else {
          let r = row + dir.rowDir;
          let c = col + dir.colDir;
          let foundOpponent = false;
          let opponentRow, opponentCol;

          while (r >= 0 && r <= 7 && c >= 0 && c <= 7) {
            const checkerAt = this.getCheckerAt(r, c);

            if (!checkerAt) {
              if (foundOpponent) {
                captures.push({
                  row: r,
                  col: c,
                  captureRow: opponentRow,
                  captureCol: opponentCol
                });
              }
            } else if (!foundOpponent && checkerAt.type !== type) {
              foundOpponent = true;
              opponentRow = r;
              opponentCol = c;
            } else {
              break;
            }

            r += dir.rowDir;
            c += dir.colDir;
          }
        }
      }

      return captures;
    },
    getCheckerAt(row, col) {
      return this.checkers.find(c => c.row === row && c.col === col);
    },

    makeMove(move) {
      if (this.selectedChecker === null) return;

      const checker = this.checkers[this.selectedChecker];

      if (move.captureRow !== undefined) {
        const capturedCheckerIndex = this.checkers.findIndex(
            c => c.row === move.captureRow && c.col === move.captureCol
        );

        if (capturedCheckerIndex !== -1) {
          this.checkers.splice(capturedCheckerIndex, 1);
          this.lastCapturedChecker = { row: move.captureRow, col: move.captureCol };

          if (this.selectedChecker > capturedCheckerIndex) {
            this.selectedChecker--;
          }
        }
      }

      checker.row = move.row;
      checker.col = move.col;

      if (!checker.isKing) {
        if ((checker.type === 'White' && checker.row === 0) ||
            (checker.type === 'Black' && checker.row === 7)) {
          checker.isKing = true;
        }
      }

      let canContinueCapture = false;
      if (move.captureRow !== undefined) {
        const furtherCaptures = this.findCaptureOptions(checker);
        if (furtherCaptures.length > 0) {
          canContinueCapture = true;
          this.captureChain = true;
          this.selectedChecker = this.checkers.findIndex(c => c.row === checker.row && c.col === checker.col);
          this.possibleMoves = furtherCaptures;
          this.gameMessage = 'You can continue the takedown!';
        }
      }

      if (!canContinueCapture) {
        this.endTurn();
      }
    },

    endTurn() {
      this.selectedChecker = null;
      this.possibleMoves = [];
      this.mustCapture = false;
      this.captureChain = false;
      this.lastCapturedChecker = null;

      this.currentTurn = this.currentTurn === 'White' ? 'Black' : 'White';

      if (this.checkGameOver()) {
        this.gameMessage = `${this.currentTurn === 'White' ? 'Black' : 'White'} won!`;
        return;
      }

      if (this.currentTurn === 'Black') {
        this.gameMessage = 'Bots move....';
        setTimeout(() => this.makeBotMove(), 1000);
      } else {
        this.gameMessage = 'Your move! Choose a checker.';

        this.checkForcedCapture();
      }
    },

    checkGameOver() {
      const currentTypeCheckers = this.checkers.filter(c => c.type === this.currentTurn);

      if (currentTypeCheckers.length === 0) return true;

      for (const checker of currentTypeCheckers) {
        const captures = this.findCaptureOptions(checker);
        const moves = this.findMoveOptions(checker);

        if (captures.length > 0 || moves.length > 0) {
          return false;
        }
      }

      return true;
    },

    checkForcedCapture() {
      const playerCheckers = this.checkers.filter(c => c.type === 'White');

      for (const checker of playerCheckers) {
        const captures = this.findCaptureOptions(checker);

        if (captures.length > 0) {
          this.mustCapture = true;
          this.gameMessage = 'You have a compulsory takedown!';
          return;
        }
      }
    },

    makeBotMove() {
      const botCheckers = this.checkers.filter(c => c.type === 'Black');
      let captureOptions = [];

      for (const checker of botCheckers) {
        const checkerIndex = this.checkers.indexOf(checker);
        const captures = this.findCaptureOptions(checker);

        if (captures.length > 0) {
          captureOptions.push({ checkerIndex, moves: captures });
        }
      }

      if (captureOptions.length > 0) {
        const randomCaptureOption = captureOptions[Math.floor(Math.random() * captureOptions.length)];
        const randomMove = randomCaptureOption.moves[Math.floor(Math.random() * randomCaptureOption.moves.length)];

        this.selectedChecker = randomCaptureOption.checkerIndex;
        this.makeMove(randomMove);


        if (this.captureChain) {
          setTimeout(() => this.makeBotMove(), 1000);
          return;
        }
      }
      else {
        let moveOptions = [];

        for (const checker of botCheckers) {
          const checkerIndex = this.checkers.indexOf(checker);
          const moves = this.findMoveOptions(checker);

          if (moves.length > 0) {
            moveOptions.push({ checkerIndex, moves });
          }
        }

        if (moveOptions.length > 0) {
          const randomMoveOption = moveOptions[Math.floor(Math.random() * moveOptions.length)];
          const randomMove = randomMoveOption.moves[Math.floor(Math.random() * randomMoveOption.moves.length)];

          this.selectedChecker = randomMoveOption.checkerIndex;
          this.makeMove(randomMove);
        } else {
          this.gameMessage = 'You win! The bot has no possible moves.';
          this.currentTurn = 'White';
        }
      }
    }
  }
};
</script>

<style scoped>
.board-container {
  background-image: url('@/assets/GameWithBotBackground.png');
  background-size: cover;
  background-position: center;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  color: white;
}

.status-bar {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
}

.turn-indicator {
  position: absolute;
  top: 120px; /* вертикально вниз */
  left: 50%;
  transform: translateX(-50%);
  font-size: 24px;
  font-weight: bold;
  color: white;
}


.message {
  position: absolute;
  top: 150px;
  left: 46%;
  font-size: 18px;
  font-style: italic;
}

.board {
  position: relative;
  width: 510px;
  height: 510px;
  margin: 0 auto;
}

.board-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.checker {
  position: absolute;
  width: 60px;
  height: 60px;
  object-fit: contain;
  cursor: pointer;
  transition: transform 0.2s;
}

.checker.selected {
  transform: scale(1.1);
  box-shadow: 0 0 10px 5px rgba(255, 255, 0, 0.5);
  border-radius: 50%;
}

.possible-move {
  position: absolute;
  width: 60px;
  height: 60px;
  background-color: rgba(0, 255, 0, 0.3);
  border-radius: 50%;
  cursor: pointer;
}

.action-button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  border: none;
  cursor: pointer;
  font-size: 18px;
  transition: 0.3s;
}

.action-button:hover {
  background-color: rgba(0, 0, 0, 0.9);
}
</style>