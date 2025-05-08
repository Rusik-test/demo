import { createRouter, createWebHistory } from 'vue-router';
import GameWithFriends from '@/views/GameWithFriend.vue';
import HomePage from '@/views/HomePage.vue';
import GameRoom from "@/views/GameRoom.vue";
import GameWithBot from "@/views/GameWithBot.vue";
import CommentBoard from "@/views/CommentBoard.vue";
import LeaderBoard from "@/views/LeaderBoard.vue";

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePage,
  },
  {
    path: '/comments123',
    name: 'comments123',
    component: CommentBoard,
  },
  {
    path: '/leaderboard',
    name : 'leaderboard',
    component: LeaderBoard,
  },
  {
    path: '/game-with-friends',
    name: 'gameWithFriends',
    component: GameWithFriends,
  },
  {
    path: '/game-with-bot',
    name: 'gameWithBot',
    component: GameWithBot,
  },
  {
    path: '/game-room/:roomId',
    name: 'gameRoom',
    component: GameRoom,
  },
];

export default createRouter({
  history: createWebHistory(),
  routes,
});
