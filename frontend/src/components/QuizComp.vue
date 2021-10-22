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
        <b-col sm="12">
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
      <b-row>
        <b-col sm="12">
          <b-form-textarea
            v-model="questionText"
            placeholder="문제를 입력하세요" rows=2
            no-resize
          ></b-form-textarea>
        </b-col>
      </b-row>

      <div>&nbsp;</div>

      <!--s.tfprob -->
      <b-row v-show="selectedType == 'OX'"> 
        <div v-for="(inputTf, idx) in optionsTf" :key="idx" class="form-check form-group col-sm-6">
          <input
            class="form-check-input"
            v-model="selectedTf"
            type="radio"
            :value="inputTf.value"
            :id="inputTf.value"
          >
          <div>&nbsp;</div>
          <b-button
            :class="backgroundColor[idx]"
            class="col-sm-12"
            size="lg"
            style="text-align: center; border: none;"
          >{{inputTf.text}}</b-button>
          <!--
          <b-form-textarea
            :class="backgroundColor[idx]"
            style="text-align: center;"
            v-model="ans[inputTf.value]"
            placeholder="답을 입력하세요"
            rows="1"
            no-resize
          ></b-form-textarea>
          -->
        </div>
       

        <!--
        <b-form-group v-slot="{ ariaDescribedby }">
          <b-form-radio-group
            v-model="selectedTf"
            :options="optionsTf"
            :aria-describedby="ariaDescribedby"
            name="radio-inline"
          ></b-form-radio-group>
        </b-form-group>
        -->
      </b-row>
      <!--e.tfprob-->

      <b-row v-show="selectedType == 'OB'">
        <!-- radio -->
        <div v-for="(inputRadio, idx) in radios" :key="idx" class="form-check form-group col-sm-3">
          <input
            class="form-check-input"
            v-model="radioSelected"
            type="radio"
            :value="inputRadio.value"
            :id="inputRadio.value"
          />
          <div>&nbsp;</div>
          <b-form-textarea
            :class="backgroundColor[idx]"
            class="obans"
            style="text-align: center;"
            v-model="ans[inputRadio.value]"
            placeholder="답을 입력하세요"
            rows="2"
            no-resize
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
      questionId: "",
      questionText: "",
      ans: [],
      selectedTf: "1",
      optionsTf: [
        { value: "1", text: "O" },
        { value: "2", text: "X" },
      ],
      radioSelected: null,
      radios: [
        { value: "1", label: "" },
        { value: "2", label: "" },
        { value: "3", label: "" },
        { value: "4", label: "" },
      ],
      selectedType: null,
      optionsType: [
        { value: null, text: "퀴즈타입" },
        { value: "OX", text: "OX" },
        { value: "OB", text: "객관식" },
      ],
      backgroundColor: [
        "bg-primary text-white",
        "bg-danger text-white",
        "bg-success text-white",
        "bg-warning text-white"
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
textarea.obans::placeholder {
  color: white;
  text-align: center;
}

textarea.obans::-webkit-input-placeholder {
  color: white;
  text-align: center;
}

textarea.obans::-ms-input-placeholder {
  color: white;
  text-align: center;
}

textarea.obans::-moz-placeholder {
  color: white;
  text-align: center;
}
</style>