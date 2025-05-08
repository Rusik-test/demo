<template>
  <div class="main-container">
    <div class="Chat-container">
      <h1>Awesome chat - <span class="connection_ready" v-if="connection_ready">Connection ready!</span></h1>

      <div class="messages" id="messages">
        <div class="message-container">
          <h1 class="error" v-if="connection_error"> Connection error! </h1>
          <div v-for="(m, idx) in messages" :key="'m-' + idx" style="clear:both">
            <div :class="{ 'msg-from-me': m.from == 'me', 'msg-from-other': m.from == 'other' }">
              {{ m.message }}
            </div>
          </div>
        </div>
      </div>

      <div class="send-zone">
        <input v-model="new_message" type="text" placeholder="Type a message" @keyup.enter="send_message" />
      </div>
    </div>

    <div class="board-container">
      <div v-if="gameOver" class="end-panel">
        <div v-if="winnerName === nickname" class="win-anim">
          <h2>🎉 You won, {{ winnerName }}!</h2>
          <label>
            Rate (1–5):
            <input v-model="rateInput" type="number" min="1" max="5" class="rate-input" />
          </label>
          <br />
          <label>
            Comment:
            <input v-model="commentInput" type="text" placeholder="Write a comment" class="comment-input" />
          </label>
          <br />
          <button class="save-button" @click="saveResult">💾 Save Result</button>
        </div>
        <div v-else class="lose-anim">
          <h2>😞 You lost!</h2>
        </div>
        <br />
        <button class="home-button" @click="goHome">🏠 Back to Home</button>
        <button class="play-again-button" @click="resetGame">🔁 Play Again</button>
      </div>

      <h2 class="room-title">Game Room: {{ roomId }}</h2>
      <p class="room-text">Welcome to the game room. Start playing with your friend!</p>
      <p class="room-text">First Player : {{firstPlayerWhite}}</p>
      <p class="room-text">First Player : {{secondPlayerBlack}}</p>
      <button class="debug-button" @click="debugOpenEndPanel">🐞 Debug Льoша</button>

      <div class="board" @click="handleBoardClick">
        <img src="@/assets/Board.png" alt="Game Board" class="board-img" />
        <img
            v-for="(checker, index) in checkers"
            :key="index"
            :src="getCheckerImage(checker.type)"
            class="checker"
            :style="{
            left: checker.col * cellSize + borderOffset + 'px',
            top: checker.row * cellSize + borderOffset + 'px'
          }"
            @click.stop="selectChecker(checker.row, checker.col)"
        />
      </div>
    </div>
  </div>
</template>

<script>
import * as Ably from 'ably';
import axios from 'axios';

export default {
  computed: {
    roomId() {
      return this.$route.params.roomId;
    }
  },
  data() {
    return {
      cellSize: 60,
      borderOffset: 16,
      checkers: [
        { row: 0, col: 1, type: "Black" },
        { row: 0, col: 3, type: "Black" },
        { row: 0, col: 5, type: "Black" },
        { row: 0, col: 7, type: "Black" },
        { row: 1, col: 0, type: "Black" },
        { row: 1, col: 2, type: "Black" },
        { row: 1, col: 4, type: "Black" },
        { row: 1, col: 6, type: "Black" },
        { row: 2, col: 1, type: "Black" },
        { row: 2, col: 3, type: "Black" },
        { row: 2, col: 5, type: "Black" },
        { row: 2, col: 7, type: "Black" },
        { row: 5, col: 0, type: "White" },
        { row: 5, col: 2, type: "White" },
        { row: 5, col: 4, type: "White" },
        { row: 5, col: 6, type: "White" },
        { row: 6, col: 1, type: "White" },
        { row: 6, col: 3, type: "White" },
        { row: 6, col: 5, type: "White" },
        { row: 6, col: 7, type: "White" },
        { row: 7, col: 0, type: "White" },
        { row: 7, col: 2, type: "White" },
        { row: 7, col: 4, type: "White" },
        { row: 7, col: 6, type: "White" }
      ],
      connection_ready: false,
      connection_error: false,
      ably: null,
      ablyChannel: null,
      new_message: "",
      messages: [],
      selectedChecker: null,
      nickname: this.$route.query.nickname || "",
      currentTurn: "white",
      myColor: this.$route.query.myColor || null,
      gameOver: false,
      winnerName: "",
      rateInput: "",
      commentInput: "",
      firstPlayerWhite: "",
      secondPlayerBlack: "",
    };
  },
  methods: {
    getCheckerImage(type) {
      if (type === "Black") return require("@/assets/Black.png");
      if (type === "White") return require("@/assets/White.png");
      if (type === "BlackQueen") return require("@/assets/Black_Queen.png");
      if (type === "WhiteQueen") return require("@/assets/White_Queen.png");
    }
,
    selectChecker(row, col) {
      const selected = this.checkers.find(c => c.row === row && c.col === col);
      if (!selected || !selected.type.toLowerCase().includes(this.myColor)) {
        alert("This is not your checker!");
        return;
      }
      console.log("Checker selected", row, col);
      this.selectedChecker = { row, col };
    },
    init_chat() {
      if (!this.nickname) {
        this.nickname = "Player_" + Math.floor(Math.random() * 10000);
      }
      try {
        this.ably = new Ably.Realtime({
          key: 'Gcm66A.wu-rhA:rZ7OMih3kWt1XPyIXYTJG2cbEvc2uLMgw3CS2LdtwXw',
          clientId: this.nickname
        });
        this.ably.connection.once('connected', () => {
          this.loadBoard();
          console.log("✅ Ably connected as:", this.ably.auth.clientId);

          this.ablyChannel = this.ably.channels.get('game-room-' + this.roomId);

          this.ablyChannel.presence.get((err, members) => {
            if (err) {
              console.error("❌ [PRESENCE.GET] Error:", err);
              return;
            }

            const alreadyInRoom = members.filter(m => m.clientId !== this.ably.auth.clientId);
            if (!this.myColor) {
              this.myColor = alreadyInRoom.length === 0 ? "white" : "black";
              console.log("🎨 [COLOR INIT] Assigned:", this.myColor);
            }
          });

          this.ablyChannel.presence.enter({ nickname: this.nickname }, async () => {
            console.log("✅ [ENTER] Entered as:", this.nickname);
            this.Player1 = this.nickname;
            console.log(this.Player1 , "FIRST");
            await this.getAndSetMyColor();
          });

          this.ablyChannel.presence.subscribe('enter', (member) => {
            console.log("📥 [PRESENCE ENTER EVENT] Someone entered:", member.clientId);
            this.Player2= member.clientId;
            console.log(this.Player2 , "SECOND");
            this.getAndSetMyColor();
          });
          let blackPlayerName = this.Player1;
          let whitePlayerName = this.Player2
          console.log(whitePlayerName , "FIRST PLAYER");
          console.log(blackPlayerName , "SECOND PLAYER");

          this.ablyChannel.subscribe('chat-message', (msg) => {
            if (msg.data.from !== this.nickname) {
              this.messages.push({ from: "other", message: msg.data.message });
              this.scrollMessages();
            }
          });
          this.ablyChannel.subscribe('move', async () => {
            await this.loadBoard();
          });



          this.ablyChannel.subscribe("turn", (msg) => {
            if (msg.data.nextTurn !== null && msg.data.nextTurn !== undefined) {
              this.currentTurn = msg.data.nextTurn;
            } else {
              console.log("🎉 Game is OVER!!");
            }
          });
          this.ablyChannel.subscribe("game-over", (msg) => {
            if (msg.data.type === "win") {
              this.gameOver = true;
              if (msg.data.winner === "white") {
                this.winnerName = this.firstPlayerWhite;
              } else if (msg.data.winner === "black") {
                this.winnerName = this.secondPlayerBlack;
              } else {
                this.winnerName = msg.data.winner;
              }
              alert(`The race is over! winner: ${msg.data.winner}`);
            } else if (msg.data.type === "draw") {
              this.gameOver = true;
              this.winnerName = "draw";
              alert("The game ended in a draw!");
            }
          });
          this.connection_ready = true;
        });

      } catch (e) {
        console.error("Ably connection error", e);
        this.connection_error = true;
      }
    },

    getAndSetMyColor() {
      this.ablyChannel.presence.get((err, members) => {
        if (err) {
          console.error("❌ [PRESENCE.GET] Error:", err);
          return;
        }

        const sorted = members.sort((a, b) => a.timestamp - b.timestamp);
        const myMember = sorted.find(m => m.clientId === this.ably.auth.clientId);
        if (!myMember) return;

        const index = sorted.indexOf(myMember);
        this.myColor = index === 0 ? "white" : "black";
        console.log("🎨 Колір призначено:", this.myColor);
        console.log("👤 Гравець 1:", this.whitePlayerName);
        console.log("👤 Гравець 2:", this.blackPlayerName);
      });
    }
,
    send_message() {
      const to_send = { from: this.nickname, message: this.new_message };
      this.ablyChannel.publish('chat-message', to_send);
      this.messages.push({ from: "me", message: this.new_message });
      this.new_message = "";
      this.scrollMessages();
    },
    scrollMessages() {
      const messages_div = document.getElementById('messages');
      if (messages_div) {
        messages_div.scrollTo({ top: messages_div.scrollHeight, behavior: 'smooth' });
      }
    },
    async handleBoardClick(event) {
      console.log("Board clicked", this.selectedChecker);
      await this.loadBoard();
      if (!this.selectedChecker) return;
      if (this.gameOver || !this.currentTurn) {
        alert("Game is OVER!");
        return;
      }
      const boardElement = this.$el.querySelector(".board-img");
      const rect = boardElement.getBoundingClientRect();

      const x = event.clientX - rect.left - this.borderOffset;
      const y = event.clientY - rect.top - this.borderOffset;

      const row = Math.floor(y / this.cellSize);
      const col = Math.floor(x / this.cellSize);

      console.log("→ Calculated click at:", { row, col });
      console.log(this.currentTurn);
      console.log(this.myColor);
      console.log("MyColor:", this.myColor, "CurrentTurn:", this.currentTurn);

      if (this.currentTurn !== this.myColor) {
        alert("Wait for your turn!");
        return;
      }
      const move = {
        gameId: this.roomId,
        playerName: this.nickname,
        playerColor: this.myColor,
        move: {
          startX: this.selectedChecker.row,
          startY: this.selectedChecker.col,
          endX: row,
          endY: col
        }
      }
      console.log("Sending move:", {
        gameId: this.roomId,
        playerName: this.nickname,
        move: {
          startX: this.selectedChecker.row,
          startY: this.selectedChecker.col,
          endX: row,
          endY: col
        }
      });
      try {
        const response = await axios.post('http://localhost:7000/api/game/move', move);
        await this.loadBoard();
        const { valid, becameQueen, captured, error,nextTurn } = response.data;
        if (response.data.type === 'win') {
          console.log("🎉 the game is OVER!!");
          this.gameOver = true;
          if (response.data.winner === "white") {
            this.winnerName = this.firstPlayerWhite;
          } else if (response.data.winner === "black") {
            this.winnerName = this.secondPlayerBlack;
          }
          this.ablyChannel.publish('game-over', {
            type: 'win',
            winner: response.data.winner
          });
        }
        if (response.data.type === 'draw') {
          alert("The game ended in a draw!");
          this.gameOver = true;
          this.winnerName = "Draw";
          this.ablyChannel.publish('game-over', {
            type: 'draw'
          });
          return;
        }
        if (!valid) {
          alert(error || "Invalid move!");
          return;
        }
        this.ablyChannel.publish('move', {
          move: move.move,
          captured,
          becameQueen
        });
        this.ablyChannel.publish("turn", { nextTurn });
        const checkerIndex = this.checkers.findIndex(c =>
            c.row === this.selectedChecker.row && c.col === this.selectedChecker.col
        );
        if (checkerIndex !== -1) {
          this.checkers[checkerIndex].row = row;
          this.checkers[checkerIndex].col = col;

          if (becameQueen) {
            const color = this.checkers[checkerIndex].type.includes("Black") ? "BlackQueen" : "WhiteQueen";
            this.checkers[checkerIndex].type = color;
          }
        }
        if (captured && Array.isArray(captured)) {
          this.checkers = this.checkers.filter(c =>
              !(c.row === captured[0] && c.col === captured[1])
          );
        }

        await this.loadBoard();
        if (nextTurn === this.myColor && this.checkers.some(c => c.row === row && c.col === col && c.type.toLowerCase().includes(this.myColor))) {
          this.selectChecker(row, col);
        } else {
          this.selectedChecker = null;
        }
        const whiteCount = this.checkers.filter(c => c.type.includes("White")).length;
        const blackCount = this.checkers.filter(c => c.type.includes("Black")).length;

        if (whiteCount === 0 || blackCount === 0) {
          const winner = whiteCount === 0 ? "black" : "white";
          this.winnerName = winner === "white" ? this.firstPlayerWhite || "Player1" : this.secondPlayerBlack || "Player2";
          this.gameOver = true;
          alert(`The game is over! The winner:`,this.winnerName);
          this.ablyChannel.publish('game-over', {
            type: 'win',
            winner:this.winnerName
          });
          return;
        }

      } catch (error) {
        console.error("Move error:", error);
        alert("Move failed");
      }
    },
    async saveScore() {
      try {
        await axios.post('http://localhost:7000/api/scores/add', null, {
          params: {
            playerName:this.winnerName,
            type: "win"
          }
        });
      } catch (error) {
        console.error("❌ Помилка збереження рахунку:", error);
      }
    },

    async saveRate() {
      console.log("Saving with", this.winnerName, this.rateInput);
      try {
        await axios.post('http://localhost:7000/api/rate/add', {
          playerName: this.winnerName,
          rate: this.rateInput
        });
      } catch (error) {
        console.error("❌ Помилка збереження оцінки:", error);
      }
    },

    async saveComment() {
      console.log("Saving with", this.winnerName, this.rateInput);
      try {
        await axios.post('http://localhost:7000/api/comment/addcomment', {
          playerName: this.winnerName,
          comment: this.commentInput
        });
      } catch (error) {
        console.error("❌ Помилка збереження коментаря:", error);
      }
    },

    async resetGame() {
      try {
        await axios.get('http://localhost:7000/api/game/reset', {
          params: { gameId: this.roomId }
        });
        await this.loadBoard();
        this.selectedChecker = null;
        this.gameOver = false;
        this.winnerName = "";
      } catch (error) {
        console.error("❌ Не вдалося перезапустити гру:", error);
        alert("Error when restarting the game.");
      }
    },
    async saveResult() {
      if (this.rateInput === "" || this.commentInput.trim() === "") {
        alert("Please fill in all fields!");
        return;
      }

      const rate = parseInt(this.rateInput);
      if (isNaN(rate) || rate < 1 || rate > 5) {
        alert("The score should be from 1 to 5!");
        return;
      }

      try {
        await this.saveScore();
        await this.saveRate();
        await this.saveComment();

        alert("✅ All data is successfully saved!");
        this.rateInput = "";
        this.commentInput = "";
      } catch (e) {
        console.error("❌ Помилка при збереженні результату:", e);
      }
    },
    async goHome() {
      try {
        await axios.post('http://localhost:7000/api/roomservice/deleteroom', null, {
          params: {
            roomId: this.roomId
          }
        });
      } catch (error) {
        console.error("❌ Помилка видалення кімнати:", error);
      }
      this.$router.push('/');
    },
    async loadBoard() {
      try {
        const response = await axios.get('http://localhost:7000/api/game/state', {
          params: { gameId: this.roomId }
        });
        this.checkers = response.data.checkers;
        this.currentTurn = response.data.turn;
      } catch (error) {
        console.error("❌ Помилка завантаження стану гри:", error);
        alert("Не вдалося завантажити стан гри.");
      }
    },
    debugOpenEndPanel() {
      this.gameOver = true;
      this.winnerName =this.firstPlayerWhite;
    },  async rewriteUsersNames() {
      try {
        const response = await axios.get(`http://localhost:7000/api/users/findnicknamesbyroomid`, {
          params: {
            roomId: this.roomId
          }
        });
        if (response.data && response.data.length > 0) {
          this.firstPlayerWhite = response.data[0][0];
          this.secondPlayerBlack = response.data[0][1];
          console.log(this.firstPlayerWhite ,"Присвоєння імені ДЕБАГ");
          console.log(this.secondPlayerBlack,"Присвоєння імені ДЕБАГ");
        }
      } catch (error) {
        console.error("❌ Не вдалося отримати імена гравців:", error);
      }
    }
  },

  mounted() {
    this.init_chat();
    this.rewriteUsersNames();
  }
};
</script>

<style scoped>
body {
  margin: 0;
  padding: 0;
}

.main-container {
  display: flex;
  width: 100vw;
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  background-image: url('@/assets/BK2.jpg');
  background-size: cover;
  background-position: center;
  flex-direction: row;
  overflow: hidden;
}

.board-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  color: white;
  flex: 1;
  padding: 20px;
}

.Chat-container {
  width: 350px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 10px;
  box-sizing: border-box;
}

.Chat-container h1 {
  padding: 0px;
  height: 30px;
  color: white;
  font-size: 20px;
  text-transform: uppercase;
  margin-top: 5px;
  margin-bottom: 10px;
}

.Chat-container .connection_ready {
  color: greenyellow;
}

.messages {
  flex: 1;
  overflow-y: auto;
  background: url(@/assets/ChatBackground.jpg) no-repeat center;
  background-size: cover;
  border-radius: 5px;
  margin-bottom: 10px;
}

.message-container {
  padding: 10px;
}

.msg-from-me {
  border-radius: 7.5px;
  max-width: 65%;
  font-size: 16px;
  line-height: 19px;
  color: #e9edef;
  background-color: #2a3942;
  padding: 8px;
  margin: 20px 20px 5px 0px;
  float: right;
}

.msg-from-other {
  border-radius: 7.5px;
  max-width: 65%;
  font-size: 16px;
  line-height: 19px;
  color: #e9edef;
  background-color: fuchsia;
  padding: 8px;
  margin: 20px 0px 5px 20px;
  float: left;
}

.send-zone {
  padding: 10px 0;
}

.send-zone input {
  width: 100%;
  padding: 10px;
  border-radius: 20px;
  border: none;
  background-color: #2a3942;
  color: white;
  outline: none;
}

.room-title {
  font-size: 36px;
  font-family: Impact, Fantasy, sans-serif;
  margin-top: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.7);
}

.room-text {
  font-size: 18px;
  font-family: Impact, Fantasy, sans-serif;
  margin-bottom: 20px;
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
}
.end-panel {
  margin-top: 20px;
  padding: 20px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: 10px;
  text-align: center;
}

.save-button, .home-button {
  margin-top: 10px;
  padding: 10px 20px;
  font-size: 16px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
}

.save-button {
  background-color: limegreen;
  color: white;
}

.home-button {
  background-color: darkred;
  color: white;
}
.debug-button {
  position: absolute;
  bottom: 20px;
  right: 20px;
  background-color: orange;
  color: black;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  padding: 10px 15px;
  cursor: pointer;
  z-index: 1000;
}
/* Кнопки */
button.save-button,
button.home-button,
button.play-again-button {
  padding: 12px 24px;
  font-size: 16px;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: bold;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
}

.save-button:hover {
  background-color: #28a745;
}

.home-button:hover {
  background-color: #aa0000;
}

.play-again-button {
  background-color: #007bff;
  color: white;
}

.play-again-button:hover {
  background-color: #0056b3;
}

.rate-input,
.comment-input {
  padding: 10px;
  border-radius: 10px;
  border: 2px solid #ccc;
  font-size: 16px;
  width: 80%;
  margin: 8px 0;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.2);
  outline: none;
}

.rate-input:focus,
.comment-input:focus {
  border-color: #007bff;
}
@keyframes floatUp {
  0% {
    transform: translateY(10px);
    opacity: 0;
  }
  50% {
    transform: translateY(-5px);
    opacity: 1;
  }
  100% {
    transform: translateY(0);
  }
}

@keyframes shake {
  0% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  50% { transform: translateX(5px); }
  75% { transform: translateX(-3px); }
  100% { transform: translateX(0); }
}

.win-anim {
  animation: floatUp 1s ease-out;
  background: linear-gradient(135deg, #4CAF50, #81C784);
  border: 2px solid #ffffff99;
  box-shadow: 0 0 20px lime;
}

.lose-anim {
  animation: shake 0.6s ease-in-out;
  background: linear-gradient(135deg, #b71c1c, #e57373);
  border: 2px solid #ff8a8088;
  box-shadow: 0 0 20px red;
}
.rotated {
  transform: rotate(180deg);
}

.rotated .checker {
  transform: rotate(180deg);
}

</style>