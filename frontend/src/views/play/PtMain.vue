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
        <template #header>Play <br> Uknowhat?</template>
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
              placeholder="Enter Room Number"
              :state="roomNumberState"
              required
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
              placeholder="Enter Nickname"
              :state="nicknameState"
              required
            ></b-form-input>
          </b-form-group>

          <b-button variant="primary" @click="enter()">Enter</b-button>
          <b-button variant="danger" @click="cancel()">Cancel</b-button>
          <b-button variant="primary" @click="goExaminer()">출제자로</b-button>
          <b-button variant="primary" @click="goPlayer()">참가자로</b-button>
        </b-form>
      </b-jumbotron>
    </b-overlay>
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
      if (this.roomNum.length > 0) {
        return 'Enter 4 numbers.';
      }
      return 'Please enter numbers.';
    },
    invalidNicknameFeedback() {
      return 'Please enter something.';
    }
  },
  data() {
    return {
      name: "PtMain",
      roomNum: "",
      participantName: "",
      typeRoomNumber: true,
      show: false
    };
  },
  methods: {
    goExaminer(){
      this.$router.push({name: 'ExamChat', params: { number: "1234", nickname: "hygjob"}});
    },
    goPlayer(){
      this.$router.push({name: 'PlayChat', params: { number: "1234", nickname: "player1"}});
    },

    enter() {
      if (this.typeRoomNumber) {
        if (this.roomNum === '') {
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
          .catch((error) => {console.log(error); this.show = false;});
      } else {
        if (this.participantName === '') {
          alert("닉네임을 입력하세요.");
          return;
        }
        this.$http
          .get("/api/v1/room/check/nickname?number=" + this.roomNum + "&nickname=" + this.participantName)
          .then((res) => {
            if (res.data.code === 0) {
              this.$router.push({name: 'Chat', params: { number: this.roomNum, nickname: this.participantName}});
            } else {
              alert(res.data.message);
            }
          })
          .catch((error) => {console.log(error);});
      }
    },
    cancel() {
      this.$router.go(-1);
    },
  }
};
</script>