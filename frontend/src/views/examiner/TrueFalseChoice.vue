<template>
  <!-- 게임 플레이 화면 (OX 문제 남은 시간) -->
  <b-container>
    <b-row>
      {{prob}}
    </b-row>
    <b-row>
      <b-col cols="2">남은시간: {{time}}</b-col>
      <b-col cols="8">Kahoot@</b-col>
      <b-col cols="2">응답제출자수: {{answers}}</b-col>

    </b-row>
    <b-row>
      <b-col><b-button>True</b-button></b-col>
      <b-col><b-button>False</b-button></b-col>

    </b-row>
    {{prob}}
  </b-container>
</template>

<script>
export default {
  data(){
    return{
      name: 'TrueFalseChoice',
      prob: '하나님께서 천지를 창조하셨다',
      time: 18,
      answers: 1
    }
  }

}
</script>

<style>

</style>