<template>
  <div style="height: 1000px">
    <b-container>
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col cols="2"></b-col>
        <b-col cols="7"><h1>문제은행</h1></b-col>
        <b-col cols="3">
          
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
        <b-button variant="primary" @click="tableRefresh()">추가</b-button>
        <b-button variant="primary">편집</b-button>
        <b-button variant="danger">삭제</b-button>
      </b-row>
      <b-row>
        <b-table ref="quiztable"
        :items="myProvider"
        :fields="fields"
      > 
      </b-table></b-row>
      <b-row>
        <!-- main content -->
        <b-col cols="8">
          <quiz-comp ref="quizdata" @child-event="parentReceive"></quiz-comp>
        </b-col>
        <!-- e.main content -->
        <b-col cols="2">
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
    newRoom: {
      type: Number,
      default: 1, // 1: yes 2: no
    },
    roomId: {
      type: Number,
      default: -1,
    },
    roomTitle: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      cardCnt: 4,
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
      items:[],
      fields:[
        {key: 'questionId', label: 'ID'},
        {key: 'questionType', label: '문제유형'},        
        {key: 'questionText', label: '문제내용'},
        {key: 'questionAnswer', label: '문제정답'},
      ]
    };
  },
  created() {
    this.genQuizId();
  },
  computed: {},
  mounted() {
    this.listQuiz();
    
    
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
    tableRefresh(){
      console.log('refresh')
      this.$refs.quiztable.refresh()
    },
     listQuiz(){
      this.$http.get('/api/v1/question')
      .then(res=>{
        this.items = []
        this.items = this.items.concat(res.data.result.content);
        
      })
    },
    myProvider(ctx){
      let promise = this.$http.get('/api/v1/question');
            return promise.then((res) => {
                return(res.data.result.content || []);
            });
    },
    save() {
      let quizid = this.$refs.quizdata.quizid;
      let question = this.$refs.quizdata.question;
      let questionType = this.$refs.quizdata.selectedQuestion;
      let timeLimitType = this.$refs.quizdata.selectedTimeLimit;
      let pointType = this.$refs.quizdata.selectedPoint;
      let ans1 = this.$refs.quizdata.ans1;
      let ans2 = this.$refs.quizdata.ans2;
      let ans3 = this.$refs.quizdata.ans3;
      let ans4 = this.$refs.quizdata.ans4;
      let check1 = this.$refs.quizdata.check1;
      let check2 = this.$refs.quizdata.check2;
      let check3 = this.$refs.quizdata.check3;
      let check4 = this.$refs.quizdata.check4;
      console.log(pointType, timeLimitType, questionType);

      // ref: https://blog.naver.com/PostView.naver?blogId=varkiry05&logNo=221835597905&redirect=Dlog&widgetTypeCall=true&directAccess=false
      // this.$http
      //   .post("/api/v1/sample/post", {
      //     id: 1,
      //     question: question,
      //     answer: ans1,
      //   })
      //   .then((res) => {

      //   });

      // 문제저장
      // ref: probbank http://namchengju.com/Board/Detail/52/1077
      this.$http
        .post("/api/v1/question", {
          //roomId: this.roomId,
          questionType: questionType,
          timeLimitType: timeLimitType,
          pointType: pointType,
          questionText: question,
          answer1Text: ans1,
          answer2Text: ans2,
          answer3Text: ans3,
          answer4Text: ans4,
          answer: check1 + check2 + check3 + check4,
        })
        .then((res) => {
          if(res.data.code == 0){
            this.$refs.quiztable.refresh();
            alert('저장했습니다')
            
          }else{
            alert('저장시 문제가 발생했습니다')
          }
        });
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