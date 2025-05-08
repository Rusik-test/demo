<template>
  <div class="container">
    <div class="leaderBoard">
      <h2>🏆 Leaderboard - Wins</h2>
      <table>
        <thead>
        <tr>
          <th>Place</th>
          <th>Player</th>
          <th>Wins</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(result, index) in sortedResults" :key="index">
          <td>{{ index + 1 }}</td>
          <td>{{ result.player }}</td>
          <td>{{ result.wins }}</td>
        </tr>
        </tbody>
      </table>
      <button class="home-button" @click="goHome">🏠 Go Home</button>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";

const leaderResult = ref([]);

const router = useRouter();

function goHome() {
  router.push('/');
}

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:7000/api/scores/leaderboard');
    leaderResult.value = response.data.map(([player, wins]) => ({
      player,
      wins
    }));
  } catch (err) {
    console.error(err, "Проблема з лідербордом");
  }
});

const sortedResults = computed(() => {
  return [...leaderResult.value].sort((a, b) => b.wins - a.wins);
});
</script>

<style scoped>
.container {
  background-image: url('@/assets/LastGamesBack.png');
  background-size: cover;
  background-position: center;
  height: 100vh;
  padding: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
}
.leaderBoard {
  background: rgba(0, 0, 0, 0.6);
  padding: 2rem;
  border-radius: 20px;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.2);
  color: #fff;
  max-width: 800px;
  width: 100%;
  text-align: center;
}
h2 {
  margin-bottom: 1rem;
  font-size: 2rem;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

th, td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

th {
  background-color: rgba(255, 255, 255, 0.1);
  font-weight: bold;
}

tr:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transition: 0.3s;
}
.home-button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: none;
  cursor: pointer;
  font-size: 18px;
  transition: 0.3s;
}

.home-button:hover {
  background-color: #005edb;
}
</style>
