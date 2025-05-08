<template>
  <div class="container">
    <div class="comment-board">
      <h2>💬 Коментарі</h2>
      <table>
        <thead>
        <tr>
          <th>Гравець</th>
          <th>Коментар</th>
          <th>Час</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(comment, index) in comments" :key="index">
          <td>{{ comment.playerName }}</td>
          <td>{{ comment.comment }}</td>
          <td>{{ new Date(comment.data).toLocaleString() }}</td>
        </tr>
        </tbody>
      </table>
      <button class="home-button" @click="goHome">🏠 Go Home</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const comments = ref([])
const router = useRouter()

function goHome() {
  router.push('/')
}

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:7000/api/comment/getallcomment')
    comments.value = response.data
  } catch (error) {
    console.error('eror', error)
  }
})
</script>

<style scoped>
.container {
  background-image: url('@/assets/Back2.png');
  background-size: cover;
  background-position: center;
  height: 100vh;
  padding: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.comment-board {
  background: rgba(0, 0, 0, 0.6);
  padding: 2rem;
  border-radius: 20px;
  color: #fff;
  max-width: 800px;
  width: 100%;
  text-align: center;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.2);
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
  margin-top: 2rem;
  padding: 0.75rem 1.5rem;
  font-size: 1.1rem;
  color: white;
  background-color: #0077ff;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.home-button:hover {
  background-color: #005edb;
}
</style>
