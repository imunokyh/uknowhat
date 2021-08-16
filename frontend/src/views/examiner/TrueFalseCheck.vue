<template>
  <!-- 게임 플레이 화면 (OX 문제 정답 인원) -->
  <b-container>
    <b-row>{{prob}}</b-row>
    <b-row>
      <b-col></b-col>
      <b-col>정답과 분포</b-col>
      <b-col><b-button>다음문제</b-button></b-col>
    </b-row>
    <b-row>분포도차트</b-row>
    <b-row>
      
      <b-col><b-button>O</b-button></b-col>
      <b-col><b-button>X</b-button></b-col>
      
    </b-row>
  </b-container>
</template>

<script>
export default {
data() {
    return {
      name: 'TureFalseCheck',
      prob: '하나님께서 천지를 창조하셨다',

    };
  },
}
</script>

<style>

</style>