<template>
  <!-- 게임 플레이 화면 (4지 선다 문제 표시) -->
  <div class="text-white bg-dark" style="height: 768px">
    <b-container>
      <b-row>&nbsp;</b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col></b-col>
        <b-col><h2 class="text-success">객관식 선택</h2></b-col>
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
  data(){
    return{
      name: 'ObjectiveChoice',
      time: 10,
      answers: 2,
      prob: '다음중 광운UBF의 위치는?',
      choice1: '광운대역',
      choice2: '광운대',
      choice3: '광운로39',
      choice4: '석계역',
    }
  }

}
</script>

<style>

</style>