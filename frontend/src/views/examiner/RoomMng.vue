<template>
  <div class="uk-admin">
    <app-bar active="room"></app-bar>

    <div class="uk-container">
      <div class="uk-page-head">
        <div>
          <h1 class="uk-page-title">내 퀴즈방</h1>
          <p class="uk-page-sub">퀴즈방을 만들고 문제를 담은 뒤 시작해 보세요.</p>
        </div>
        <div class="uk-actions">
          <button class="uk-btn light" @click="goProbBank()">📚 문제은행</button>
          <button class="uk-btn" v-b-modal.modal-room-create>＋ 새 퀴즈방</button>
        </div>
      </div>

      <b-modal
        id="modal-room-create"
        ref="modal"
        title="새 퀴즈방 만들기"
        ok-title="만들기"
        cancel-title="취소"
        centered
        @show="resetModal"
        @hidden="resetModal"
        @ok="handleOk"
      >
        <form ref="form" @submit.stop.prevent="handleSubmit">
          <b-form-group
            label="퀴즈방 이름"
            label-for="name-input"
            invalid-feedback="이름을 입력하세요"
            :state="nameState"
          >
            <b-form-input
              id="name-input"
              v-model="name"
              :state="nameState"
              placeholder="예) 3학년 2반 역사 퀴즈"
              maxlength="40"
              required
              autofocus
            ></b-form-input>
          </b-form-group>
        </form>
      </b-modal>

      <div v-if="loaded && roomData.length === 0" class="uk-card uk-empty">
        <div class="icon">🎲</div>
        <h3 class="uk-card-title">아직 퀴즈방이 없어요</h3>
        <p class="uk-card-sub">첫 번째 퀴즈방을 만들어 보세요!</p>
        <button class="uk-btn" v-b-modal.modal-room-create>＋ 새 퀴즈방</button>
      </div>

      <div v-else class="room-grid">
        <div v-for="(room, idx) in roomData" :key="room.roomId" class="room-card" :class="{ live: room.roomState === 'PLAY' }">
          <div class="room-cover" :class="'c' + (idx % 4)">
            <span v-if="room.roomState === 'PLAY'" class="uk-badge live">진행 중 · {{ room.roomNumber }}</span>
            <span v-else-if="room.roomState === 'END'" class="uk-badge">종료됨</span>
            <span v-else class="uk-badge">대기</span>
            <span class="cover-emoji">{{ emojis[idx % emojis.length] }}</span>
          </div>
          <div class="room-body">
            <h3 class="room-title" :title="room.roomTitle">{{ room.roomTitle }}</h3>
            <p class="room-date">{{ formatDate(room.createdDate) }} 생성</p>
            <div class="room-actions">
              <button v-if="room.roomState === 'PLAY'" class="uk-btn accent sm grow" @click="resumeGroup(room)">▶ 이어하기</button>
              <button v-else class="uk-btn success sm grow" @click="startGroup(room.roomId, room.roomTitle)">▶ 시작</button>
              <button class="uk-btn light sm" title="문제 편집" @click="modifyGroup(room.roomId, room.roomTitle)">✏️</button>
              <button class="uk-btn light sm" title="삭제" @click="deleteGroup(room.roomId)">🗑</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { errorMessage } from "@/utils/gameSession";
import AppBar from "@/components/layout/AppBar.vue";

export default {
  components: { AppBar },
  data() {
    return {
      roomData: [],
      loaded: false,
      emojis: ["🧠", "🚀", "🎯", "📚", "🌍", "🔬", "🎨", "⚡"],
      roomTitle: "",
      name: "",
      nameState: null,
      submittedNames: [],
    };
  },
  created() {
    document.oncontextmenu = function(){};
    document.onkeydown = function() {};
  },
  mounted() {
    this.getRoomData();
  },
  methods: {
    getRoomData() {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      let promise = this.$http.get("/api/v1/room/me", config);
      promise.then((res) => {
        this.roomData = res.data.result || [];
        this.loaded = true;
      }).catch((error) => {
        this.loaded = true;
        if (error.response && error.response.status === 401) {
          alert("로그인이 필요합니다.");
          this.$router.replace({ name: "Login" });
          return;
        }
        console.log(error);
        alert(errorMessage(error, "방 목록을 불러오지 못했습니다."));
      });
    },
    formatDate(value) {
      if (!value) return "";
      const d = new Date(value);
      if (isNaN(d.getTime())) return "";
      return d.getFullYear() + "." + String(d.getMonth() + 1).padStart(2, "0") + "." + String(d.getDate()).padStart(2, "0");
    },
    goProbBank() {
      this.$router.push({ name: "ProbBank" });
    },
    modifyGroup(roomId, roomTitle) {
      this.$router.push({
        name: "ProbReg",
        params: {
          roomId: roomId,
          roomTitle: roomTitle,
          newRoomYn: "N",
        },
      });
    },
    deleteGroup(roomId) {
      if (confirm("퀴즈방을 삭제하시겠습니까?\n진행 중인 게임이 있다면 함께 종료됩니다.") == false) {
        return;
      }
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      this.$http.delete("/api/v1/room/" + roomId, config).then((res) => {
        
        alert("삭제 되었습니다");
        this.getRoomData();
      }).catch((error) => {
        console.log(error);
        alert(errorMessage(error, "방 삭제 중 오류가 발생했습니다."));
      });
    },
    startGroup(roomId, roomTitle) {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };

      this.$http
        .put("/api/v1/room/state", {
          "id": roomId,
          "state": "PLAY"
        }, config)
        .then((res) => {
          if (res.data.code !== 0) {
            alert(res.data.message);
          } else {
            this.$router.push({ name: "RoomProc", params: { identification: roomId, number: res.data.result.roomNumber, examiner: sessionStorage.getItem("userId")} });
          }
        })
        .catch((error) => {
          console.log(error);
          alert(errorMessage(error, "방 시작 중 오류가 발생했습니다."));
        });
    },
    // 진행 중인 방에 출제자로 다시 들어가기 (연결 끊김/브라우저 종료 후 재접속)
    resumeGroup(room) {
      this.$router.push({
        name: "RoomProc",
        params: { identification: room.roomId, number: room.roomNumber, examiner: sessionStorage.getItem("userId") },
      });
    },
    checkFormValidity() {
      const valid = this.$refs.form.checkValidity();
      this.nameState = valid;
      return valid;
    },
    resetModal() {
      this.name = "";
      this.nameState = null;
    },
    handleOk(bvModalEvt) {
      // Prevent modal from closing
      bvModalEvt.preventDefault();
      // Trigger submit handler
      this.handleSubmit();
    },
    handleSubmit() {
      // Exit when the form isn't valid
      if (!this.checkFormValidity()) {
        return;
      }
      // Push the name to submitted names
      //this.submittedNames.push(this.name)
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      this.$http
        .post(
          "/api/v1/room",
          {
            title: this.name,
          },
          config
        )
        .then((res) => {
          // Hide the modal manually
          this.$nextTick(() => {
            this.$bvModal.hide("modal-room-create");
          });
          this.$router.push({
            name: "ProbReg",
            params: {
              roomId: res.data.result.roomId,
              roomTitle: res.data.result.roomTitle,
              newRoomYn: "Y",
            },
          });
        });
    },
  },
};
</script>

<style>
</style>

<style scoped>
.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
.room-card {
  background: #fff;
  border-radius: var(--uk-radius);
  box-shadow: var(--uk-shadow);
  overflow: hidden;
  transition: transform 0.15s, box-shadow 0.15s;
  border: 2px solid transparent;
}
.room-card:hover { transform: translateY(-4px); box-shadow: 0 16px 40px rgba(39, 16, 105, 0.16); }
.room-card.live { border-color: #ffb3c1; }
.room-cover {
  height: 120px;
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 12px 14px;
}
.room-cover.c0 { background: linear-gradient(135deg, #6c3cf0, #a855f7); }
.room-cover.c1 { background: linear-gradient(135deg, #3d8bff, #22d3ee); }
.room-cover.c2 { background: linear-gradient(135deg, #ff5470, #ffa41b); }
.room-cover.c3 { background: linear-gradient(135deg, #22b573, #84cc16); }
.room-cover .uk-badge { background: rgba(255, 255, 255, 0.92); }
.room-cover .uk-badge.live { background: #fff; }
.cover-emoji { position: absolute; right: 14px; bottom: -6px; font-size: 64px; filter: drop-shadow(0 6px 10px rgba(0, 0, 0, 0.18)); }
.room-body { padding: 18px 18px 20px; }
.room-title { font-size: 19px; font-weight: 800; margin: 0 0 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.room-date { color: var(--uk-text-sub); font-size: 13px; margin: 0 0 16px; }
.room-actions { display: flex; gap: 8px; }
.room-actions .grow { flex: 1; }
.uk-empty .uk-btn { margin-top: 8px; }
</style>
