<template>
  <!-- 참가자 메인 -->
  <div class="h-100">
    <b-overlay :show="show" rounded="sm" class="h-100">
      <b-jumbotron
        class="h-100"
        bg-variant="dark"
        text-variant="white"
        border-variant="dark"
      >
        <template #header
          >Play <br />
          Uknowhat?</template
        >
        <hr class="my-4" />
        <b-form>
          <b-form-group
            id="input-group-1"
            v-if="typeRoomNumber"
            :invalid-feedback="invalidRoomNumberFeedback"
          >
            <b-form-input
              id="input-1"
              v-model="roomNum"
              type="text"
              placeholder="Room Number"
              :state="roomNumberState"
              required
              class="col-sm-3 text-center h-center"
            ></b-form-input>
          </b-form-group>
          <b-form-group
            id="input-group-2"
            v-else
            :invalid-feedback="invalidNicknameFeedback"
          >
            <b-form-input
              id="input-2"
              v-model="participantName"
              type="text"
              placeholder="Nickname"
              :state="nicknameState"
              required
              class="col-sm-3 text-center h-center"
            ></b-form-input>
          </b-form-group>
          <!--<b-button class="m-1" variant="danger" @click="cancel()">Cancel</b-button>-->
          <b-button class="col-sm-3" variant="primary" @click="enter()">Enter</b-button>
        </b-form>
        <hr class="my-4" />
        <!--<b-button class="col-sm-1 m-1" variant="danger" @click="goExaminer()">출제자로</b-button>
        <b-button class="col-sm-1 m-1" variant="danger" @click="goPlayer()">참가자로</b-button>-->
      </b-jumbotron>
    </b-overlay>
  </div>
</template>

<script>
import Vue from "vue";
import { createToken, saveParticipant, loadParticipant, clearParticipant } from "@/utils/gameSession";
import { BJumbotron } from "bootstrap-vue";
Vue.component("b-jumbotron", BJumbotron);

export default {
  computed: {
    roomNumberState() {
      return this.roomNum.length === 4;
    },
    nicknameState() {
      return this.participantName.length > 0;
    },
    invalidRoomNumberFeedback() {
      /*if (this.roomNum.length > 0) {
        return "Enter 4 numbers.";
      }
      return "Please enter numbers.";*/
    },
    invalidNicknameFeedback() {
      //return "Please enter something.";
    },
  },
  data() {
    return {
      name: "PtEnter",
      roomNum: "",
      participantName: "",
      typeRoomNumber: true,
      show: false,
    };
  },
  created() {
    this.checkResume();
  },
  methods: {
    // 이전에 참여 중이던 게임이 있으면 이어서 참여할지 확인
    checkResume() {
      const saved = loadParticipant();
      if (!saved || !saved.roomNum || !saved.nickname || !saved.token) return;

      this.$http
        .get("/api/v1/room/check/nickname", {
          params: { number: saved.roomNum, nickname: saved.nickname, token: saved.token },
        })
        .then((res) => {
          if (res.data.code !== 0 || !res.data.result || !res.data.result.rejoin) {
            clearParticipant();
            return;
          }
          if (confirm(saved.roomNum + "번 방에 '" + saved.nickname + "'(으)로 참여 중이었습니다. 이어서 참여하시겠습니까?")) {
            this.$router.push({
              name: "PlayMain",
              params: { number: saved.roomNum, nickname: saved.nickname },
            });
          } else {
            clearParticipant();
          }
        })
        .catch(() => clearParticipant());
    },
    goExaminer() {
      this.$router.push({
        name: "ExamChat",
        params: { number: "1234", nickname: "hygjob" },
      });
    },
    goPlayer() {
      this.$router.push({
        name: "PlayChat",
        params: { number: "1234", nickname: "player1" },
      });
    },

    enter() {
      if (this.typeRoomNumber) {
        if (this.roomNum === "") {
          alert("방 번호를 입력하세요.");
          return;
        } else if (this.roomNum.length !== 4) {
          alert("방 번호 4자리를 입력하세요.");
          return;
        }
        this.show = true;
        this.$http
          .get("/api/v1/room/check/number/" + this.roomNum)
          .then((res) => {
            if (res.data.code === 0) {
              this.typeRoomNumber = false;
            } else {
              alert(res.data.message);
            }
            this.show = false;
          })
          .catch((error) => {
            console.log(error);
            alert("서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
            this.show = false;
          });
      } else {
        if (this.participantName === "") {
          alert("닉네임을 입력하세요.");
          return;
        }
        const saved = loadParticipant();
        const token = (saved && saved.roomNum === this.roomNum && saved.nickname === this.participantName && saved.token)
          ? saved.token : createToken();
        this.$http
          .get("/api/v1/room/check/nickname", {
            params: { number: this.roomNum, nickname: this.participantName, token: token },
          })
          .then((res) => {
            if (res.data.code === 0) {
              saveParticipant({ roomNum: this.roomNum, nickname: this.participantName, token: token });
              this.$router.push({
                name: "PlayMain",
                params: {
                  number: this.roomNum,
                  nickname: this.participantName,
                },
              });
            } else {
              alert(res.data.message);
            }
          })
          .catch((error) => {
            console.log(error);
            alert("서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
          });
      }
    },
    cancel() {
      this.$router.go(-1);
    },
  },
};
</script>

<style>
.h-center {
  float: none; 
  margin: 0 auto
}
</style>