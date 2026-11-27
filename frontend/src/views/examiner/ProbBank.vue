<template>
  <div class="uk-admin">
    <app-bar active="bank"></app-bar>

    <div class="uk-container">
      <div class="uk-page-head">
        <div>
          <h1 class="uk-page-title">문제은행</h1>
          <p class="uk-page-sub">내가 만든 문제를 관리하고, 퀴즈방에서 골라 쓸 수 있어요.</p>
        </div>
        <div class="uk-actions">
          <button class="uk-btn light" @click="goRoomMng()">← 내 퀴즈방</button>
        </div>
      </div>

      <div class="bank-layout">
        <!-- 문제 목록 -->
        <div class="uk-card list-card">
          <div class="card-head">
            <h2 class="uk-section-title">📋 내 문제 <span class="uk-badge">{{ totalRows }}</span></h2>
            <div class="uk-actions">
              <button class="uk-btn light sm" :disabled="!selected || !selected.questionId" @click="delProb()">🗑 삭제</button>
              <button class="uk-btn sm" @click="newProb()">＋ 새 문제</button>
            </div>
          </div>

          <div class="uk-table-wrap">
            <b-table
              selectable
              select-mode="single"
              @row-selected="onRowSelected"
              hover
              ref="quiztable"
              :items="myProvider"
              :fields="fields"
              :per-page="perPage"
              :current-page="currentPage"
              show-empty
              empty-text="아직 등록한 문제가 없어요"
            >
              <template #cell(questionType)="data">
                <span class="uk-badge" :class="data.value === 'OX' ? 'ox' : 'ob'">{{ data.value === 'OX' ? 'OX' : '객관식' }}</span>
              </template>
              <template #cell(questionText)="data">
                <span class="q-text">{{ data.value }}</span>
              </template>
            </b-table>
          </div>

          <b-pagination
            v-if="totalRows > perPage"
            v-model="currentPage"
            :total-rows="totalRows"
            :per-page="perPage"
            align="center"
            class="mt-3 mb-0"
          ></b-pagination>
        </div>

        <!-- 문제 편집 -->
        <div class="uk-card editor-card">
          <div class="card-head">
            <h2 class="uk-section-title">{{ editing ? '✏️ 문제 수정' : '✨ 새 문제 만들기' }}</h2>
          </div>
          <quiz-comp ref="quizdata" @child-event="parentReceive"></quiz-comp>
          <div class="editor-actions">
            <button v-if="editing" class="uk-btn ghost" @click="newProb()">취소</button>
            <button class="uk-btn success" @click="save()">💾 저장</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import QuizComp from "@/components/QuizComp";
import AppBar from "@/components/layout/AppBar.vue";

export default {
  components: {
    QuizComp,
    AppBar,
  },
  data() {
    return {
      perPage: 8,
      currentPage: 1,
      totalRows: 0,
      selected: null,
      editing: false,
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
        { key: "questionType", label: "유형" },
        { key: "questionText", label: "문제" },
        { key: "questionAnswer", label: "정답" },
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
      }).catch((error) => {
        console.log(error);
        if (error.response && error.response.status === 401) {
          alert("로그인이 필요합니다.");
          this.$router.replace({ name: "Login" });
        }
        return [];
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
      if (!questionText || !questionText.trim()) {
        alert("문제를 입력해주세요");
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
      }).catch((error) => {
        console.log(error);
        alert("저장 중 오류가 발생했습니다. 입력값을 확인해 주세요.");
      });
    },
    newProb() {
      this.initVal();
      if (this.$refs.quiztable) this.$refs.quiztable.clearSelected();
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
      this.selected = null;
      this.editing = false;
    },
    delProb() {
      if (this.selected == null || !this.selected.questionId) {
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
      }).catch((error) => {
        console.log(error);
        alert("삭제 중 오류가 발생했습니다. 퀴즈방에서 사용 중인 문제인지 확인해 주세요.");
      });
    },
    onRowSelected(items) {
      if (items == null || items[0] == null) {
        return;
      }
      this.selected = items[0];
      this.editing = true;
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

    },
    parentReceive(val) {
      console.log("parentReceive");
    },
  },
};
</script>

<style scoped>
.bank-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 1fr);
  gap: 20px;
  align-items: start;
}
.card-head { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.card-head .uk-section-title { margin: 0; }
.q-text { display: block; max-width: 340px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-weight: 600; }
.editor-card { position: sticky; top: 84px; }
.editor-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 24px; }
@media (max-width: 992px) {
  .bank-layout { grid-template-columns: 1fr; }
  .editor-card { position: static; }
}
</style>
