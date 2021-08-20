<template>
  <div class="text-white bg-dark" style="height: 768px">
    <b-container>
      <!-- 게임대기화면 -->
      <b-row>&nbsp;</b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col cols="3"
          ><h3>참여자수: {{ participationNo }}</h3></b-col
        >
        <b-col cols="6"
          ><h2>Game Pin: {{ gamepin }}</h2></b-col
        >
        <b-col cols="3"
          ><b-button variant="info" @click="goNext()">시작</b-button></b-col
        >
      </b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col cols="2"></b-col>
        <b-col cols="8">
          <b-button variant="success">고현영</b-button>
          <b-button variant="info">김윤호</b-button>
        </b-col>
        <b-col cols="2"></b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
//import TrueFalseProbProc from './TrueFalseProbProc.vue';
export default {
  data() {
    return {
      name: "Waiting",
      role: "출제자",
      rank: "1",
      gamepin: "2083316",
      participationNo: 2,
    };
  },
  methods: {
    goNext() {
      console.log("goNext");
      this.$router.push("TrueFalseQuestion");
    },
  },
};
</script>

<style>
body {
  color: white;
}
</style>