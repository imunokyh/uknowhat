<template>
  <div style="height: 100%">
    <b-button class="m-1 float-right" size="sm" variant="success" @click="examiner()">Login</b-button>
    <b-jumbotron
      class="h-100"
      bg-variant="dark"
      text-variant="white"
      border-variant="dark"
    >
      <img class="mt-5 mb-5 img-fluid" alt="Title" src="../assets/title.png">
      <!--<template #header>Uknowhat?</template>-->

      <!--
      <template #lead>
        재미있게 같이 문제를 풀어봐요.
      </template>
      -->

      <!--
      <p>함께 해 볼까요</p>

      <b-button class="m-1" size="lg" variant="success" @click="examiner()">출제자</b-button>
      <b-button class="m-1" size="lg" variant="primary" @click="participation()">참가자</b-button>
      -->

      <b-card bg-variant="white" text-variant="white" class="text-center h-center col-md-4">
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
              class="text-center h-center"
              size="lg"
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
              class="text-center h-center"
              size="lg"
            ></b-form-input>
          </b-form-group>
          <!--<b-button class="m-1" variant="danger" @click="cancel()">Cancel</b-button>-->
          <b-button class="col-md-12" size="lg" variant="dark" @click="enter()">Enter</b-button>
        </b-form>
      </b-card>
    </b-jumbotron>
  </div>
</template>

<script>
import Vue from "vue";
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
      name: "Home",
      roomNum: "",
      participantName: "",
      typeRoomNumber: true,
      show: false,
    };
  },
  methods: {
    examiner() {
      // 출제자 메인
      //this.$router.push({name: 'RoomMng'});
      this.$router.push({name: 'Login'});
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
            this.show = false;
          });
      } else {
        if (this.participantName === "") {
          alert("닉네임을 입력하세요.");
          return;
        }
        this.$http
          .get(
            "/api/v1/room/check/nickname?number=" +
              this.roomNum +
              "&nickname=" +
              this.participantName
          )
          .then((res) => {
            if (res.data.code === 0) {
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
          });
      }
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