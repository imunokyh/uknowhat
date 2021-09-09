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
            v-model="selectedType"
            :options="optionsType"
          ></b-form-select>
        </b-col>
       
      </b-row>
    </div>
    <!-- e -->
    <!--
    <label >{{ quizId }}</label>-->
    <div>&nbsp;</div>
    <div>
      <b-form-input
        v-model="questionText"
        placeholder="질문을 입력하세요"
      ></b-form-input>
      <div>&nbsp;</div>
      <div>&nbsp;</div>

      <!--s.tfprob -->
      <b-row v-show="selectedType == 'OX'">
        <b-form-group label="참-거짓 선택" v-slot="{ ariaDescribedby }">
          <b-form-radio-group
            v-model="selectedTf"
            :options="optionsTf"
            :aria-describedby="ariaDescribedby"
            name="radio-inline"
          ></b-form-radio-group>
        </b-form-group>
      </b-row>
      <!--e.tfprob-->

      <b-row v-show="selectedType == 'OB'">
        <!-- radio -->
        <div v-for="inputRadio in radios" class="form-check form-group">
          <input
            class="form-check-input"
            v-model="radioSelected"
            type="radio"
            :value="inputRadio.value"
            :id="inputRadio.value"
          />
          <label class="form-check-label" :for="inputRadio.value">
            {{ inputRadio.label }}
          </label>
          <b-form-textarea
            v-model="ans[inputRadio.value]"
            placeholder="답을 입력하세요"
            rows="2"
          ></b-form-textarea>
          <br />
        </div>

        <!-- e.radio -->
      </b-row>
    </div>
    <!-- debug
    selected is: {{ selectedTf }} / {{ radioSelected }}
    -->
    <!-- e.main content -->
  </div>
</template>

<script>
export default {
  
  data() {
    return {
      radios: [
        { value: "1", label: "1번답" },
        { value: "2", label: "2번답" },
        { value: "3", label: "3번답" },
        { value: "4", label: "4번답" },
      ],
      radioSelected: null,
      questionId: "",
      questionText: "",
      ans: [],
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

      selectedTf: "1",
      optionsTf: [
        { text: "참", value: "1" },
        { text: "거짓", value: "2" },
      ],

      selectedType: null,
      selectedTimeLimit: null,
      selectedPoint: null,
      optionsType: [
        { value: null, text: "퀴즈타입" },
        { value: "OX", text: "OX" },
        { value: "OB", text: "객관식" },
      ],
      optionsTimeLimit: [
        { value: null, text: "시간제한" },
        { value: "20", text: "20초" },
        { value: "5", text: "5초" },
        { value: "0", text: "없음" },
      ],
      optionsPoint: [
        { value: null, text: "포인트" },
        { value: "S", text: "스탠다드" },
        { value: "D", text: "더블" },
        { value: "N", text: "없음" },
      ],
    };
  },
  watch: {
    selectedQuestion: function (newVal, oldVal) {
      if (newVal == "OX") {
        showTfTypeInput();
      } else if (newVal == "OB") {
        showObjTypeInput();
      } else {
      }
    },
  },

  created() {
    //this.genQuizId();
  },
  computed: {},
  mounted() {
    //this.genQuizId();
  },
  methods: {
    showTfTypeInput() {},
    showObjTypeInput() {},

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
      console.log(this.ans1);
      this.$emit("child-event", 100);
    },
  },
};
</script>

<style>
</style>