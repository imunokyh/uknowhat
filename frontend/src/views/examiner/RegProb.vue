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
          <label hidden>{{ quizid }}</label>
          <div>
            <b-form-input
              v-model="question"
              placeholder="질문을 입력하세요"
            ></b-form-input>
            <div>&nbsp;</div>
            <div>&nbsp;</div>
            <b-row>
              <b-col>
                <!-- check -->
                <b-row>
                  <b-col cols="1">
                    <b-form-checkbox
                      id="checkbox-1"
                      v-model="status1"
                      name="checkbox-1"
                      value="accepted"
                      unchecked-value="not_accepted"
                    >
                    </b-form-checkbox>
                  </b-col>
                  <b-col cols="11">
                    <b-form-textarea
                      v-model="ans1"
                      placeholder="1번 답을 입력하세요"
                      rows="2"
                    ></b-form-textarea>
                  </b-col>
                </b-row>
                <!-- e.check -->
              </b-col>
              <b-col>
                <b-row>
                  <b-col cols="1">
                    <b-form-checkbox
                      id="checkbox-2"
                      v-model="status2"
                      name="checkbox-2"
                      value="accepted"
                      unchecked-value="not_accepted"
                    >
                    </b-form-checkbox>
                  </b-col>
                  <b-col cols="11">
                    <b-form-textarea
                      v-model="ans2"
                      placeholder="2번 답을 입력하세요"
                      rows="2"
                    ></b-form-textarea>
                  </b-col>
                </b-row>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <b-row>
                  <b-col cols="1">
                    <b-form-checkbox
                      id="checkbox-3"
                      v-model="status3"
                      name="checkbox-3"
                      value="accepted"
                      unchecked-value="not_accepted"
                    >
                    </b-form-checkbox>
                  </b-col>
                  <b-col cols="11">
                    <b-form-textarea
                      v-model="ans3"
                      placeholder="3번 답을 입력하세요"
                      rows="2"
                    ></b-form-textarea>
                  </b-col>
                </b-row>
              </b-col>
              <b-col>
                <b-row>
                  <b-col cols="1">
                    <b-form-checkbox
                      id="checkbox-4"
                      v-model="status4"
                      name="checkbox-4"
                      value="accepted"
                      unchecked-value="not_accepted"
                    >
                    </b-form-checkbox>
                  </b-col>
                  <b-col cols="11">
                    <b-form-textarea
                      v-model="ans4"
                      placeholder="4번 답을 입력하세요"
                      rows="2"
                    ></b-form-textarea>
                  </b-col>
                </b-row>
              </b-col>
            </b-row>
          </div>
        </b-col>
        <!-- e.main content -->
        <b-col cols="2" class="vl">
          <div>
            <b-dropdown text="퀴즈타입" variant="outline-primary" class="m-2">
              <b-dropdown-item href="#">객관식</b-dropdown-item>
              <b-dropdown-item href="#">OX</b-dropdown-item>
            </b-dropdown>

            <b-dropdown text="시간제한" variant="outline-primary" class="m-2">
              <b-dropdown-item href="#">20</b-dropdown-item>
              <b-dropdown-item href="#">10</b-dropdown-item>
              <b-dropdown-item href="#">0</b-dropdown-item>
            </b-dropdown>

            <b-dropdown text="포인트" variant="outline-primary" class="m-2">
              <b-dropdown-item href="#">스탠다드</b-dropdown-item>
              <b-dropdown-item href="#">더블</b-dropdown-item>
              <b-dropdown-item href="#">없음</b-dropdown-item>
            </b-dropdown>
          </div>
          <b-row>&nbsp;</b-row>
          <b-row>&nbsp;</b-row>
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
export default {
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