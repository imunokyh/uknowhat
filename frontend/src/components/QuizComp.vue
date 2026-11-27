<template>
  <div class="quiz-editor">
    <label class="uk-label">문제 유형</label>
    <div class="type-toggle">
      <button type="button" :class="{ on: selectedType === 'OX' }" @click="selectedType = 'OX'">⭕ OX 퀴즈</button>
      <button type="button" :class="{ on: selectedType === 'OB' }" @click="selectedType = 'OB'">🔢 4지선다</button>
    </div>

    <label class="uk-label mt-4">문제</label>
    <textarea
      v-model="questionText"
      class="uk-input question-input"
      placeholder="문제를 입력하세요"
      rows="3"
    ></textarea>

    <template v-if="selectedType === 'OX'">
      <label class="uk-label mt-4">정답 선택</label>
      <div class="ox-grid">
        <button
          v-for="(opt, idx) in optionsTf"
          :key="opt.value"
          type="button"
          class="uk-answer ox-pick"
          :class="[idx === 0 ? 'a2' : 'a1', { dim: selectedTf !== opt.value, correct: selectedTf === opt.value }]"
          @click="selectedTf = opt.value"
        >{{ opt.text }}</button>
      </div>
    </template>

    <template v-else-if="selectedType === 'OB'">
      <label class="uk-label mt-4">보기 입력 · 정답 보기를 눌러 선택하세요</label>
      <div class="ob-grid">
        <div
          v-for="(r, idx) in radios"
          :key="r.value"
          class="ob-item"
          :class="['a' + (idx + 1), { picked: radioSelected === r.value }]"
        >
          <button type="button" class="pick" :title="'정답으로 선택'" @click="radioSelected = r.value">
            {{ radioSelected === r.value ? '✓' : r.value }}
          </button>
          <input v-model="ans[r.value]" class="ob-input" :placeholder="r.value + '번 보기'" maxlength="500" />
        </div>
      </div>
    </template>

    <p v-else class="type-hint">먼저 문제 유형을 선택하세요.</p>
  </div>
</template>

<script>
export default {
  
  data() {
    return {
      questionId: "",
      questionText: "",
      // 인덱스 대입(ans[1] = ...)도 반응형으로 동작하도록 키를 미리 선언한 객체 사용
      ans: { 1: "", 2: "", 3: "", 4: "" },
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

<style scoped>
.quiz-editor { text-align: left; }
.type-toggle {
  display: inline-flex;
  background: #f1eef9;
  border-radius: 14px;
  padding: 4px;
  gap: 4px;
}
.type-toggle button {
  border: none;
  background: transparent;
  padding: 10px 18px;
  border-radius: 10px;
  font-weight: 800;
  color: var(--uk-text-sub);
  font-family: inherit;
  cursor: pointer;
}
.type-toggle button.on { background: #fff; color: var(--uk-primary); box-shadow: 0 2px 8px rgba(39, 16, 105, 0.12); }
.question-input {
  height: auto;
  padding: 14px 18px;
  resize: none;
  font-size: 18px;
  line-height: 1.5;
}
.ox-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.ox-pick { min-height: 110px; font-size: 56px; font-weight: 900; }
.ox-pick.dim { opacity: 0.35; }
.ob-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.ob-item {
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 14px;
  padding: 10px;
  transition: box-shadow 0.15s;
}
.ob-item.a1 { background: var(--uk-a1); }
.ob-item.a2 { background: var(--uk-a2); }
.ob-item.a3 { background: var(--uk-a3); }
.ob-item.a4 { background: var(--uk-a4); }
.ob-item.picked { box-shadow: 0 0 0 4px #fff, 0 0 0 7px var(--uk-text); }
.ob-item .pick {
  flex: none;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  border: none;
  background: rgba(255, 255, 255, 0.28);
  color: #fff;
  font-weight: 900;
  font-size: 18px;
  cursor: pointer;
}
.ob-item.picked .pick { background: #fff; color: var(--uk-a4-dark); }
.ob-input {
  flex: 1;
  min-width: 0;
  height: 44px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.92);
  padding: 0 12px;
  font-weight: 700;
  font-family: inherit;
  outline: none;
}
.type-hint { color: var(--uk-text-sub); margin-top: 20px; }
@media (max-width: 576px) {
  .ob-grid { grid-template-columns: 1fr; }
}
</style>
