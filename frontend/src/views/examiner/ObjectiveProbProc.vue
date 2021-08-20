<template>
  <!-- 게임 플레이 화면 (4지 선다 문제 표시) -->
  <div class="text-white bg-dark" style="height: 768px">
    <!-- 게임 플레이 화면 (OX 문제 정답 인원) -->
    <b-container>
      <b-row>&nbsp;</b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col></b-col>
        <b-col><h2 class="text-success">객관식 문제</h2></b-col>
        <b-col></b-col>
      </b-row>
      <b-row>
        <b-col></b-col>
        <b-col></b-col>
        <b-col><b-button variant="info">다음</b-button></b-col>
      </b-row>
      <b-row>
        <b-col cols="2"></b-col>
        <b-col cols="8"
          ><h1>{{ prob }}</h1></b-col
        >
        <b-col cols="2"></b-col>
      </b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col></b-col>
        <b-col><h3>1번. {{choice1}}</h3></b-col>
        <b-col><h3>2번. {{choice2}}</h3></b-col>
        <b-col></b-col>
      </b-row>
      <b-row>&nbsp;</b-row>
      
      <b-row>
        <b-col></b-col>
        <b-col><h3>3번. {{choice3}}</h3></b-col>
        <b-col><h3>4번. {{choice4}}</h3></b-col>
        <b-col></b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      name: "ObjectiveProb",
      prob: "다음중 광운UBF의 위치는?",
      choice1: "광운대역",
      choice2: "광운대학교",
      choice3: "광운로39",
      choice4: "석계역",
    };
  },
};
</script>

<style>
</style>