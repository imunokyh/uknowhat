<template>
  <div class="overflow-auto"   >
    <b-container style="height: 900px">
      <b-row>&nbsp;</b-row>
      <b-row>
        <b-col cols="2"></b-col>
        <b-col cols="7"><h1>문제은행</h1></b-col>
        <b-col cols="3"> </b-col>
      </b-row>

      <hr
        style="
          height: 2px;
          border-width: 0;
          color: gray;
          background-color: gray;
        "
      />
      <b-row>
        <b-button variant="primary" @click="initVal()">추가</b-button>
        <b-button variant="danger" @click="delProb()">삭제</b-button>
      </b-row>
      <b-row>
        <b-table
          selectable
          select-mode="single"
          @row-selected="onRowSelected"
          striped
          hover
          ref="quiztable"
          :items="myProvider"
          :fields="fields"
        >
        </b-table
      ></b-row>
      <b-row>
        <!-- main content -->
        <b-col cols="10">
          <quiz-comp ref="quizdata" @child-event="parentReceive"></quiz-comp>
        </b-col>
        <!-- e.main content -->
        <b-col cols="2">
          <div><b-button @click="save()">저장</b-button></div>
        </b-col>
      </b-row>

    <!-- debug
      <p>
        Selected Rows:<br />
        {{ selected }}
      </p>
      -->
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
      selected: [],
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
      items: [],
      fields: [
        { key: "questionId", label: "ID" },
        { key: "questionType", label: "TYPE" },
        { key: "questionText", label: "내용" },
        { key: "questionAnswer", label: "답" },
      ],
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
    tableRefresh() {
      console.log("refresh");
      this.$refs.quiztable.refresh();
    },
    listQuiz() {
      this.$http.get("/api/v1/question").then((res) => {
        this.items = [];
        this.items = this.items.concat(res.data.result.content);
      });
    },
    myProvider(ctx) {
      let promise = this.$http.get("/api/v1/question");
      return promise.then((res) => {
        return res.data.result.content || [];
      });
    },
    save() {
      let quizId = this.$refs.quizdata.quizId;
      let question = this.$refs.quizdata.question;
      let questionType = this.$refs.quizdata.selectedType;
      if (questionType == null) {
        alert("퀴즈타입을 선택해주세요");
        return;
      }
      //let timeLimitType = this.$refs.quizdata.selectedTimeLimit;
      //let pointType = this.$refs.quizdata.selectedPoint;
      let ans1 = this.$refs.quizdata.ans[1];
      let ans2 = this.$refs.quizdata.ans[2];
      let ans3 = this.$refs.quizdata.ans[3];
      let ans4 = this.$refs.quizdata.ans[4];

      //console.log(pointType, timeLimitType, questionType);
      let answer =
        questionType == "TF"
          ? this.$refs.quizdata.selectedTf
          : this.$refs.quizdata.radioSelected;

    let method = quizId > 0 ? 'put': 'post';

      // 문제저장
      // ref: probbank http://namchengju.com/Board/Detail/52/1077
      this.$http({
        method: method,
        url:"/api/v1/question",
        data: {
          //roomId: this.roomId,
          questionId: quizId,
          questionType: questionType,
          timeLimitType: "",
          pointType: "",
          questionText: question,
          answer1Text: ans1,
          answer2Text: ans2,
          answer3Text: ans3,
          answer4Text: ans4,
          answer: answer,
        }})
        .then((res) => {
          if (res.data.code == 0) {
            this.$refs.quiztable.refresh();
            alert("저장했습니다");
            this.initVal();
          } else {
            alert("저장시 문제가 발생했습니다");
          }
        });
    },
    initVal() {
      this.$refs.quizdata.quizId = null;
      this.$refs.quizdata.question = null;
      this.$refs.quizdata.selectedType = null;
    },
    delProb(){
      if ( this.selected == null){
        alert('삭제할 문제를 선택해 주십시오.')
        return;
      }
      if(confirm('해당 문제를 삭제하시겠습니까?') == false){
        return;
      }

      this.$http({
        method: 'delete',
        url:"/api/v1/question/" + this.selected.questionId,
        })
        .then((res) => {
          if (res.data.code == 0) {
            this.$refs.quiztable.refresh();
            alert("삭제했습니다");
            this.initVal();
          } else {
            alert("삭제시 문제가 발생했습니다");
          }
        });
    },
    onRowSelected(items) {
      this.selected = items[0];
       this.$refs.quizdata.question = this.selected.questionText;
       this.$refs.quizdata.quizId = this.selected.questionId;
       this.$refs.quizdata.selectedType = this.selected.questionType;

       if (this.$refs.quizdata.selectedType  == 'TF'){
this.$refs.quizdata.selectedTf = this.selected.questionAnswer;
       }else{
         this.$refs.quizdata.radioSelected = this.selected.questionAnswer;
         this.$refs.quizdata.ans[1] = this.selected.answer1Text;
         this.$refs.quizdata.ans[2] = this.selected.answer2Text;
         this.$refs.quizdata.ans[3] = this.selected.answer3Text;
         this.$refs.quizdata.ans[4] = this.selected.answer4Text;
       }

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