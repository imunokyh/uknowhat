<template>
  <div style="height: 100%">
    <b-jumbotron
      class="h-100"
      bg-variant="dark"
      text-variant="white"
      border-variant="dark"
    >
      <template #header>Uknowhat?</template>

      <template #lead>
        재미있게 같이 문제를 풀어봐요.
      </template>

      <hr class="my-4" />

      <p>함께 해 볼까요</p>
      <b-button size="lg" variant="success" @click="examiner()">출제자</b-button>

      <b-button size="lg" variant="primary" @click="participation()">참가자</b-button>
    </b-jumbotron>
  </div>
</template>

<script>
import Vue from "vue";
import { BJumbotron } from "bootstrap-vue";
Vue.component("b-jumbotron", BJumbotron);

export default {
  data() {
    return {
      name: "Home",
    };
  },
  methods: {
    join() {
      console.log('join');
      
      this.$http
        .get("/api/v1/qsn/question/list")
        .then((res) => {
          console.log(res);
        })
        .catch((error) => console.log(error));
        
    },
    examiner() {
      // 출제자 메인
      //this.$router.push({name: 'RoomMng'});
      this.$router.push({name: 'Login'});
    },
    participation() {
      // 참가자 메인
      this.$router.push({name:'PtEnter'});
        
    },
  },
};
</script>
