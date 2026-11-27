<template>
  <div class="uk-admin">
    <app-bar active="room"></app-bar>

    <div class="uk-container">
      <div class="uk-page-head">
        <div>
          <a class="uk-link" @click="goRoomMng()">← 내 퀴즈방</a>
          <h1 class="uk-page-title mt-1">{{ oRoomTitle || '퀴즈방 문제 구성' }}</h1>
          <p class="uk-page-sub">문제은행에서 문제를 골라 담고, 점수와 제한 시간을 정하세요.</p>
        </div>
        <div class="uk-actions">
          <button class="uk-btn light" @click="goProbBank()">📚 문제은행에서 새 문제 만들기</button>
        </div>
      </div>

      <div class="reg-layout">
        <!-- 이 방의 문제 -->
        <div class="uk-card">
          <div class="card-head">
            <h2 class="uk-section-title">🎯 이 방의 문제 <span class="uk-badge">{{ probCount }}</span></h2>
          </div>
          <div class="uk-table-wrap">
            <b-table
              hover
              ref="probtable"
              :items="probProvider"
              :fields="probFields"
              show-empty
              empty-text="오른쪽 문제은행에서 문제를 추가하세요"
            >
              <template #cell(questionOrder)="data">
                <span class="order">{{ data.index + 1 }}</span>
              </template>
              <template #cell(questionType)="data">
                <span class="uk-badge" :class="data.value === 'OX' ? 'ox' : 'ob'">{{ data.value === 'OX' ? 'OX' : '객관식' }}</span>
              </template>
              <template #cell(questionText)="data">
                <span class="q-text" :title="data.value">{{ data.value }}</span>
              </template>
              <template #cell(questionScore)="data">{{ data.value }}점</template>
              <template #cell(questionTime)="data">{{ data.value > 0 ? data.value + '초' : '∞' }}</template>
              <template #cell(actions)="data">
                <div class="row-actions">
                  <button class="uk-btn ghost sm" title="점수/시간 편집" @click.stop="editProb(data.item)">✏️</button>
                  <button class="uk-btn ghost sm" title="방에서 빼기" @click.stop="removeProb(data.item)">✕</button>
                </div>
              </template>
            </b-table>
          </div>
        </div>

        <!-- 문제은행 -->
        <div class="uk-card">
          <div class="card-head">
            <h2 class="uk-section-title">📚 문제은행</h2>
          </div>
          <form class="search" @submit.prevent="searchQuestion()">
            <b-form-select v-model="selectedSearchType" :options="optionsSearchType" class="type-select"></b-form-select>
            <b-form-input v-model="searchText" placeholder="문제 검색"></b-form-input>
            <button class="uk-btn sm" type="submit">검색</button>
          </form>
          <div class="uk-table-wrap bank-wrap">
            <b-table
              ref="banktable"
              :items="bankItems"
              :fields="bankFields"
              show-empty
              empty-text="검색 결과가 없어요"
            >
              <template #cell(questionType)="data">
                <span class="uk-badge" :class="data.value === 'OX' ? 'ox' : 'ob'">{{ data.value === 'OX' ? 'OX' : '객관식' }}</span>
              </template>
              <template #cell(questionText)="data">
                <span class="q-text" :title="data.value">{{ data.value }}</span>
              </template>
              <template #cell(add)="data">
                <button class="uk-btn success sm" @click="addRoomQuestion(data.item.questionId)">＋ 담기</button>
              </template>
            </b-table>
          </div>
        </div>
      </div>
    </div>

    <b-modal
      id="modal-prob-modify"
      ref="modal"
      title="문제 설정"
      ok-title="저장"
      cancel-title="취소"
      centered
      @show="resetModal"
      @hidden="resetModal"
      @ok="handleMdfyProb"
    >
      <form ref="formProb" @submit.stop.prevent="handleSubmit">
        <b-form-group label="점수">
          <b-form-select v-model="formProb.questionScore" :options="scoreOptions"></b-form-select>
        </b-form-group>
        <b-form-group label="제한 시간">
          <b-form-select v-model="formProb.questionTime" :options="timeOptions"></b-form-select>
        </b-form-group>
        <b-form-group label="순서 (작을수록 먼저 출제)" class="mb-0">
          <b-form-input v-model="formProb.questionOrder" type="number"></b-form-input>
        </b-form-group>
      </form>
    </b-modal>
  </div>
</template>

<script>
import {mapGetters, mapMutations, mapActions} from 'vuex'
import AppBar from "@/components/layout/AppBar.vue";
import { errorMessage } from "@/utils/gameSession";

export default {
  components: {
    AppBar,
  },
  props: {
    newRoomYn: {
      type: String,
      default: "", // Y/N
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
      formProb: {
        id:'',
        questionOrder:'',
        questionScore: '',
        questionTime: '',

      },
      oRoomId:'',
      oRoomTitle:'',
      timeOptions: [
        { value: 0, text: "제한 없음"},
        { value: 5, text: "5초"},
        { value: 10, text: "10초"},
        { value: 15, text: "15초"},
        { value: 20, text: "20초"},
        { value: 25, text: "25초"},
        { value: 30, text: "30초"},
      ],
      scoreOptions: [
        { value: 0, text: "없음"},
        { value: 100, text: "100점"},
        { value: 200, text: "200점"},
        { value: 300, text: "300점"},
        { value: 400, text: "400점"},
        { value: 500, text: "500점"},
        { value: 600, text: "600점"},
        { value: 700, text: "700점"},
        { value: 800, text: "800점"},
        { value: 900, text: "900점"},
        { value: 1000, text: "1000점"},
      ],
      searchText: "",
      selectedSearchType: null,
      optionsSearchType: [
        { value: null, text: "전체" },
        { value: "OX", text: "OX" },
        { value: "OB", text: "객관식" },
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
      //probItems: [],
      bankItems: [],
      probFields: [
        { key: "questionOrder", label: "#" },
        { key: "questionType", label: "유형" },
        { key: "questionText", label: "문제" },
        { key: "questionScore", label: "점수" },
        { key: "questionTime", label: "시간" },
        { key: "actions", label: "" },
      ],
      bankFields: [
        { key: "questionType", label: "유형" },
        { key: "questionText", label: "문제" },
        { key: "add", label: "" },
      ],
      probCount: 0,
      maxQuizOrder: 0,
    };
  },
  created() {
    //this.genQuizId();

    if(this.roomId != -1){ // refresh 인경우값
      this.setRoomId(this.roomId);
    }

    this.searchQuestion();
  },
  computed: {
    test1(){
      //return this.$store.getters['roomStore']
      return 0
    },
    ...mapGetters([
      'getRoomId'
    ])
  },
  mounted() {
    this.oRoomId = this.roomId;
    this.oRoomTitle = this.roomTitle || sessionStorage.getItem("probRegRoomTitle") || "";
    if (this.roomTitle) sessionStorage.setItem("probRegRoomTitle", this.roomTitle);
    // this.$root.$on('bv::modal::show', (bvEvent, modalId) => {
    //   if (bvEvent.type == 'show' && modalId == 'modal-prob-modify' ){
    //     console.log('event:' + this.formProb.id);
    //     if(this.formProb.id == null || this.formProb.id == ''){
    //       bvEvent.preventDefault();
    //       alert('편집할 문제를 선택해 주세요');
    //       return;
    //     }
    //   }
    // })

    if (this.newRoomYn == 'Y') {

    } else if(this.newRoomYn == 'N') {

    } else{
    }

  },
  methods: {
    goRoomMng(){
      // 방관리로 이동
      this.$router.push({ name: "RoomMng" });
    },
    goProbBank(){
      // 문제은행으로 이동
      this.$router.push({ name: "ProbBank" });
    },
    setRoomId(roomId){
      this.$store.commit('updateRoomId', roomId);
    },
    ...mapMutations([
      'updateRoomId'
    ]),
    ...mapActions([
      'asyncUpdateRoomId'
    ]),
    initProbForm(){
      /* 폼의값 초기화 */
      this.formProb.id = '';
      this.formProb.questionOrder = '';
      this.formProb.questionScore = '';
      this.formProb.questionTime = '';
    },
    resetModal(){

    },
    handleMdfyProb(bvModalEvt) {
      // Prevent modal from closing
      bvModalEvt.preventDefault();
      // Trigger submit handler
      this.handleSubmit();
    },
    handleSubmit() {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };

      this.$http.put("/api/v1/rq", {
          roomId: this.getRoomId,
          id : this.formProb.id,
          questionScore: this.formProb.questionScore,
          questionTime : this.formProb.questionTime,
          questionOrder: this.formProb.questionOrder

      },config).then((res) => {
        // Hide the modal manually
        this.$nextTick(() => {
          this.$bvModal.hide("modal-prob-modify");
        });
        this.$refs.probtable.refresh();

      }).catch((error) => {
        console.log(error);
        alert(errorMessage(error, "문제 설정 저장 중 오류가 발생했습니다."));
      });
    },
    editProb(item) {
      this.formProb.id = item.id;
      this.formProb.questionScore = item.questionScore;
      this.formProb.questionTime = item.questionTime;
      this.formProb.questionOrder = item.questionOrder;
      this.$bvModal.show("modal-prob-modify");
    },
    removeProb(item) {
      this.formProb.id = item.id;
      this.delProb();
    },
    delProb() {
      if(this.formProb.id == null || this.formProb.id == ''){

        alert('삭제할 문제를 선택해 주세요');
        return;
      }
      /* 방문제삭제*/
      if(confirm("문제를 삭제하시겠습니까?")!=true){
        return;
      }
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      this.$http({
        method: "delete",
        url: "/api/v1/rq/" + this.formProb.id,
        headers: config.headers
      }).then((res) => {
        if (res.data.code == 0) {
          this.$refs.probtable.refresh();
          this.initProbForm();
        } else {
          alert("삭제시 문제가 발생했습니다");
        }
      }).catch((error) => {
        console.log(error);
        alert(errorMessage(error, "삭제 중 오류가 발생했습니다."));
      });

    },
    addRoomQuestion(questionId) {
      /* 방문제에 추가 */
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      let roomId = this.getRoomId;
      let questionTime = 10;
      let questionScore = 100;
      // 기존 문제들 뒤에 추가 (페이지를 다시 열어도 순서가 겹치지 않도록)
      let questionOrder = ++this.maxQuizOrder;
      this.$http({
        method: "post",
        url: "/api/v1/rq",
        data: {
          roomId: roomId,
          questionId: questionId,
          questionTime: questionTime,
          questionScore: questionScore,
          questionOrder: questionOrder,

        },
        headers: config.headers
      }).then((res) => {
        if (res.data.code == 0) {
          this.$refs.probtable.refresh();
          this.initProbForm();
        } else {
          alert("문제 추가시 문제가 발생했습니다");
        }
      }).catch((error) => {
        console.log(error);
        alert(errorMessage(error, "문제 추가 중 오류가 발생했습니다."));
      });
    },
    probProvider(ctx) {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
        params: {
          roomId: this.getRoomId,
        }
      };
      //console.log(sessionStorage.getItem("token"))
      let promise = this.$http.get("/api/v1/rq",config);
      return promise.then((res) => {
        const list = res.data.result || [];
        this.probCount = list.length;
        this.maxQuizOrder = list.reduce((max, q) => Math.max(max, Number(q.questionOrder) || 0), 0);
        return list;
      }).catch((error) => {
        console.log(error);
        if (error.response && error.response.status === 401) {
          alert("로그인이 필요합니다.");
          this.$router.replace({ name: "Login" });
        }
        return [];
      });
    },
    onProbSelected(items) {
      console.log(items);
      if (items == null || items[0] == null) {
        console.log('empty')
        this.initProbForm();
        return;
      }
      let ps = items[0]; // problem selected
      console.log('prob selected');
      console.log(ps);

      this.formProb.id = ps.id;
      this.formProb.questionScore = ps.questionScore
      this.formProb.questionTime = ps.questionTime
      this.formProb.questionOrder = ps.questionOrder
    },
    onBankSelected(items) {
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
    },
    bankProvider(ctx) {
      let promise = this.$http.get("/api/v1/question/me", {
        params: {
          searchType: "",
          searchText: "",
        },
      },config);
      return promise.then((res) => {
        return res.data.result.content || [];
      });
    },
    searchQuestion() {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
        params: {
          page: 0,
          size: 100000,
          searchType: this.selectedSearchType,
          searchText: this.searchText,
        },
      };
      let promise = this.$http.get("/api/v1/question/me", config);
      promise.then((res) => {
        this.bankItems = [];
        let result = res.data.result.content || [];

        result.forEach((item) => {
          this.bankItems.push(item);
        });

      }).catch((error) => {
        console.log(error);
      });
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

    parentReceive(val) {
      console.log("parentReceive");
    },
  },
};
</script>

<style scoped>
.reg-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(0, 1fr);
  gap: 20px;
  align-items: start;
}
.card-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.card-head .uk-section-title { margin: 0; }
.order {
  display: inline-flex; width: 28px; height: 28px; border-radius: 8px;
  align-items: center; justify-content: center;
  background: var(--uk-primary-soft); color: var(--uk-primary); font-weight: 800;
}
.q-text { display: block; max-width: 260px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-weight: 600; }
.row-actions { display: flex; gap: 2px; justify-content: flex-end; }
.row-actions .uk-btn { padding: 0 10px; }
.search { display: flex; gap: 8px; margin-bottom: 14px; }
.search .type-select { width: 120px; flex: none; }
.bank-wrap { max-height: 560px; overflow-y: auto; }
@media (max-width: 992px) {
  .reg-layout { grid-template-columns: 1fr; }
}
</style>
