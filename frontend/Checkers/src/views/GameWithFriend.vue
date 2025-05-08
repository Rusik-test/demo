<template>
  <div class="GameWIthFriend">
    <h2>Play with Friends</h2>
    <input v-model="nickname" placeholder="Enter your nickname" class="nickname-input" />
    <input v-model="roomId" placeholder="Enter Room ID" class="room-input" />
    <button @click="joinRoom" class="action-button">Join Room</button>
    <button @click="createRoom" class="action-button">Create Room</button>
    <button @click="goHome" class="action-button back-button">Back to Home</button>
  </div>
</template>

<script>
import axios from "axios";
export default {
  data() {
    return {
      roomId: '',
      nickname: '',
    };
  },
  methods: {
    async joinRoom() {
      if (!/^\d+$/.test(this.roomId)) {
        alert("Room ID should contain only numbers");
        return;
      }
      if (this.roomId.length !== 6) {
        alert("Room ID must be exactly 6 digits long");
        return;
      }
      if (!this.nickname || this.nickname.trim() === "") {
        alert("Nickname is required.");
        return;
      }

      try {
        const response = await axios.get('http://localhost:7000/api/rooms/idRooms');
        const roomsData = response.data;
        const exists = roomsData.some(room => room.id.toString() === this.roomId);

        if (!exists) {
          alert("Room ID does not exist");
          return;
        }

        await fetch(`http://localhost:7000/api/users/writesecondplayer?roomId=${this.roomId}&secondNick=${this.nickname}`, {
          method: 'POST'
        });

        this.$router.push({
          path: `/game-room/${this.roomId}`,
          query: {
            nickname: this.nickname,
            myColor: 'black' // 🎯
          }
        });

      } catch (err) {
        alert("Some Problems");
        console.error(err);
      }
    }
,
    createRoom() {
      if (!this.nickname || this.nickname.trim() === "") {
        alert("Nickname is required.");
        return;
      }

      const requestOptions = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nickname: this.nickname.trim(),
        }),
      };

      fetch('http://localhost:7000/api/rooms/create', requestOptions)
          .then(response => {
            if (!response.ok) {
              console.error("Error response:", response);
              throw new Error('Network response was not ok');
            }
            return response.json();
          })
          .then(data => {
            if (data.id) {
              fetch(`http://localhost:7000/api/users/writefirstnick?roomId=${data.id}&firstNick=${this.nickname}&secondNick=1`, {
                method: 'POST'
              });

              this.$router.push({
                path: `/game-room/${data.id}`,
                query: {
                  nickname: this.nickname,
                  myColor: 'white' // 🎯
                }
              });
            } else {
              alert("Error: Room ID not found.");
            }
          })
          .catch(error => {
            console.error("Error:", error);
            alert("Failed to create room.");
          });
    }
,
    goHome() {
      this.$router.push('/');
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap');

.GameWIthFriend {
  background-image: url('@/assets/Back3.png');
  background-size: cover;
  background-position: center;
  height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  text-align: center;
  font-family: 'Press Start 2P', cursive;
  overflow: hidden;
}

h2 {
  font-size: 3em;
  text-transform: uppercase;
  letter-spacing: 5px;
  margin-bottom: 20px;
  text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.7);
}

.nickname-input,
.room-input {
  padding: 15px;
  font-size: 20px;
  width: 80%;
  max-width: 400px;
  margin-bottom: 20px;
  border: none;
  border-radius: 10px;
  background: rgba(50, 50, 50, 0.8);
  color: white;
  text-align: center;
  outline: none;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
}

.nickname-input:focus,
.room-input:focus {
  background: rgba(80, 80, 80, 0.9);
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.9);
}

.nickname-input::placeholder,
.room-input::placeholder {
  color: rgba(200, 200, 200, 0.7);
  font-style: italic;
}

.action-button {
  padding: 15px;
  width: 250px;
  font-size: 20px;
  font-weight: bold;
  border: none;
  border-radius: 50px;
  text-transform: uppercase;
  letter-spacing: 2px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 10px rgba(0, 255, 0, 0.7);
  margin-top: 10px;
}

.action-button:hover {
  box-shadow: 0 0 20px rgba(0, 255, 0, 0.9);
  transform: scale(1.1);
}

.action-button:active {
  transform: scale(0.95);
}

.back-button {
  background: rgba(255, 0, 0, 0.7);
  box-shadow: 0 0 10px rgba(255, 0, 0, 0.7);
  margin-top: 20px;
}

.back-button:hover {
  background: rgba(255, 0, 0, 1);
  box-shadow: 0 0 20px rgba(255, 0, 0, 0.9);
}
</style>

