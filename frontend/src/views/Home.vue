<template>
  <div class="uk-game">
    <div class="uk-center">
      <button class="uk-btn glass sm home-login" @click="examiner()">출제자 로그인</button>

      <h1 class="uk-logo">Uknowhat<span>?</span></h1>
      <p class="uk-tagline">친구들과 함께 실시간으로 퀴즈를 풀어보세요</p>

      <div class="uk-card uk-card-narrow">
        <transition name="step" mode="out-in">
          <!-- 1단계: 방 번호 -->
          <form v-if="typeRoomNumber" key="room" @submit.prevent="enter()">
            <h2 class="uk-card-title">방 번호 입력</h2>
            <p class="uk-card-sub">출제자 화면에 표시된 4자리 번호를 입력하세요</p>
            <input
              ref="roomInput"
              v-model="roomNum"
              class="uk-input lg"
              inputmode="numeric"
              maxlength="4"
              placeholder="0000"
              autocomplete="off"
              @input="roomNum = roomNum.replace(/[^0-9]/g, '')"
            />
            <button class="uk-btn block lg mt-3" type="submit" :disabled="loading || roomNum.length !== 4">
              {{ loading ? "확인 중..." : "입장하기" }}
            </button>
          </form>

          <!-- 2단계: 닉네임 -->
          <form v-else key="nick" @submit.prevent="enter()">
            <div class="room-tag">
              <span class="uk-badge">{{ roomNum }}번 방</span>
              <span v-if="roomTitle" class="room-title">{{ roomTitle }}</span>
            </div>
            <h2 class="uk-card-title">닉네임을 정해주세요</h2>
            <p class="uk-card-sub">화면에 표시될 이름이에요 (최대 10자)</p>
            <input
              ref="nickInput"
              v-model="participantName"
              class="uk-input lg"
              maxlength="10"
              placeholder="닉네임"
              autocomplete="off"
            />
            <button class="uk-btn block lg accent mt-3" type="submit" :disabled="loading || !participantName.trim()">
              {{ loading ? "입장 중..." : "시작하기" }}
            </button>
            <a class="uk-link d-inline-block mt-3" @click="back()">← 방 번호 다시 입력</a>
          </form>
        </transition>
      </div>

      <!-- 이어서 참여 -->
      <div v-if="resume" class="uk-glass resume">
        <div>
          <strong>{{ resume.roomNum }}번 방</strong>에 <strong>{{ resume.nickname }}</strong>(으)로 참여 중이었어요
        </div>
        <div class="resume-actions">
          <button class="uk-btn accent sm" @click="continueResume()">이어서 참여</button>
          <button class="uk-btn glass sm" @click="dismissResume()">닫기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { createToken, saveParticipant, loadParticipant, clearParticipant, errorMessage } from "@/utils/gameSession";

export default {
  name: "Home",
  data() {
    return {
      roomNum: "",
      roomTitle: "",
      participantName: "",
      typeRoomNumber: true,
      loading: false,
      resume: null,
    };
  },
  created() {
    history.pushState(null, document.title, location.href);
    window.addEventListener('popstate', function(e) {
      history.pushState(null, document.title, location.href);
    });

    document.oncontextmenu = function(){};
    document.onkeydown = function() {};

    this.checkResume();
  },
  mounted() {
    this.focus("roomInput");
  },
  methods: {
    focus(ref) {
      this.$nextTick(() => this.$refs[ref] && this.$refs[ref].focus());
    },
    examiner() {
      this.$router.push({ name: 'Login' });
    },
    back() {
      this.typeRoomNumber = true;
      this.participantName = "";
      this.focus("roomInput");
    },
    // 이전에 참여 중이던 게임이 아직 진행 중인지 확인
    checkResume() {
      const saved = loadParticipant();
      if (!saved || !saved.roomNum || !saved.nickname || !saved.token) return;

      this.$http
        .get("/api/v1/room/check/nickname", {
          params: { number: saved.roomNum, nickname: saved.nickname, token: saved.token },
        })
        .then((res) => {
          if (res.data.code === 0 && res.data.result && res.data.result.rejoin) {
            this.resume = saved;
          } else {
            clearParticipant();
          }
        })
        .catch(() => {});
    },
    continueResume() {
      this.$router.push({
        name: "PlayMain",
        params: { number: this.resume.roomNum, nickname: this.resume.nickname },
      });
    },
    dismissResume() {
      clearParticipant();
      this.resume = null;
    },
    enter() {
      if (this.loading) return;

      if (this.typeRoomNumber) {
        if (this.roomNum.length !== 4) {
          alert("방 번호 4자리를 입력하세요.");
          return;
        }

        this.loading = true;
        this.$http
          .get("/api/v1/room/check/number/" + this.roomNum)
          .then((res) => {
            if (res.data.code === 0) {
              this.roomTitle = res.data.result ? res.data.result.title : "";
              this.typeRoomNumber = false;
              this.focus("nickInput");
            } else {
              alert(res.data.message || "방을 찾을 수 없습니다.");
            }
          })
          .catch((error) => {
            console.log(error);
            alert(errorMessage(error, "서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));
          })
          .finally(() => { this.loading = false; });
      } else {
        const nickname = this.participantName.trim();
        if (nickname === "") {
          alert("닉네임을 입력하세요.");
          return;
        } else if (nickname.length > 10) {
          alert("10자리 이내의 닉네임을 입력하세요.");
          return;
        }

        const saved = loadParticipant();
        const token = (saved && saved.roomNum === this.roomNum && saved.nickname === nickname && saved.token)
          ? saved.token : createToken();

        this.loading = true;
        this.$http
          .get("/api/v1/room/check/nickname", {
            params: { number: this.roomNum, nickname: nickname, token: token },
          })
          .then((res) => {
            if (res.data.code === 0) {
              saveParticipant({ roomNum: this.roomNum, nickname: nickname, token: token });
              this.$router.push({
                name: "PlayMain",
                params: { number: this.roomNum, nickname: nickname },
              });
            } else {
              alert(res.data.message);
            }
          })
          .catch((error) => {
            console.log(error);
            alert(errorMessage(error, "서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));
          })
          .finally(() => { this.loading = false; });
      }
    },
  },
};
</script>

<style scoped>
.home-login {
  position: absolute;
  top: 20px;
  right: 20px;
}
.room-tag {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 14px;
}
.room-title {
  font-weight: 700;
  color: var(--uk-text-sub);
  font-size: 14px;
}
.resume {
  margin-top: 20px;
  padding: 16px 20px;
  max-width: 400px;
  width: 100%;
  font-size: 15px;
}
.resume-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 12px;
}
.step-enter-active, .step-leave-active { transition: all 0.2s; }
.step-enter { opacity: 0; transform: translateX(20px); }
.step-leave-to { opacity: 0; transform: translateX(-20px); }
</style>
