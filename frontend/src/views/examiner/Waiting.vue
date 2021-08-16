<template>
<b-container>
  <b-label>게임대기실화면</b-label>
  <b-row>
    <b-col></b-col>
    <b-col>Game Pin: {{gamepin}}</b-col>
    <b-col></b-col>
  </b-row>
  <b-row>
    <b-col cols="3">참여자수: {{participationNo}}</b-col>
    <b-col cols="6">Kahoot</b-col>
    <b-col cols="3">
      <b-button>고현영</b-button>
      <b-button>김윤호</b-button>

    </b-col>
  </b-row>
</b-container>
</template>

<script>
export default {
  data() {
    return {
      name: 'Waiting',
      role: '출제자',
      rank: '1',
      gamepin: '2083316',
      participationNo: 2
    };
  },
};
</script>

<style>
</style>