<template>
  <div class="overflow-auto">
    <b-container class="bv-example-row bv-example-row-flex-cols h-100" fluid="sm">
      <b-row>&nbsp;</b-row>

      <b-row cols="3">
        <b-col>
          <b-button class="col-sm-4" @click="goRoomMng()">방 관리로</b-button>
        </b-col>
        <b-col><h1>문제은행</h1></b-col>
        <b-col></b-col>
      </b-row>

      <hr
        style="
          height: 2px;
          border-width: 0;
          color: gray;
          background-color: gray;
        "
      />

      <b-row cols="3" align-h="center">
        <b-col></b-col>
        <b-col>
          <b-pagination
            v-model="currentPage"
            :total-rows="totalRows"
            :per-page="perPage"
            aria-controls="my-table"
            align="center"
          ></b-pagination>
        </b-col>
        <b-col>
          <b-button class="col-sm-4 mr-2" variant="danger" @click="delProb()">삭제</b-button>
          <b-button class="col-sm-4 ml-2" variant="primary" @click="initVal()">추가</b-button>
        </b-col>
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
          :per-page="perPage"
          :current-page="currentPage"
        ></b-table>
      </b-row>

<!--
      <b-row align-h="end">
        <b-col cols="1">
          <b-button class="col-sm-12" variant="danger" @click="delProb()">삭제</b-button>
        </b-col>
        <b-col cols="1">
          <b-button class="col-sm-12" variant="primary" @click="initVal()">추가</b-button>
        </b-col>
      </b-row>
-->

      <b-row>
        <!-- main content -->
        <b-col cols="11">
          <quiz-comp ref="quizdata" @child-event="parentReceive"></quiz-comp>
        </b-col>
        <!-- e.main content -->
        <b-col cols="1">
          <b-button class="col-sm-12" variant="success" @click="save()">저장</b-button>
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
  data() {
    return {
      perPage: 7,
      currentPage: 1,
      totalRows: 0,
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
  computed: {

  },
  mounted() {
    //this.listQuiz();
  },
  methods: {
    
    goRoomMng() {
      // 방관리로 이동
      this.$router.push({ name: "RoomMng" });
    },
    genQuizId() {
      this.quizid = "quizid_" + this.uuidv4();
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
      this.$refs.quiztable.refresh();
    },
    listQuiz() {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
        params:{
          page: 0,
          size: 100000
        }
      };
      this.$http.get("/api/v1/question/me", config).then((res) => {
        this.items = [];
        this.items = this.items.concat(res.data.result.content);
      });
    },
    myProvider(ctx) {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
        params:{
          page: ctx.currentPage - 1,
          size: ctx.perPage
        }
      };
      let promise = this.$http.get("/api/v1/question/me", config);
      return promise.then((res) => {
        this.totalRows = res.data.result.totalElements
        return res.data.result.content || [];
      });
    },
    save() {
      let questionId = this.$refs.quizdata.questionId;
      let questionText = this.$refs.quizdata.questionText;
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

      if ((questionType == "OX" && this.$refs.quizdata.selectedTf === null) ||
          (questionType == "OB" && this.$refs.quizdata.radioSelected === null))
      {
        alert("정답을 선택하세요");
        return;
      }

      //console.log(pointType, timeLimitType, questionType);
      let answer =
        questionType == "OX"
          ? this.$refs.quizdata.selectedTf
          : this.$refs.quizdata.radioSelected;

      let method = questionId > 0 ? "put" : "post";

      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      // 문제저장
      // ref: probbank http://namchengju.com/Board/Detail/52/1077
      this.$http({
        method: method,
        url: "/api/v1/question",
        headers: config.headers,
        data: {
          questionId: questionId,
          questionType: questionType,
          timeLimitType: "",
          pointType: "",
          questionText: questionText,
          answer1Text: ans1,
          answer2Text: ans2,
          answer3Text: ans3,
          answer4Text: ans4,
          answer: answer,
        },
      }).then((res) => {
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
      this.$refs.quizdata.questionId = null;
      this.$refs.quizdata.question = null;
      this.$refs.quizdata.selectedType = null;
      this.$refs.quizdata.questionText = null;
      this.$refs.quizdata.selectedTf = null;
      this.$refs.quizdata.radioSelected = null;
      this.$refs.quizdata.ans[1] = null;
      this.$refs.quizdata.ans[2] = null;
      this.$refs.quizdata.ans[3] = null;
      this.$refs.quizdata.ans[4] = null;
    },
    delProb() {
      if (this.selected == null) {
        alert("삭제할 문제를 선택해 주십시오.");
        return;
      }
      if (confirm("해당 문제를 삭제하시겠습니까?") == false) {
        return;
      }
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      this.$http({
        method: "delete",
        url: "/api/v1/question/" + this.selected.questionId,
        headers: config.headers,
      }).then((res) => {
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
      if (items == null || items[0] == null) {
        return;
      }
      this.selected = items[0];
      this.$refs.quizdata.questionText = this.selected.questionText;
      this.$refs.quizdata.questionId = this.selected.questionId;
      this.$refs.quizdata.selectedType = this.selected.questionType;

      if (this.$refs.quizdata.selectedType == "OX") {
        this.$refs.quizdata.selectedTf = this.selected.questionAnswer;
      } else {
        this.$refs.quizdata.radioSelected = this.selected.questionAnswer;
        this.$refs.quizdata.ans[1] = this.selected.answer1Text;
        this.$refs.quizdata.ans[2] = this.selected.answer2Text;
        this.$refs.quizdata.ans[3] = this.selected.answer3Text;
        this.$refs.quizdata.ans[4] = this.selected.answer4Text;
      }

      console.log(this.$refs.quizdata.questionText)
    },
    parentReceive(val) {
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