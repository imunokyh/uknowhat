<template>
  <!-- 게임 플레이 화면 (OX 문제 남은 시간) -->
  <div class="text-white bg-dark" style="height: 768px">
    <b-container>
      <b-row>&nbsp;</b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col></b-col>
        <b-col><h2 class="text-success">참 거짓 선택</h2></b-col>
        <b-col></b-col>
      </b-row>
      <b-row>
        <b-col cols="3"><h3>남은시간: {{ time }}</h3></b-col>
        <b-col cols="6"></b-col>
        <b-col cols="3"><h3>응답제출자수: {{ answers }}</h3></b-col>
      </b-row>
  
      <b-row>&nbsp; </b-row>
      <b-row>&nbsp; </b-row>
      <b-row>&nbsp; </b-row>
      <b-row>
        <b-col cols="2"></b-col>
        <b-col cols="8"
          ><h1>{{ prob }}</h1></b-col
        >
        <b-col cols="2"></b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      name: "TrueFalseChoice",
      prob: "하나님께서 천지를 창조하셨다",
      time: 18,
      answers: 1,
    };
  },
};
</script>

<style>
</style>