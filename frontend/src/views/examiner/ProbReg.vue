<template>
  <div >
    <b-container>
      <br>
      
      <b-row cols="3">
        <b-col>
          <b-button class="col-sm-4" @click="goRoomMng()">방 관리로</b-button>
        </b-col>
        <b-col>
          <h2>{{oRoomTitle}}</h2>
        </b-col>
        <b-col>
          <b-button class="col-sm-4" @click="goProbBank()">문제은행</b-button>
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

      <b-row><!--left-->

        <b-col cols="6" class="vl">
          <!-- Title:{{ roomTitle }} -->

          <b-row style="height: 5%;">
            <b-col sm="2">
              <!-- edit -->
              <b-button variant="info" v-b-modal.modal-prob-modify>편집</b-button>
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
                      <b-form-select
                        id="question-score-input"
                        v-model="formProb.questionScore"
                        :options="scoreOptions"
                      ></b-form-select>
                      <!--
                      <b-form-input
                      id="question-score-input"
                      v-model="formProb.questionScore" type="number"
                      ></b-form-input>
                      -->
                    </b-col>
                  </b-row>

                  <b-row class="my-1">
                    
                    <b-col sm="3">시간</b-col>
                    <b-col sm="9">
                      <b-form-select
                        id="question-time-input"
                        v-model="formProb.questionTime"
                        :options="timeOptions"
                      ></b-form-select>
                      <!--
                      <b-form-input
                      id="question-time-input"
                      v-model="formProb.questionTime" type="number"
                      ></b-form-input>
                      -->
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
              <b-button variant="danger" @click="delProb()">삭제</b-button>
            </b-col>
            
          </b-row>
          <br />
          <b-row class="overflow-auto" style="height: 90%;">
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
          <b-row style="height: 5%;">
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
              <b-button variant="primary" @click="searchQuestion()">검색</b-button>
            </b-col>
            <b-col sm="2">
              <b-button variant="success" @click="addRoomQuestion()">추가</b-button>
            </b-col>
          </b-row>
          <br />
          <b-row class="overflow-auto" style="height: 90%;">
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

      <b-row>&nbsp;</b-row>

      <b-row>
        <b-col cols="12">
          <quiz-comp ref="quizdata"></quiz-comp>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import QuizComp from "@/components/QuizComp";
import {mapGetters, mapMutations, mapActions} from 'vuex'

export default {
  components: {
    QuizComp,
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
        { value: 0, text: "없음"},
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
      incrQuizOrder: 0,
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
    this.oRoomTitle = this.roomTitle
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
        
      });
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
      });

    },
    addRoomQuestion() {
      /* 방문제에 추가 */
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      let roomId = this.getRoomId;
      let questionId = this.$refs.quizdata.questionId;
      let questionTime = 10;
      let questionScore = 100;
      let questionOrder = this.incrQuizOrder++;
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
        return res.data.result || [];
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
      let promise = this.$http.get("/api/v1/question", {
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
      let promise = this.$http.get("/api/v1/question", config);
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

<style>
.vl {
  border-bottom: 1px solid gray;
  border-left: 1px solid gray;
  border-right: 1px solid gray;
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