<template>
  <div class="board-container">


    <div class="board">
      <img src="@/assets/Board.png" alt="Game Board" class="board-img" />
    </div>

  </div>
</template>

<script>
export default {
  props: {
    roomId: String,
  },
};
</script>

<style scoped>
.board-container {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.board-border {
  width: 500px;
  display: flex;
  justify-content: center;
}

.border-img {
  width: 100%;
  height: 20px;
}


.board {
  position: relative;
  width: 800px;
  height: 800px;
  margin: 0 auto;
}
.board-container {
  background-image: url('@/assets/BK2.jpg');
  background-size: cover;
  background-position: center;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  color: white;
  text-align: center;
}

.board-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>