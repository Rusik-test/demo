<template>
  <div class="home">
    <GameBoard msg="Welcome to Your Vue.js App"/>
  </div>
</template>

<script>

import GameBoard from '@/components/GameBoard.vue';
export default {
  name: 'HomeView',
  components: {
    GameBoard
  }
}
</script>
