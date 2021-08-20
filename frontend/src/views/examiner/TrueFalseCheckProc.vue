<template>
  <div class="text-white bg-dark" style="height: 768px">
    <!-- 게임 플레이 화면 (OX 문제 정답 인원) -->
    <b-container>
      <b-row>&nbsp;</b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col></b-col>
        <b-col><h2 class="text-success">참 거짓 결과</h2></b-col>
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
//import { Line } from "vue-chartjs";
import LineComp from "@/components/LineComp";
import BarComp from "@/components/BarComp";

export default {
  components: {
    LineComp,
    BarComp,
  },
  data() {
    return {
      name: "TureFalseCheck",
      prob: "하나님께서 천지를 창조하셨다",
      dataCollection: null,
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
      (this.barData = {
        labels: ["참", "거짓"],
        datasets: [
          {
            label: "선택수",
            backgroundColor: "#008B8B",
            data: [this.getRandomInt(), this.getRandomInt()],
          },
        ],
      }),
        (this.dataCollection = {
          labels: [this.getRandomInt(), this.getRandomInt()],
          datasets: [
            {
              label: "Main One",
              backgroundColor: "#f87979",
              data: [this.getRandomInt(), this.getRandomInt()],
            },
            {
              label: "Main Two",
              backgroundColor: "#000000",
              data: [this.getRandomInt(), this.getRandomInt()],
            },
          ],
        });
      //console.log("ChartTestMain", this.dataCollection);
    },
    getRandomInt() {
      return Math.floor(Math.random() * (50 - 4)) + 5;
    },
  },
};
</script>

<style>
.small {
  max-width: 600px;
  margin: 10px auto;
}
</style>