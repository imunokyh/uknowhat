<template>
  <div style="height: 768px">
    <b-container>
      <b-row>
        <b-col cols="2"></b-col>
        <b-col cols="8"></b-col>
        <b-col cols="2">
          <b-button>추가</b-button>
          <b-button>완료</b-button>
        </b-col>
      </b-row>
      <hr style="height:2px;border-width:0;color:gray;background-color:gray">
      <b-row>&nbsp;</b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col cols="2">
          <b-card title="quiz1"></b-card><b-card title="quiz2"></b-card>
        </b-col>
        

        <!-- main content -->
        <b-col cols="8" class="vl">
          <quiz-comp></quiz-comp>
        </b-col>
        <!-- e.main content -->
        <b-col cols="2" class="vl">
        
          <div><b-button>저장</b-button></div>
          <b-row>&nbsp;</b-row>
          <div><b-button>복제본</b-button></div>
          <b-row>&nbsp;</b-row>
          <div><b-button>삭제</b-button></div>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import QuizComp from "@/components/QuizComp";

export default {
  components: {
    QuizComp,
  },
  data() {
    return {
      status1: "not_accepted",
      status2: "not_accepted",
      status3: "not_accepted",
      status4: "not_accepted",
      question: "",
      ans1: "",
      ans2: "",
      ans3: "",
      ans4: "",
      quizid: "",
    };
  },
  created() {
    this.genQuizId();
  },
  computed: {},
  mounted() {
    //this.genQuizId();
  },
  methods: {
    genQuizId() {
      this.quizid = "quizid_" + this.uuidv4();
      //this.quizid = "55";
    },
    getRandomInt() {
      return Math.floor(Math.random() * (50 - 4)) + 5;
      
    },
    uuidv4() {
      return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
        /[xy]/g,
        function (c) {
          var r = (Math.random() * 16) | 0,
            v = c == "x" ? r : (r & 0x3) | 0x8;
          return v.toString(16);
        }
      );
    },
  },
};
</script>

<style>
.vl {
  border-left: 2px solid gray;
  height: 500px;
}


</style>