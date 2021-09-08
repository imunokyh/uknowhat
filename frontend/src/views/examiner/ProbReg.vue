<template>
  <div class="topbar">
    <b-container>
      <!--      
      <b-row>
        <b-col cols="2"></b-col>
        <b-col cols="7"><h1>방문제등록</h1></b-col>
        <b-col cols="3">
          <b-button>문제은행</b-button>
      
          <b-button>완료</b-button>
        </b-col>
      </b-row>
      <b-row> roomId: {{ roomId }} / roomTitle: {{ roomTitle }} </b-row>
      -->
      <hr
        style="
          height: 2px;
          border-width: 0;
          color: gray;
          background-color: gray;
        "
      />
      <b-row>&nbsp;</b-row>
      
      <b-row
        ><!--left-->
        
        


        <b-col cols="6" class="vl">
          Title:<b-form-input v-model="roomTitle"></b-form-input>

          <b-row>
          <b-col sm="2">
            <b-button @click="modProb()">편집</b-button>
          </b-col>
          <b-col sm="2">
            <b-button @click="delProb()">삭제</b-button>
          </b-col>
          <b-col sm="4">
            roomId: {{roomId}}/ probId:{{}}
          </b-col>
        </b-row>

          <b-table
            selectable
            select-mode="single"
            @row-selected="onProbSelected"
            striped
            hover
            ref="probtable"
            :items="probItems"
            :fields="probFields"
          ></b-table>
        </b-col>
        <b-col cols="6" class="vl">
          <b-row>
            <b-col sm="5">
              <b-form-input
                v-model="searchText"
                placeholder="검색어"
              ></b-form-input>
            </b-col>
            <b-col sm="3">
              <b-form-select
                v-model="selectedSearchType"
                :options="optionsSearchType"
              >
              </b-form-select
            ></b-col>
            <b-col sm="2">
              <b-button>검색</b-button>
            </b-col>
            <b-col sm="2">
              <b-button @click="add()">추가</b-button>
            </b-col>
          </b-row>
          <br />
          <b-row class="overflow-auto" style="height: 400px">
            <b-table
              selectable
              select-mode="single"
              @row-selected="onBankSelected"
              striped
              hover
              ref="banktable"
              :items="bankProvider"
              :fields="bankFields"
            >
            </b-table>
          </b-row>
        </b-col>
      </b-row>

      <b-row>
        <quiz-comp ref="quizdata"></quiz-comp>
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
      searchText: "",
      selectedSearchType: null,
      optionsSearchType: [
        { value: null, text: "문제타입" },
        { value: "tf", text: "OX" },
        { value: "obj", text: "객관식" },
      ],
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
      probItems: [],
      probFields: [
        { key: "quesOrder", label: "순서" },
        { key: "quesType", label: "문제유형" },
        { key: "quesText", label: "문제내용" },
        { key: "quesScore", label: "점수" },
        { key: "quesTime", label: "시간" },
      ],
      bankFields: [{ key: "questionText", label: "내용" }],
    };
  },
  created() {
    this.genQuizId();
  },
  computed: {},
  mounted() {
    //this.genQuizId();

    if (this.newRoom == 1) {
      console.log("new prob group");
    } else {
      console.log("modify prob group");
    }
    this.roomId = 1;
    this.roomTitle = "test";
  },
  methods: {
    modProb(){

    },
    delProb(){

    },
    add() {
      let roomId = this.roomId;
      let quizId = this.$refs.quizdata.quizId;
      let time = 5;
      let point = 10;
      this.$http({
        method: method,
        url:"/api/v1/roomquestion",
        data: {
          //roomId: this.roomId,
          roomId: roomId,
          quizId: quizId,
          time: time,
          point: point,
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
      this.probItems.push({ quesType: "TF", quesText: "문제다" });
    },
    onProbSelected(items) {},
    onBankSelected(items) {
      this.selected = items[0];
      this.$refs.quizdata.question = this.selected.questionText;
      this.$refs.quizdata.quizId = this.selected.questionId;
      this.$refs.quizdata.selectedType = this.selected.questionType;

      if (this.$refs.quizdata.selectedType == "TF") {
        this.$refs.quizdata.selectedTf = this.selected.questionAnswer;
      } else {
        this.$refs.quizdata.radioSelected = this.selected.questionAnswer;
        this.$refs.quizdata.ans[1] = this.selected.answer1Text;
        this.$refs.quizdata.ans[2] = this.selected.answer2Text;
        this.$refs.quizdata.ans[3] = this.selected.answer3Text;
        this.$refs.quizdata.ans[4] = this.selected.answer4Text;
      }
    },
    bankProvider(ctx) {
      let promise = this.$http.get("/api/v1/question");
      return promise.then((res) => {
        return res.data.result.content || [];
      });
    },
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
      let questionType = this.selectedQuestion;
      let timeLimitType = this.selectedTimeLimit;
      let pointType = this.selectedPoint;
      let timeLimit;
      let ans1 = this.$refs.quizdata.ans1;
      let ans2 = this.$refs.quizdata.ans2;
      let ans3 = this.$refs.quizdata.ans3;
      let ans4 = this.$refs.quizdata.ans4;
      let check1 = this.$refs.quizdata.check1;
      let check2 = this.$refs.quizdata.check2;
      let check3 = this.$refs.quizdata.check3;
      let check4 = this.$refs.quizdata.check4;

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
      this.$http
        .post("/api/v1/question", {
          roomId: this.roomId,
          questionType: questionType,
          timeLimitType: timeLimitType,
          pointType: pointType,
          questionText: question,
          answer: check1 + "," + check2 + "," + check3 + "," + check4,
        })
        .then((res) => {});
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
.topbar {
  display: block;
  width: 100%;
  height: 900px;
  background-color: white;
  overflow: scroll;
}
</style>