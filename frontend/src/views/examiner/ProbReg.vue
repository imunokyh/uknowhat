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
          <!-- Title:{{ roomTitle }} -->

          <b-row>
            <b-col sm="2">
              <!-- edit -->
              <b-button v-b-modal.modal-prob-modify>편집</b-button>
              <b-modal
                id="modal-prob-modify"
                ref="modal"
                title="문제 정보 편집"
                @show="resetModal"
                @hidden="resetModal"
                @ok="handleMdfyProb"
              >
                <form ref="formProb" @submit.stop.prevent="handleSubmit">
                  
                   <b-row >
                    
                    <b-col sm="3">아이디</b-col>
                    <b-col sm="9">
                      <b-form-input id="rq-id" v-model="formProb.id" disabled>
                    </b-form-input>
                    </b-col>
                  </b-row>

                  <b-row class="my-1">
                    
                    <b-col sm="3">점수</b-col>
                    <b-col sm="9">
                      <b-form-input
                      id="question-score-input"
                      v-model="formProb.questionScore" type="number"
                    ></b-form-input>
                    </b-col>
                  </b-row>

                  <b-row class="my-1">
                    
                    <b-col sm="3">시간</b-col>
                    <b-col sm="9">
                      <b-form-input
                      id="question-time-input"
                      v-model="formProb.questionTime" type="number"
                    ></b-form-input>
                    </b-col>
                  </b-row>

                  <b-row class="my-1">
                    
                    <b-col sm="3">순서</b-col>
                    <b-col sm="9">
                      <b-form-input
                      id="question-time-input"
                      v-model="formProb.questionOrder" type="number"
                    ></b-form-input>
                    </b-col>
                  </b-row>
                    
                    
                    
                </form>
              </b-modal>
              <!-- e.edit-->
            </b-col>
            <b-col sm="2">
              <b-button @click="delProb()">삭제</b-button>
            </b-col>
            <!--
            <b-col sm="4"> roomId: {{ fakeRoomId }}/ probId:{{}} </b-col>-->
          </b-row>
          <br />
          <b-row class="overflow-auto" style="height: 400px">
            <b-table
              selectable
              select-mode="single"
              @row-selected="onProbSelected"
              striped
              hover
              ref="probtable"
              :items="probProvider"
              :fields="probFields"
            ></b-table>
          </b-row>
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
              <b-button @click="searchQuestion()">검색</b-button>
            </b-col>
            <b-col sm="2">
              <b-button @click="addRoomQuestion()">방문제추가</b-button>
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
              :items="bankItems"
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
      formProb: {
        id:'',
        questionOrder:'',
        questionScore: '',
        questionTime: '',

      },
      
      fakeRoomId: 11,
      searchText: "",
      selectedSearchType: null,
      optionsSearchType: [
        { value: null, text: "문제타입" },
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
        { key: "questionOrder", label: "순서" },
        { key: "questionType", label: "문제유형" },
        { key: "questionText", label: "문제내용" },
        { key: "questionScore", label: "점수" },
        { key: "questionTime", label: "시간" },
      ],
      bankFields: [{ key: "questionText", label: "내용" }],
    };
  },
  created() {
    this.genQuizId();
  },
  computed: {},
  mounted() {
    
    this.$root.$on('bv::modal::show', (bvEvent, modalId) => {
      //console.log(bvEvent)
      //console.log(bvEvent.type );
      //console.log(modalId );
       if (bvEvent.type == 'show' && modalId == 'modal-prob-modify' ){
        
         if(this.formProb.id == null || this.formProb.id == ''){
            bvEvent.preventDefault();
            alert('편집할 문제를 선택해 주세요');
            return;
         }
       }
      
      //console.log('Modal is about to be shown', bvEvent, modalId);
    })

    if (this.newRoom == 1) {
      console.log("new prob group");
    } else {
      console.log("modify prob group");
    }
    //this.roomId = 1;
    //this.roomTitle = "test";
  },
  methods: {
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
      // Exit when the form isn't valid
      // if (!this.checkFormValidity()) {
      //   return;
      // }
      // score, time, order
      let _roomId = 11;
      this.$http.put("/api/v1/rq", {
          roomId: _roomId,
          id : this.formProb.id,
          questionScore: this.formProb.questionScore,
          questionTime : this.formProb.questionTime,
          questionOrder: this.formProb.questionOrder

      }).then((res) => {
        // Hide the modal manually
        this.$nextTick(() => {
          this.$bvModal.hide("modal-prob-modify");
        });
        this.$refs.probtable.refresh();
        
      });
    },
    delProb() {},
    addRoomQuestion() {
      /* 방문제에 추가 */
      let roomId = this.fakeRoomId;
      let questionId = this.$refs.quizdata.questionId;
      let questionTime = 5;
      let questionScore = 10;
      let questionOrder = 0;
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
      }).then((res) => {
        if (res.data.code == 0) {
          this.$refs.probtable.refresh();
          this.initProbForm();
        } else {
          alert("저장시 문제가 발생했습니다");
        }
      });
      // this.probItems.push({
      //   questionType: questionType,
      //   questionText: questionText,
      // });
    },
    probProvider(ctx) {
      let promise = this.$http.get("/api/v1/rq", {
        params: {
          roomId: this.fakeRoomId,
        },
      });
      return promise.then((res) => {
        //console.log(res.data.result)
        return res.data.result || [];
      });
    },
    onProbSelected(items) {
      //console.log('probselected');
      //console.log(items);
      if (items == null || items[0] == null) {
        return;
      }
      let ps = items[0]; // problem selected
      
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
      let promise = this.$http.get("/api/v1/question", {
        params: {
          searchType: "",
          searchText: "",
        },
      });
      return promise.then((res) => {
        return res.data.result.content || [];
      });
    },
    searchQuestion() {
      let promise = this.$http.get("/api/v1/question", {
        params: {
          searchType: this.selectedSearchType,
          searchText: this.searchText,
        },
      });
      promise.then((res) => {
        this.bankItems = [];
        let result = res.data.result.content || [];

        result.forEach((item) => {
          this.bankItems.push(item);
        });

        this.$refs.banktable.refresh();
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