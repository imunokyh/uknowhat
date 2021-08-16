<template>
  <!-- 게임 플레이 화면 (4지 선다 문제 표시) -->
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
      <b-col><b-button>{{choice1}}</b-button></b-col>
      <b-col><b-button>{{choice2}}</b-button></b-col>
    </b-row>
    <b-row>
      <b-col><b-button>{{choice3}}</b-button></b-col>
      <b-col><b-button>{{choice4}}</b-button></b-col>
    </b-row>
    {{prob}}
  </b-container>
</template>
</template>

<script>
export default {
  data(){
    return{
      name: 'ObjectiveChoice',
      prob: '다음중 광운UBF의 위치는?',
      choice1: '광운대역',
      choice2: '광운대',
      choice3: '광운로39',
      choice1: '석계역',
    }
  }

}
</script>

<style>

</style>