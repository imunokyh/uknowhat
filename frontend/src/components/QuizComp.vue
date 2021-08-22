<template>
  <div>
    <!-- main content -->

    <!--
      data props
      https://question0.tistory.com/20
      https://sas-study.tistory.com/236
      -->

    <!-- s -->
    <div>
      <b-row>
        <b-col>
          <b-form-select
            v-model="selectedQuizType"
            :options="optionsQuizType"
          ></b-form-select>
        </b-col>
        <b-col
          ><b-form-select
            v-model="selectedTimeLimit"
            :options="optionsTimeLimit"
          ></b-form-select
        ></b-col>
        <b-col>
          <b-form-select
            v-model="selectedPoint"
            :options="optionsPoint"
          ></b-form-select
        ></b-col>
      </b-row>
    </div>
    <!-- e -->
    <label hidden>{{ quizid }}</label>
    <div>&nbsp;</div>
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
                v-model="check1"
                name="checkbox-1"
                value="1"
                unchecked-value="0"
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
                v-model="check2"
                name="checkbox-2"
                value="1"
                unchecked-value="0"
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
                v-model="check3"
                name="checkbox-3"
                value="1"
                unchecked-value="0"
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
                v-model="check4"
                name="checkbox-4"
                value="1"
                unchecked-value="0"
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
    <!-- e.main content -->
  </div>
</template>

<script>
export default {
  data() {
    return {
      quizid: "",
      question: "",
      ans1: "",
      ans2: "",
      ans3: "",
      ans4: "",
      check1: "0",
      check2: "0",
      check3: "0",
      check4: "0",
      quizType: "0",
      timeLimit: "0",
      point: "0",
      selectedQuizType: null,
      selectedTimeLimit: null,
      selectedPoint: null,
      optionsQuizType: [
        { value: null, text: "퀴즈타입" },
        { value: "0", text: "객관식" },
        { value: "1", text: "OX" },
      ],
      optionsTimeLimit: [
        { value: null, text: "시간제한" },
        { value: "20", text: "20초" },
        { value: "5", text: "5초" },
        { value: "0", text: "없음" },
      ],
      optionsPoint: [
        { value: null, text: "포인트" },
        { value: "0", text: "스탠다드" },
        { value: "1", text: "더블" },
        { value: "2", text: "없음" },
      ],
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
      this.quizid = "quizid-" + this.uuidv4();
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
      this.$emit("child-event", 100);
    },
  },
};
</script>

<style>
</style>