<template>
  <div :class="bgClass">
    <router-view />
  </div>
</template>

<script>
export default {
  computed: {
    bgClass() {
      return this.$route.path === "/PlayWithFriend" ? "play-bg" : "default-bg";
    },
  },
};
</script>

<style>

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}


.default-bg {
  background: url("@/assets/background.jpg") no-repeat center center fixed;
  background-size: cover;
  width: 100vw;
  height: 100vh;
}

.play-bg {
  background: url("@/assets/backround2.png") no-repeat center center fixed;
  background-size: cover;
  width: 100vw;
  height: 100vh;
}
</style>
