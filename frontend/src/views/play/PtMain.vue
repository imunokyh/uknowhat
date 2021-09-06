<template>
  <!-- 참가자 메인 -->
  <div>
    <b-overlay :show="show" rounded="sm">
      <b-jumbotron>
        <template #header>Play Uknowhat</template>
        <b-form>
          <b-form-group id="input-group-1" v-if="typeRoomNumber">
            <b-form-input
              id="input-1"
              v-model="roomNum"
              type="text"
              placeholder="Enter Room Number"
              required
            ></b-form-input>
          </b-form-group>
          <b-form-group id="input-group-2" v-else>
            <b-form-input
              id="input-2"
              v-model="participantName"
              type="text"
              placeholder="Enter Nickname"
              required
            ></b-form-input>
          </b-form-group>

          <b-button variant="primary" @click="enter()">Enter</b-button>
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
    enter() {
      if (this.typeRoomNumber) {
        this.show = true;
        this.$http
          .get("/api/v1/room/number/" + this.roomNum)
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
        this.$router.push({name: 'Chat', params: { number: this.roomNum, nickname: this.participantName}});
      }
    },
  }
};
</script>