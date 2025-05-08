<template>
  <div class="home">
    <h1>Vitajte v programe <span class="title">Checkers 2.0</span></h1>
    <h3 class="rating">⭐ Average rating: {{ avgRate }}</h3>
    <div class="rules-box">
      <h2>Game Rules</h2>
      <p><strong>1. Game Setup</strong><br>
        * The game is played on an 8×8 board with alternating light and dark squares.<br>
        * Each player starts with 12 pieces placed on the dark squares of the first three rows closest to them.<br>
        * White moves first.</p>

      <p><strong>2. Movement Rules</strong><br>
        * Regular pieces (men) move diagonally forward one square.<br>
        * Kings (damka) move diagonally any number of squares in any direction.</p>

      <p><strong>3. Capturing Rules</strong><br>
        * If an opponent's piece is adjacent and the square behind it (diagonally) is empty, you must jump over and capture it.<br>
        * Multiple jumps are allowed in one turn.<br>
        * Regular pieces can jump backward, even though they cannot move backward normally.<br>
        * Kings can capture over any number of squares, as long as the landing square is empty.</p>

      <p><strong>4. Promotion</strong><br>
        * When a regular piece reaches the last row, it becomes a king (damka).</p>

      <p><strong>5. Forced Captures</strong><br>
        * If a capture is available, it must be taken.<br>
        * If multiple capture options exist, the move capturing the most pieces must be chosen.</p>

      <p><strong>6. Winning the Game</strong><br>
        * A player wins if their opponent has no legal moves left, either by capturing all pieces or blocking them completely.<br>
        * The game can end in a draw if neither player can force a win.</p>
    </div>
    <div class="buttons">
      <router-link to="/game-with-bot">
      <button class="cta"><span>Play with bot</span></button>
      </router-link>

      <router-link to="/game-with-friends">
        <button class="cta"><span>Play with friends</span></button>
      </router-link>

      <router-link to="/comments123">
      <button class="cta"><span>Comments</span></button>
      </router-link>

      <router-link to="/leaderboard">
        <button class="cta"><span>LeaderBoard</span></button>
      </router-link>
    </div>
    <div class="footer">Project suported by RS</div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
export default {
  name: 'HomePage',
  setup() {
    const avgRate = ref(0);

    onMounted(async () => {
      try {
        const response = await axios.get('http://localhost:7000/api/rate/average');
        avgRate.value = response.data.toFixed(2); // округлюємо до 2 знаків
      } catch (error) {
        console.error("Помилка завантаження середнього рейтингу:", error);
      }
    });

    return {
      avgRate
    };
  },
  methods: {
      GameWithBot() {
        this.$router.push({ name: 'gameWithBot' });
      },
      GameWithFriend() {
        this.$router.push({ name: 'gameWithFriend' });
      },
      Comment(){
        this.$router.push({ name: 'comments123' });
      },
      LeaderBoard(){
        this.$router.push({ name: 'leaderboard' });
      },
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css?family=Poppins:900i');

* {
  box-sizing: border-box;
}

body {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.home {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  color: white;
  text-align: center;
  position: relative;
}

h1 {
  margin-top: 20px;
  font-family: 'DejaVu Sans Mono', monospace;
  font-size: 3em;
  text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.7);
}

.rules-box {
  position: absolute;
  left: 20px;
  top: 160px;
  width: 700px;
  background: rgba(0, 0, 0, 0.6);
  padding: 15px;
  border-radius: 10px;
  text-align: left;
  font-size: 20px;
}

.buttons {
  margin-top: 50px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.cta {
  display: flex;
  padding: 10px 45px;
  text-decoration: none;
  font-family: 'Poppins', sans-serif;
  font-size: 40px;
  color: white;
  background: rgba(255, 0, 0, 0.7);
  transition: 1s;
  box-shadow: 6px 6px 0 black;
  transform: skewX(-15deg);
}

.cta:hover {
  transition: 0.5s;
  box-shadow: 10px 10px 0 #FBC638;
}

.footer {
  position: absolute;
  bottom: 20px;
  left: 20px;
  font-size: 28px;
  color: rgba(255, 255, 255, 0.7);
}
.rating {
  margin-top: 10px;
  font-size: 28px;
  color: gold;
  text-shadow: 1px 1px 3px black;
}
</style>
