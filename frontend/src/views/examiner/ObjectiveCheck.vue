<template>
  <!-- 게임 플레이 화면 (4지 선다 정답 인원) -->
  <b-container>
    <b-row>{{prob}}</b-row>
    <b-row>
      <b-col></b-col>
      <b-col>정답과 분포</b-col>
      <b-col><b-button>다음문제</b-button></b-col>
    </b-row>
    <b-row>분포도차트</b-row>
    <b-row>
      <b-col><b-button>{{choice1}}</b-button></b-col>
      <b-col><b-button>{{choice2}}</b-button></b-col>
    </b-row>
    <b-row>
      <b-col><b-button>{{choice3}}</b-button></b-col>
      <b-col><b-button>{{choice4}}</b-button></b-col>
    </b-row>
  </b-container>
</template>

<script>
export default {
data() {
    return {
      name: 'ObjectiveCheck',
      prob: '다음중 광운UBF의 위치는?',
      choice1: '광운대역',
      choice2: '광운대',
      choice3: '광운로39',
      choice1: '석계역',

    };
  },
}
</script>

<style>

</style>