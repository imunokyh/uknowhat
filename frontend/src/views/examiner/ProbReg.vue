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
      <hr
        style="
          height: 2px;
          border-width: 0;
          color: gray;
          background-color: gray;
        "
      />
      <b-row>&nbsp;</b-row>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col cols="2">
          <b-card title="quiz1"></b-card><b-card title="quiz2"></b-card>
        </b-col>

        <!-- main content -->
        <b-col cols="8" class="vl">
          <quiz-comp ref="quizdata" @child-event="parentReceive"></quiz-comp>
        </b-col>
        <!-- e.main content -->
        <b-col cols="2" class="vl">
          <div><b-button @click="save()">저장</b-button></div>
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
  props: {
    probGrpId: {
      type: String,
      default: "",
    },    
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
    console.log("probGrpId:" + this.probGrpId);
    if (this.probGrpId == "") {
      console.log("new prob group");
    } else {
      console.log("modify prob group");
    }
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
    save() {
      let quizid = this.$refs.quizdata.quizid;
      let question = this.$refs.quizdata.question;
      let ans1 = this.$refs.quizdata.ans1;
      let ans2 = this.$refs.quizdata.ans2;
      let ans3 = this.$refs.quizdata.ans3;
      let ans4 = this.$refs.quizdata.ans4;
      let check1 = this.$refs.quizdata.check1;
      let check2 = this.$refs.quizdata.check2;
      let check3 = this.$refs.quizdata.check3;
      let check4 = this.$refs.quizdata.check4;
      //console.log(quizid);
      //console.log(question);
      //console.log(ans1);

      //   const axiosConfig = {
      //     headers:{
      //         "Content-Type": "application/json"
      //     }
      // }

      // ref: https://blog.naver.com/PostView.naver?blogId=varkiry05&logNo=221835597905&redirect=Dlog&widgetTypeCall=true&directAccess=false
      this.$http
        .post("/api/v1/sample/post", {
          id: 1,
          question: question,
          answer: ans1,
        })
        .then((res) => {
          //console.log(res)
        });
      /*
          this.$http.request({
            method : 'POST',
            url: '/api/v1/sample/post',
            headers: {'Content-type' : 'application/json'},
            data: {
                id: 1,
                question: question,
                answer: ans1
            }
        }).then(
            alert("회원가입에 성공했습니다.")
        )
      */
    },
    parentReceive(val) {
      console.log(val);
      console.log("parentReceive");
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