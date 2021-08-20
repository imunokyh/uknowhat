<template>
  <!-- 게임 플레이 화면 (4지 선다 정답 인원) -->
  <div class="text-white bg-dark" style="height: 768px">
    <b-container>
      <b-row>&nbsp;</b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col></b-col>
        <b-col><h2 class="text-success">객관식 결과</h2></b-col>
        <b-col></b-col>
      </b-row>
      <b-row>
        <b-col></b-col>
        <b-col></b-col>
        <b-col><b-button>다음문제</b-button></b-col>
      </b-row>
      <b-row>
        <b-col cols="2"></b-col>
        <b-col cols="8"
          ><h1>{{ prob }}</h1></b-col
        >
        <b-col cols="2"></b-col>
      </b-row>
      <b-row>
        <b-col></b-col>
        <b-col><h2>분포도차트</h2></b-col>
        <b-col></b-col>
      </b-row>

      <div class="small">
        <!--
      <line-comp :chart-data="dataCollection"></line-comp>
      -->
        <bar-comp :chart-data="barData" :options="options"></bar-comp>
        <button @click="fillData()">Randomize</button>
      </div>
    </b-container>
  </div>
</template>

<script>
import BarComp from "@/components/BarComp";
export default {
  components: {
    BarComp,
  },
  data() {
    return {
      name: "ObjectiveCheck",
      prob: "다음중 광운UBF의 위치는?",
      choice1: "광운대역",
      choice2: "광운대",
      choice3: "광운로39",
      choice4: "석계역",
      barData: null,
      options: {
        // title
        title: {
          display: true,
          text: "결과",
        },

        scales: {
          yAxes: [
            {
              ticks: {
                beginAtZero: true,
              },
              gridLines: {
                display: false,
              },
            },
          ],
          xAxes: [
            {
              ticks: {
                beginAtZero: true,
              },
              gridLines: {
                display: false,
              },
            },
          ],
        },
        legend: {
          display: false,
        },
        responsive: true,
        maintainAspectRatio: false,
        height: 200,
      },
    };
  },
  created() {
    this.fillData();
  },
  methods: {
    fillData() {
      this.barData = {
        labels: ["1번", "2번", "3번", "4번"],
        datasets: [
          {
            label: "선택수",
            backgroundColor: "#008B8B",
            data: [this.getRandomInt(), this.getRandomInt(),this.getRandomInt(), this.getRandomInt()],
          },
        ],
     };
    },
    getRandomInt() {
      return Math.floor(Math.random() * (50 - 4)) + 5;
    },
  },
};
</script>

<style>
</style>